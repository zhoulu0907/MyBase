package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl;

import cn.hutool.core.util.ObjectUtil;
import com.cmsr.onebase.framework.common.util.object.ObjectUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
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
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.ProcessedSubEntityVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class MetadataDataMethodDeleteImpl extends AbstractMetadataDataMethodCoreService {

    @Resource
    private MetadataBusinessEntityCoreService businessEntityService;

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Autowired
    private FlowProcessExecApiImpl flowProcessExecApi;

    @Resource
    MetadataDataMethodSubEntityCrudImpl metadataDataMethodSubEntityCrudImpl;

    @Override
    protected void validateData(ProcessContext context) {
        Object id = context.getId();
        if(id == null){
            throw invalidParamException("主键字段[{}]为必传字段");
        }
    }

    /**
     * 根据id校验待更新的数据记录是否存在
     * @param context
     */
    protected void checkDataExistence(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();

        // 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        TenantUtils.executeIgnore(() -> {
            // 校验数据存在
            Object id = context.getId();
            validateDataExistsWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);
        });

    }

    /**
     * 针对删除后的数据进行存储
     * @param context
     */
    protected void storeData(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        Map<String, Object> processedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        // 获取临时数据源服务
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }

        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());

        // 检查表中是否有软删除字段
        boolean hasDeletedField = fields.stream()
                .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));

        // 动态业务表忽略租户条件 - 使用TenantUtils.executeIgnore包装操作
        TenantUtils.executeIgnore(() -> {

            // 获取主键字段名
            String primaryKeyField = getPrimaryKeyFieldName(fields);

            // 构建更新条件
            DefaultConfigStore configStore = new DefaultConfigStore();
            Object id = context.getId();
            configStore.and(primaryKeyField, id);

            DataRow sourceDataRow = temporaryService.query(quoteTableName(entity.getTableName()),configStore);
            Map sourceData = convertDataRowToMap(sourceDataRow,fields);
            context.setProcessedData(sourceData);//保存源业务实体数据 在级联删除的时候使用（根据关联字段查询原始值）

            long deleteCount;
            if (hasDeletedField) {
                // 软删除：更新deleted字段为删除时间戳
                DataRow updateData = new DataRow();
                updateData.put("deleted", String.valueOf(System.currentTimeMillis()));

                // 修改时间
                LocalDateTime dateTime = LocalDateTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String now = dateTime.format(dateTimeFormatter);
                updateData.put("updated_time", now);

                deleteCount = temporaryService.update(quoteTableName(entity.getTableName()), updateData, configStore);
                log.info("软删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
            } else {
                // 物理删除：直接删除记录
                deleteCount = temporaryService.delete(quoteTableName(entity.getTableName()), configStore);
                log.info("物理删除数据成功，实体ID: {}, 表名: {}, 删除记录数: {}", entityId, entity.getTableName(), deleteCount);
            }
            boolean ok = deleteCount > 0;
            if(ok){
//               super.storeData(context);
                handleSubEntities(context);
            }
            return ok;
        });
    }

    @Override
    protected void handleSubEntities(ProcessContext context) {
        //处理子表逻辑
        processCascadeDelete(context);//级联删除
    }

    /**
     * 级联删除逻辑
     */
    protected void processCascadeDelete(ProcessContext context){
        MetadataBusinessEntityDO entity = context.getEntity();
        log.info("开始对被删除表进行级联删除操作,表名：{}",entity.getTableName());
        Long entityId = context.getEntityId();
        List<MetadataEntityRelationshipDO> relationshipDOs  = getRelationShipByEntityId(entityId,"SOURCE");
        if(!relationshipDOs.isEmpty()){
            doCascadeIfSourceEntityDeleted(context,relationshipDOs);
        }else{
            log.info("没有找到被删除表作为源表对应的关联表，处理被删除表作为目标表的级联删除逻辑。实体ID: {}，表名：{}",entityId,entity.getTableName());
            List<MetadataEntityRelationshipDO> relationships  = getRelationShipByEntityId(entityId,"TARGET");
            if(relationships.isEmpty()){
                return;
            }
            doCascadeIfTargetEntityDeleted(context,relationships);
        }
    }

    /**
     * 当被删除的表在级联配置关系中是源表,删除关联表的数据
     * @param context relationshipDOs
     */
    private void doCascadeIfSourceEntityDeleted(ProcessContext context,List<MetadataEntityRelationshipDO> relationshipDOs){
        MetadataBusinessEntityDO entity = context.getEntity();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        for(MetadataEntityRelationshipDO relationshipDO: relationshipDOs) {
            if("DELETE".equals(relationshipDO.getCascadeType())){
                continue;
            }
            if (relationshipDO.getRelationshipType().equals("MANY_TO_ONE")) {
                log.info("被删除表和关联表是多对一关系，无需对一方删除: 源实体ID: {}, 关联实体ID： {}", relationshipDO.getSourceEntityId(), relationshipDO.getTargetEntityId());
                return;
            } else if (relationshipDO.getRelationshipType().equals("ONE_TO_ONE") || relationshipDO.getRelationshipType().equals("ONE_TO_MANY") || relationshipDO.getRelationshipType().equals("MANY_TO_MANY")) {
                MetadataBusinessEntityDO sourceEntity = businessEntityService.getBusinessEntity(relationshipDO.getSourceEntityId());
                if (sourceEntity.getEntityType() == 3) {
                    log.info("被删除表类型是多对多的中间表，表名：{}，无需对其他关联表删除", sourceEntity.getTableName());
                    return;
                }
                MetadataEntityFieldDO sourceFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getSourceFieldId()));
                if ("parent_id".equals(sourceFieldDO.getFieldName())) {
                    log.info("被删除表是子表，表名：{}，无需对主表删除", sourceEntity.getTableName());
                    return;
                }

                // 子表实体id
                Long subEntityId = relationshipDO.getTargetEntityId();

                // 构建查询子表关联条件
                MetadataEntityFieldDO targetEntityFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getTargetFieldId()));
                String targetField = targetEntityFieldDO.getFieldName();
                Map deletedData = context.getProcessedData();
                Object value = deletedData.get(sourceFieldDO.getFieldName());

                // 要删除的子表数据行
                AnylineService<?> temporaryService = context.getTemporaryService();
                DefaultConfigStore deleteConfig = new DefaultConfigStore();
                deleteConfig.and(targetField, value);
                deleteConfig.and("deleted",0);
                MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
                DataSet dateSet = temporaryService.querys(quoteTableName(targetEntity.getTableName()), deleteConfig);

                // 待删除数据行的id集合
                List toDeleteList = dateSet.stream().map(map -> map.get("id")).collect(Collectors.toList());

                if(ObjectUtil.isEmpty(toDeleteList)){
                    return;
                }

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

    }

    /**
     * 当被删除的表在级联配置关系中是目标表,删除关联表的数据
     * @param context relationshipDOs
     */
    private void doCascadeIfTargetEntityDeleted(ProcessContext context,List<MetadataEntityRelationshipDO> relationshipDOs){
        MetadataBusinessEntityDO entity = context.getEntity();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        for(MetadataEntityRelationshipDO relationshipDO: relationshipDOs) {
            if("DELETE".equals(relationshipDO.getCascadeType())){
                continue;
            }
            if (relationshipDO.getRelationshipType().equals("ONE_TO_MANY")) {
                log.info("关联表和被删除表是一对多关系，无需对一方删除: 源实体ID: {}, 关联实体ID： {}", relationshipDO.getSourceEntityId(), relationshipDO.getTargetEntityId());
                return;
            } else if (relationshipDO.getRelationshipType().equals("ONE_TO_ONE") || relationshipDO.getRelationshipType().equals("MANY_TO_ONE") || relationshipDO.getRelationshipType().equals("MANY_TO_MANY")) {
                MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
                if (targetEntity.getEntityType() == 3) {
                    log.info("被删除表类型是多对多的中间表，表名：{}，无需对其他关联表删除", targetEntity.getTableName());
                    return;
                }
                MetadataEntityFieldDO targetFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getTargetFieldId()));
                if ("parent_id".equals(targetFieldDO.getFieldName())) {
                    log.info("被删除表是子表，表名：{}，无需对主表删除", targetEntity.getTableName());
                    return;
                }

                // 子表实体id
                Long subEntityId = relationshipDO.getSourceEntityId();

                // 构建查询子表关联条件
                MetadataEntityFieldDO sourceEntityFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getSourceFieldId()));
                String sourceField = sourceEntityFieldDO.getFieldName();
                Map deletedData = context.getProcessedData();
                Object value = deletedData.get(targetFieldDO.getFieldName());

                // 要删除的子表数据行
                AnylineService<?> temporaryService = context.getTemporaryService();
                DefaultConfigStore deleteConfig = new DefaultConfigStore();
                deleteConfig.and(sourceField, value);
                deleteConfig.and("deleted",0);
                MetadataBusinessEntityDO sourceEntity = businessEntityService.getBusinessEntity(relationshipDO.getSourceEntityId());
                DataSet dateSet = temporaryService.querys(quoteTableName(sourceEntity.getTableName()), deleteConfig);

                // 待删除数据行的id集合
                List toDeleteList = dateSet.stream().map(map -> map.get("id")).collect(Collectors.toList());

                if(ObjectUtil.isEmpty(toDeleteList)){
                    return;
                }

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
    }

    /**
     * 根据被删除的数据实体Id查询对应的关联关系
     * @param entityId type
     * @return
     */
    private List<MetadataEntityRelationshipDO> getRelationShipByEntityId(Long entityId, String type){
        DefaultConfigStore configStore = new DefaultConfigStore();
        if(type.equals("SOURCE")){
            //被删除表是源表
            configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityId);
        }else if(type.equals("TARGET")){
            //被删除表是目标表
            configStore.and(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, entityId);
        }
        configStore.order("create_time", Order.TYPE.DESC);
        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);
        return relationships;
    }

    /**
     * 结果进行格式化
     * @param context
     * @return
     */
    @Override
    protected Map<String, Object> formatResult(ProcessContext context) {
        Map<String, Object> processedData = new HashMap<>();

        MetadataBusinessEntityDO entity = context.getEntity();
        Long entityId = context.getEntityId();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();

        return TenantUtils.executeIgnore(() -> {
            Object primaryKeyValue = context.getId();
            // 确保主键值不为null
            if (primaryKeyValue == null) {
                processedData.put("id", "");
                log.warn("无法获取主键值，跳过查询删除后的数据，实体ID: {}, 表名: {}", entityId, entity.getTableName());
                // 返回插入的数据
                return buildDataResponse(entity, processedData, fields);
            }else {
                processedData.put("id", primaryKeyValue);
                return buildDataResponse(entity, processedData, fields);
            }

        });
    }

    @Override
    protected void executePreWorkflow(ProcessContext context) {
        //查询待删除数据
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();

        String primaryKeyField = getPrimaryKeyFieldName(fields);

        DefaultConfigStore configStore = new DefaultConfigStore();
        Object id = context.getId();
        configStore.and(primaryKeyField, id);
        configStore.and("deleted",0);

        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        DataRow dataRow = temporaryService.query(quoteTableName(entity.getTableName()),configStore);

        // 将要删除的数据行保存下来，以便在后置删除触发方法中使用（物理删除之后数据库可能不存在该数据行）
        context.setProcessedData(dataRow.map());

        Long entityId = context.getEntityId();
        Map<String, Object> data = convertNameToId(entityId,dataRow.map());

        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(entityId);
        reqDTO.setTriggerEvent(TriggerEventEnum.BEFORE_DELETE);
        reqDTO.setFieldData(data);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if(!respDTO.isTriggered()){
            log.info("BEFORE_DELETE 数据删除前置工作流未触发，实体Id：{} ，参数：{}，原因：{}", entityId,data,respDTO.getMessage());
            return;
        }
        if(respDTO.isSuccess()){
            log.info("BEFORE_DELETE 数据删除触发前置工作流成功，实体Id：{} ，参数：{}", entityId,data);
        }else{
            log.error("BEFORE_DELETE 数据删除触发前置工作流失败，实体Id：{} ，参数：{} ，返回信息：{}", entityId,data,respDTO.getMessage());
            throw  exception(PROCESS_ERROR_BEFORE_DELETE,respDTO.getMessage());
        }
    }

    @Override
    protected void executePostWorkflow(ProcessContext context) {
        //查询删除数据
        Map deletedData = context.getProcessedData();

        Long entityId = context.getEntityId();
        Map<String, Object> data = convertNameToId(entityId,deletedData);

        EntityTriggerReqDTO reqDTO = new EntityTriggerReqDTO();
        reqDTO.setTraceId(UUID.randomUUID().toString());
        reqDTO.setEntityId(entityId);
        reqDTO.setTriggerEvent(TriggerEventEnum.AFTER_DELETE);
        reqDTO.setFieldData(data);
        EntityTriggerRespDTO respDTO = flowProcessExecApi.entityTrigger(reqDTO);
        if(!respDTO.isTriggered()){
            log.info("AFTER_DELETE 数据删除后置工作流未触发，实体Id：{} ，参数：{}，原因：{}", entityId,data,respDTO.getMessage());
            return;
        }
        if(respDTO.isSuccess()){
            log.info("AFTER_DELETE 数据删除触发后置工作流成功，实体Id：{} ，参数：{}", entityId,data);
        }else{
            log.error("AFTER_DELETE 数据删除触发后置工作流失败，实体Id：{} ，参数：{}，返回信息：{}", entityId,data,respDTO.getMessage());
            throw  exception(PROCESS_ERROR_AFTER_DELETE,respDTO.getMessage());
        }
    }

}
