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
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.SubEntityVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.transaction.TransactionState;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.invalidParamException;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;

@Slf4j
@Component
public class MetadataDataMethodCreateImpl extends AbstractMetadataDataMethodCoreService {

    @Autowired
    private FlowProcessExecApiImpl flowProcessExecApi;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    MetadataDataMethodSubEntityCrudImpl metadataDataMethodSubEntityCrudImpl;

    /**
     * 校验创建数据的完整性
     */
    public void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        // 将字段ID转换为字段名后再校验
        Map<String, Object> convertedData = convertFieldIdToFieldName(data, fields);
        
        for (MetadataEntityFieldDO field : fields) {
            // 跳过系统字段和主键字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsSystemField()) ||
                    BooleanStatusEnum.isYes(field.getIsPrimaryKey())) {
                continue;
            }

            // 跳过自动编号字段，自动编号字段不进行必填等任何校验
            if (autoNumberService.hasAutoNumber(field.getId())) {
                log.debug("字段[{}]是自动编号字段，跳过校验", field.getFieldName());
                continue;
            }

            // 校验必填字段 - 使用新的枚举值：1-是，0-否
            if (BooleanStatusEnum.isYes(field.getIsRequired()) &&
                    (convertedData.get(field.getFieldName()) == null || String.valueOf(convertedData.get(field.getFieldName())).trim().isEmpty())) {
                throw invalidParamException("字段[{}]为必填字段", field.getDisplayName());
            }
        }
    }

    /**
     * 处理创建数据
     */
    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
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

    /**
     * 处理复杂类型字段的JSON序列化
     * 对于数组和对象类型的字段值，需要序列化为JSON字符串存储到数据库
     *
     * @param fields 字段列表
     * @param processedData 待处理的数据
     */
    private void processComplexTypeFields(List<MetadataEntityFieldDO> fields, Map<String, Object> processedData) {
        log.info("开始处理复杂类型字段，字段数量: {}", fields.size());
        
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
            
            log.info("检查字段 {} (类型: {}), 值类型: {}, 值: {}", 
                    fieldName, fieldType, fieldValue.getClass().getName(), fieldValue);
            
            // 判断是否需要JSON序列化的字段类型
            boolean needsSerialization = needsJsonSerialization(fieldType, fieldValue);
            log.info("字段 {} 是否需要JSON序列化: {}", fieldName, needsSerialization);
            
            if (needsSerialization) {
                try {
                    // 将复杂对象序列化为JSON字符串
                    String jsonString = JsonUtils.toJsonString(fieldValue);
                    processedData.put(fieldName, jsonString);
                    log.info("字段 {} (类型: {}) 的值已序列化为JSON: {}", fieldName, fieldType, jsonString);
                } catch (Exception e) {
                    log.error("字段 {} 的值序列化为JSON失败: {}", fieldName, e.getMessage(), e);
                    // 序列化失败时，保持原值
                }
            }
        }
        
        log.info("复杂类型字段处理完成，最终数据: {}", processedData);
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


    protected void storeData(ProcessContext context) {

        MetadataBusinessEntityDO entity = context.getEntity();
        Map<String, Object> processedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();

        // 5. 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 6. 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
        TenantUtils.executeIgnore(() -> {

            // 7. 执行插入
            log.info("准备插入数据，processedData: {}", processedData);
            
            // 打印每个字段的详细信息
            processedData.forEach((key, value) -> {
                if (value != null) {
                    log.info("插入前字段 {} 的值类型: {}, 值: {}", key, value.getClass().getName(), value);
                } else {
                    log.info("插入前字段 {} 的值为null", key);
                }
            });
            
            DataRow dataRow = new DataRow(processedData);
            
            // 检查DataRow中的数据
            log.info("DataRow创建后的数据: {}", dataRow);
            processedData.forEach((key, value) -> {
                Object dataRowValue = dataRow.get(key);
                if (dataRowValue != null) {
                    log.info("DataRow中字段 {} 的值类型: {}, 值: {}", key, dataRowValue.getClass().getName(), dataRowValue);
                } else {
                    log.info("DataRow中字段 {} 的值为null", key);
                }
            });

            // AnyLine开启事务
            TransactionState transactionState = temporaryService.start();

            Object insertResult = temporaryService.insert(quoteTableName(entity.getTableName()), dataRow);
            log.info("创建数据成功，实体ID: {}, 表名: {}, 插入结果: {}", entityId, entity.getTableName(), insertResult);

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

            // 8. 查询插入后的完整数据
            Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
            log.info("从处理数据中获取主键值: {}, 插入结果: {}", primaryKeyValue, insertResult);

            // 确保主键值不为null
            if (primaryKeyValue == null) {
                log.warn("无法获取主键值，跳过查询插入后的数据，实体ID: {}, 表名: {}", entityId, entity.getTableName());
                // 返回插入的数据
                return buildDataResponse(entity, processedData, fields);
            }

            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), primaryKeyValue, fields);

            // 9. 构建响应（移除多表写入逻辑，直接返回结果）
            return buildDataResponse(entity, resultData, fields);

        });
    }

    @Override
    protected void handleSubEntities(ProcessContext context) {

        // 已经插入到数据库的父表数据行
        Map parentData = context.getProcessedData();

        // 关联关系
        Long partentEntityId = context.getEntityId();
        List<MetadataEntityRelationshipDO> relationshipDOS = entityRelationshipRepository.getRelationshipsByEntityId(partentEntityId);

        //查询子表和主表的关联字段 构建关联条件后插入数据库
        List<MetadataDataMethodSubEntityContext> subEntityVos = context.getSubEntities();
        for(MetadataDataMethodSubEntityContext subEntityContext: subEntityVos){
            Long subEntityId  = subEntityContext.getEntityId();
            List<Map<Long, Object>> subData = subEntityContext.getSubData();

            String parentRelFieldId = relationshipDOS.stream().filter(relationshipDO ->
                            (subEntityId).equals(relationshipDO.getTargetEntityId())).
                    map(MetadataEntityRelationshipDO::getSourceFieldId).findFirst().orElse(null);
            MetadataEntityFieldDO parentEntityFieldDO = entityFieldRepository.findById(Long.valueOf(parentRelFieldId));
            String parentFiledName = parentEntityFieldDO.getFieldName();// 主表关联字段名称
            Object parentValue = parentData.get(parentFiledName);

            String subRelFieldId = relationshipDOS.stream().filter(relationshipDO ->
                            (subEntityId).equals(relationshipDO.getTargetEntityId())).
                    map(MetadataEntityRelationshipDO::getTargetFieldId).findFirst().orElse(null);
            MetadataEntityFieldDO subEntityFieldDO = entityFieldRepository.findById(Long.valueOf(subRelFieldId));
            String subRelFieldName = subEntityFieldDO.getFieldName();// 子表关联字段名称

            List<MetadataEntityFieldDO> subEntityFields = getEntityFields(subEntityId);
            MetadataBusinessEntityDO subEntity = validateEntityExists(subEntityId);

            // 逐条插入子表数据
            for(Map<Long,Object> row: subData){
                // key类型：Long 转 String
                Map covertedRow = row.entrySet().stream().collect(Collectors.toMap(
                        entry ->
                        entry.getKey().toString(),
                        Map.Entry::getValue));

                // id：value 转 name：value
                Map<String,Object> nameValueParis = convertFieldIdToFieldName(covertedRow,subEntityFields);

                // 执行插入
                nameValueParis.put(subRelFieldName,parentValue);// 加入关联信息字段

                ProcessedSubEntityVo processedSubEntityVo = new ProcessedSubEntityVo();
                processedSubEntityVo.setTraceId(context.getTraceId());
                processedSubEntityVo.setSubEntityId(subEntityId);
                processedSubEntityVo.setSubData(nameValueParis);

                Map<String, Object> resultData = metadataDataMethodSubEntityCrudImpl.doInsert(processedSubEntityVo);

            }

        }
    }

    @Override
    protected void executePreWorkflow(ProcessContext context) {
        // 获取插入数据
        Map processedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        Map fieldData = convertNameToId(entityId,processedData == null ? new HashMap<>() : processedData);
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(entityId);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_CREATE);
        reqDTO.setFieldData(fieldData);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if(!respDTO.isTriggered()){
            log.info("BEFORE_CREATE 数据创建前置工作流未触发，实体Id：{} ，参数：{}，原因：{}", entityId,processedData,respDTO.getMessage());
            return;
        }
        if(respDTO.isSuccess()){
            log.info("BEFORE_CREATE 数据创建触发前置工作流成功，实体Id：{} ，参数：{}", entityId,processedData);
        }else{
            log.info("BEFORE_CREATE 数据创建触发前置工作流失败，实体Id：{} ，参数：{} ，返回信息：{}", entityId,processedData,respDTO.getMessage());
            throw  exception(PROCESS_ERROR_BEFORE_CREATE,respDTO.getMessage());
        }
    }

    @Override
    protected void executePostWorkflow(ProcessContext context) {
        // 获取插入数据
        Map processedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        Map fieldData = convertNameToId(entityId,processedData == null ? new HashMap<>() : processedData);
        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(entityId);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_CREATE);
        reqDTO.setFieldData(fieldData);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if(!respDTO.isTriggered()){
            log.info("AFTER_CREATE 数据创建后置工作流未触发，实体Id：{} ，参数：{}，原因：{}", entityId,processedData,respDTO.getMessage());
            return;
        }
        if(respDTO.isSuccess()){
            log.info("AFTER_CREATE 数据创建触发后置工作流成功，实体Id：{} ，参数：{}", entityId,processedData);
        }else{
            log.error("AFTER_CREATE 数据创建触发后置工作流失败，实体Id：{} ，参数：{}，返回信息：{}", entityId,processedData,respDTO.getMessage());
            throw  exception(PROCESS_ERROR_AFTER_CREATE,respDTO.getMessage());
        }
    }



}
