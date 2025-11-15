package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import com.cmsr.onebase.module.flow.api.FlowProcessExecApiImpl;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.ProcessedSubEntityVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.data.transaction.TransactionState;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

@Slf4j
@Component
public class MetadataDataMethodUpdateImpl extends AbstractMetadataDataMethodCoreService {

    @Autowired
    private FlowProcessExecApiImpl flowProcessExecApi;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    MetadataDataMethodSubEntityCrudImpl metadataDataMethodSubEntityCrudImpl;

    /**
     * 校验更新数据
     */
    protected void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 将字段ID转换为字段名后再校验
        Map<String, Object> convertedData = convertFieldIdToFieldName(data, fields);
        
        // 更新时不校验必填，只校验数据类型等
        for (Map.Entry<String, Object> entry : convertedData.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            if (field == null) {
                throw exception(ENTITY_FIELD_NOT_EXISTS, fieldName);
            }

            // 不允许更新主键字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsPrimaryKey())) {
                throw invalidParamException("不允许更新主键字段");
            }

            // 不允许更新自动编号字段
            if (autoNumberService.hasAutoNumber(field.getId())) {
                throw invalidParamException("不允许更新自动编号字段[{}]", field.getDisplayName());
            }
        }
    }

    /**
     * 处理更新数据
     */
    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 将字段ID转换为字段名
        Map<String, Object> convertedData = convertFieldIdToFieldName(data, fields);
        Map<String, Object> processedData = new HashMap<>();

        for (Map.Entry<String, Object> entry : convertedData.entrySet()) {
            String fieldName = entry.getKey();
            MetadataEntityFieldDO field = fields.stream()
                    .filter(f -> f.getFieldName().equals(fieldName))
                    .findFirst()
                    .orElse(null);

            // 只处理非主键和非系统字段 - 使用新的枚举值：1-是，0-否
            if (field != null &&
                    !BooleanStatusEnum.isYes(field.getIsPrimaryKey()) &&
                    !BooleanStatusEnum.isYes(field.getIsSystemField())) {
                processedData.put(fieldName, entry.getValue());
            }
        }

        // 设置更新时间
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = dateTime.format(dateTimeFormatter);
        processedData.put("updated_time",now);

        // 处理复杂类型字段（数组、对象等）的JSON序列化
        processComplexTypeFields(fields, processedData);

        return processedData;
    }

    /**
     * 处理复杂类型字段的JSON序列化
     * 对于数组和对象类型的字段值，需要序列化为JSON字符串存储到数据库
     *
     * @param fields 字段列表
     * @param processedData 待处理的数据
     */
    private void processComplexTypeFields(List<MetadataEntityFieldDO> fields, Map<String, Object> processedData) {
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            String fieldType = field.getFieldType();
            
            if (fieldName == null || fieldType == null) {
                continue;
            }
            
            Object fieldValue = processedData.get(fieldName);
            if (fieldValue == null) {
                continue;
            }
            
            // 判断是否需要JSON序列化的字段类型
            if (needsJsonSerialization(fieldType, fieldValue)) {
                try {
                    // 将复杂对象序列化为JSON字符串
                    String jsonString = JsonUtils.toJsonString(fieldValue);
                    processedData.put(fieldName, jsonString);
                    log.debug("字段 {} (类型: {}) 的值已序列化为JSON: {}", fieldName, fieldType, jsonString);
                } catch (Exception e) {
                    log.error("字段 {} 的值序列化为JSON失败: {}", fieldName, e.getMessage(), e);
                    // 序列化失败时，保持原值
                }
            }
        }
    }

    /**
     * 判断字段类型是否需要JSON序列化
     * 
     * @param fieldType 字段类型
     * @param fieldValue 字段值
     * @return 是否需要序列化
     */
    private boolean needsJsonSerialization(String fieldType, Object fieldValue) {
        if (fieldType == null) {
            return false;
        }
        
        String upperFieldType = fieldType.toUpperCase();
        
        // 字段类型包含以下关键字的需要JSON序列化
        boolean isComplexType = upperFieldType.contains("SELECT") ||       // 选择类型（包括SELECT、MULTI_SELECT、DATA_SELECTION等）
                                upperFieldType.contains("MULTI") ||        // 多选类型（包括MULTI_USER、MULTI_DEPARTMENT等）
                                upperFieldType.contains("ADDRESS") ||       // 地址类型
                                upperFieldType.contains("FILE") ||          // 文件附件
                                upperFieldType.contains("ATTACHMENT") ||    // 附件
                                upperFieldType.contains("IMAGE") ||         // 图片
                                upperFieldType.contains("USER") ||          // 人员选择（包括USER、MULTI_USER）
                                upperFieldType.contains("DEPARTMENT") ||    // 部门选择（包括DEPARTMENT、MULTI_DEPARTMENT）
                                upperFieldType.contains("DATA") ||          // 数据选择（包括DATA_SELECTION、MULTI_DATA_SELECTION）
                                upperFieldType.contains("GEOGRAPHY") ||     // 地理位置
                                upperFieldType.contains("GEO") ||           // 地理位置（简写）
                                upperFieldType.equals("JSONB") ||           // JSONB类型
                                upperFieldType.equals("JSON");              // JSON类型
        
        // 同时判断值是否为复杂对象（List或Map）
        boolean isComplexValue = fieldValue instanceof List || fieldValue instanceof Map;
        
        return isComplexType && isComplexValue;
    }

    /**
     * 更新操作的数据存储
     *
     * @param context 处理上下文
     */
    @Override
    protected void storeData(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        Map<String, Object> processedData = context.getProcessedData();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();
        Object id = context.getId();

        applyFieldStorageStrategies(processedData, fields);

        TenantUtils.executeIgnore(() -> {
            // 1. 校验数据存在
            validateDataExistsWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);

            // 2. 获取主键字段名
            String primaryKeyField = getPrimaryKeyFieldName(fields);

            // 3. 构建更新条件
            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(primaryKeyField, id);

            // 4. 执行更新
            DataRow dataRow = new DataRow(processedData);

            // AnyLine开启事务
            TransactionState transactionState = temporaryService.start();

            long updateCount = temporaryService.update(quoteTableName(entity.getTableName()), dataRow, configStore);
            log.info("更新数据成功，实体ID: {}, 表名: {}, 更新记录数: {}", entityId, entity.getTableName(), updateCount);
            try {
                super.storeData(context);// 子表处理创建嵌套内部事务
                log.info("子表处理完成，准备提交事务");
                // 子表处理完成 提交事务
                temporaryService.commit(transactionState);
            }catch (Exception e){
                log.info("子表处理出现异常，准备回滚事务：{}",e.getMessage());
                // 子表处理出现异常 回滚事务
                temporaryService.rollback(transactionState);
                throw exception(DB_SUBENTITY_OPERATION_ERROR,e.getMessage());
            }

            return null;
        });
    }

    @Override
    protected void handleSubEntities(ProcessContext context) {
        // 已经插入到数据库的父表数据行
        Map parentData = context.getProcessedData();

        // 关联关系
        Long partentEntityId = context.getEntityId();
        List<MetadataEntityRelationshipDO> relationshipDOS = entityRelationshipRepository.getRelationshipsByEntityId(partentEntityId);

        //查询子表和主表的关联字段 构建关联条件
        List<MetadataDataMethodSubEntityContext> subEntityVos = context.getSubEntities();
        for(MetadataDataMethodSubEntityContext subEntityContext: subEntityVos) {
            Long subEntityId = subEntityContext.getEntityId();
            List<Map<Long, Object>> subData = subEntityContext.getSubData();

            String parentRelFieldId = relationshipDOS.stream().filter(relationshipDO ->
                            (subEntityId).equals(relationshipDO.getTargetEntityId())).
                    map(MetadataEntityRelationshipDO::getSourceFieldId).findFirst().orElse(null);
            MetadataEntityFieldDO parentEntityFieldDO = entityFieldRepository.findById(Long.valueOf(parentRelFieldId));
            String parentFiledName = parentEntityFieldDO.getFieldName();// 主表关联字段名称


            String subRelFieldId = relationshipDOS.stream().filter(relationshipDO ->
                            (subEntityId).equals(relationshipDO.getTargetEntityId())).
                    map(MetadataEntityRelationshipDO::getTargetFieldId).findFirst().orElse(null);
            MetadataEntityFieldDO subEntityFieldDO = entityFieldRepository.findById(Long.valueOf(subRelFieldId));
            String subRelFieldName = subEntityFieldDO.getFieldName();// 子表关联字段名称

            Object parentValue = new Object();
            if("parent_id".equals(subRelFieldName)){
                parentValue = context.getId();
            }else{
                parentValue = parentData.get(parentFiledName);
            }

            List<MetadataEntityFieldDO> subEntityFields = getEntityFields(subEntityId);
            MetadataBusinessEntityDO subEntity = validateEntityExists(subEntityId);

            List<MetadataEntityFieldDO> subFields = entityFieldRepository.getEntityFieldListByEntityId(subEntityId);
            String primaryKeyFieldName = getPrimaryKeyFieldName(subFields);// 子表的主键字段名

            List<String> processedIds = new ArrayList<String>();// 存放新增数据的id，修改已有数据的id集合

            Long menuId = context.getRequestContext().getMenuId();

            // 逐条插入子表数据
            for (Map<Long, Object> row : subData) {
                // key类型：Long 转 String
                Map covertedRow = row.entrySet().stream().collect(Collectors.toMap(
                        entry ->entry.getKey().toString(),
                        Map.Entry::getValue));

                // id：value 转 name：value
                Map<String,Object> nameValueParis = convertFieldIdToFieldName(covertedRow,subEntityFields);

                boolean containsPrimaryKey = nameValueParis.entrySet().stream().anyMatch(entry ->
                        entry.getKey().matches(primaryKeyFieldName));// 是否包括主键字段

                if(containsPrimaryKey){
                    // 执行更新
                    Object primaryKeyFieldValue = nameValueParis.get(primaryKeyFieldName);
                    nameValueParis.remove(primaryKeyFieldName);// 更新操作不能传id主键字段

                    ProcessedSubEntityVo processedSubEntityVo = new ProcessedSubEntityVo();
                    processedSubEntityVo.setTraceId(context.getTraceId());
                    processedSubEntityVo.setSubEntityId(subEntityId);
                    processedSubEntityVo.setId(primaryKeyFieldValue.toString());
                    processedSubEntityVo.setSubData(nameValueParis);

                    Map<String, Object> resultData = metadataDataMethodSubEntityCrudImpl.doUpdate(processedSubEntityVo);

                    // 将更新的数据行id放入processedIds
                    processedIds.add(primaryKeyFieldValue.toString());
                }else{
                    // 执行插入
                    nameValueParis.put(subRelFieldName,parentValue);// 加入关联信息字段

                    ProcessedSubEntityVo processedSubEntityVo = new ProcessedSubEntityVo();
                    processedSubEntityVo.setTraceId(context.getTraceId());
                    processedSubEntityVo.setSubEntityId(subEntityId);
                    processedSubEntityVo.setSubData(nameValueParis);

                    Map<String, Object> resultData = metadataDataMethodSubEntityCrudImpl.doInsert(processedSubEntityVo);
                    if(!ObjectUtils.isEmpty(resultData)){
                        Map data = (Map) resultData.get("data");
                        if(!ObjectUtils.isEmpty(data)){
                            String id = (String)data.get("id");
                            // 将新插入的数据行id放入processedIds
                            processedIds.add(id);
                        }
                    }
                }
            }
            // 处理完插入和更新操作之后，删除多余的数据行
            AnylineService<?> temporaryService = context.getTemporaryService();
            DefaultConfigStore deleteConfig = new DefaultConfigStore();
            deleteConfig.and(subRelFieldName, parentValue);
            deleteConfig.notIn(primaryKeyFieldName,processedIds);
            deleteConfig.and("deleted",0);
            DataSet dateSet = temporaryService.querys(quoteTableName(subEntity.getTableName()), deleteConfig);

            // 待删除数据行的id集合
            List toDeleteList = dateSet.stream().map(map -> map.get("id")).collect(Collectors.toList());
            // 执行删除操作
            for(Object id: toDeleteList){

                ProcessedSubEntityVo processedSubEntityVo = new ProcessedSubEntityVo();
                processedSubEntityVo.setTraceId(context.getTraceId());
                processedSubEntityVo.setSubEntityId(subEntityId);
                processedSubEntityVo.setId(id.toString());

                metadataDataMethodSubEntityCrudImpl.doDelete(processedSubEntityVo);
            }
        }
    }

    @Override
    protected void executePreWorkflow(ProcessContext context) {
        // 根据id查询修改前数据
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();

        String primaryKeyField = getPrimaryKeyFieldName(fields);

        DefaultConfigStore configStore = new DefaultConfigStore();
        Object id = context.getId();
        configStore.and(primaryKeyField, id);

        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        DataRow dataRow = temporaryService.query(quoteTableName(entity.getTableName()),configStore);

        Long entityId = context.getEntityId();
        Map<String, Object> data = convertNameToId(entityId,dataRow.map());

        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(context.getRequestContext().getTraceId());
        reqDTO.setEntityId(entityId);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_UPDATE);
        reqDTO.setFieldData(data);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if(!respDTO.isTriggered()){
            log.info("BEFORE_UPDATE 数据更新前置工作流未触发，实体Id：{} ，参数：{}，原因：{}", entityId,data,respDTO.getMessage());
            return;
        }
        if(respDTO.isSuccess()){
            log.info("BEFORE_UPDATE 数据更新触发前置工作流成功，实体Id：{} ，参数：{}", entityId,data);
        }else{
            log.error("BEFORE_UPDATE 数据更新触发前置工作流失败，实体Id：{} ，参数：{} ，返回信息：{}", entityId,data,respDTO.getMessage());
            throw  exception(PROCESS_ERROR_BEFORE_UPDATE,respDTO.getMessage());
        }
    }

    @Override
    protected void executePostWorkflow(ProcessContext context) {
        // 根据id查询修改后数据
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();

        String primaryKeyField = getPrimaryKeyFieldName(fields);

        DefaultConfigStore configStore = new DefaultConfigStore();
        Object id = context.getId();
        configStore.and(primaryKeyField, id);

        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        DataRow dataRow = temporaryService.query(quoteTableName(entity.getTableName()),configStore);

        Long entityId = context.getEntityId();
        Map<String, Object> data = convertNameToId(entityId,dataRow.map());

        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(context.getRequestContext().getTraceId());
        reqDTO.setEntityId(entityId);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_UPDATE);
        reqDTO.setFieldData(data);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if(!respDTO.isTriggered()){
            log.info("AFTER_UPDATE 数据更新后置工作流未触发，实体Id：{} ，参数：{}，原因：{}", entityId,data,respDTO.getMessage());
            return;
        }
        if(respDTO.isSuccess()){
            log.info("AFTER_UPDATE 数据更新触发后置工作流成功，实体Id：{} ，参数：{}", entityId,data);
        }else{
            log.info("AFTER_UPDATE 数据更新触发后置工作流失败，实体Id：{} ，参数：{}，返回信息：{}", entityId,data,respDTO.getMessage());
            throw  exception(PROCESS_ERROR_AFTER_UPDATE,respDTO.getMessage());
        }
    }

    /**
     * 处理创建数据
     */
    protected Map<String, Object> processInsertDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 将字段ID映射为字段名
        Map<String, Object> processedData = convertFieldIdToFieldName(data, fields);

        // 获取当前时间
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = dateTime.format(dateTimeFormatter);

        // 仅确定一个实际主键字段名，避免系统字段被误配置为主键导致被赋予雪花ID
        String realPrimaryKey = getPrimaryKeyFieldName(fields);

        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            if (fieldName == null) {
                continue;
            }

            // 仅对真实主键字段生成雪花ID，避免误把deleted/lock_version等系统字段当作主键
            if (fieldName != null && fieldName.equalsIgnoreCase(realPrimaryKey)) {
                if (!processedData.containsKey(fieldName)) {
                    // 生成雪花ID作为主键
                    processedData.put(fieldName, uidGenerator.getUID());
                }
                continue;
            }

            // 处理系统字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsSystemField())) {
                switch (fieldName.toLowerCase()) {
                    case "created_time":
                    case "createtime":
                        processedData.put(fieldName, now);
                        break;
                    case "updated_time":
                    case "updatetime":
                        processedData.put(fieldName, now);
                        break;
                    case "deleted":
                        // deleted字段使用数字类型0，对应数据库中的int8类型
                        processedData.put(fieldName, 0);
                        break;
                    case "lock_version":
                    case "lockversion":
                        processedData.put(fieldName, 0);
                        break;
                    case "tenant_id":
                    case "tenantid":
                        // 这里可以从当前上下文获取租户ID，暂时设置为1
                        processedData.put(fieldName, 1L);
                        break;
                    case "owner_id":
                    case "ownerid":
                        // 设置为当前登录用户ID
                        Long currentUserId = WebFrameworkUtils.getLoginUserId();
                        if (currentUserId != null) {
                            processedData.put(fieldName, currentUserId);
                        } else {
                            log.warn("无法获取当前用户ID，owner_id字段将使用默认值");
                            if (StringUtils.hasText(field.getDefaultValue())) {
                                processedData.put(fieldName, field.getDefaultValue());
                            }
                        }
                        break;
                    case "owner_dept":
                    case "ownerdept":
                        // 设置为当前登录用户的部门ID
                        Long currentUserDeptId = SecurityFrameworkUtils.getLoginUserDeptId();
                        if (currentUserDeptId != null) {
                            processedData.put(fieldName, currentUserDeptId);
                        } else {
                            log.warn("无法获取当前用户部门ID，owner_dept字段将使用默认值1");
                            // 如果获取不到就先写死为1
                            processedData.put(fieldName, 1L);
                        }
                        break;
                    case "creator":
                        // 设置为当前登录用户ID
                        Long creatorUserId = WebFrameworkUtils.getLoginUserId();
                        if (creatorUserId != null) {
                            processedData.put(fieldName, creatorUserId);
                        } else {
                            log.warn("无法获取当前用户ID，creator字段将使用默认值1");
                            processedData.put(fieldName, 1L);
                        }
                        break;
                    case "updater":
                        // 设置为当前登录用户ID
                        Long updaterUserId = WebFrameworkUtils.getLoginUserId();
                        if (updaterUserId != null) {
                            processedData.put(fieldName, updaterUserId);
                        } else {
                            log.warn("无法获取当前用户ID，updater字段将使用默认值1");
                            processedData.put(fieldName, 1L);
                        }
                        break;
                    default:
                        // 其他系统字段按默认值处理
                        if (StringUtils.hasText(field.getDefaultValue())) {
                            processedData.put(fieldName, field.getDefaultValue());
                        }
                        break;
                }
                continue;
            }

            // 设置业务字段默认值
            if (!processedData.containsKey(fieldName) && StringUtils.hasText(field.getDefaultValue())) {
                processedData.put(fieldName, field.getDefaultValue());
            }
        }

        // 处理复杂类型字段（数组、对象等）的JSON序列化
        processComplexTypeFields(fields, processedData);

        return processedData;
    }

}
