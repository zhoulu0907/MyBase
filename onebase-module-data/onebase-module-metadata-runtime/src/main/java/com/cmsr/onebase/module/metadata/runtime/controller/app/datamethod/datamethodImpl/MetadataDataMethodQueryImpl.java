package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl;

import com.cmsr.onebase.framework.security.runtime.RTSecurityContext;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.app.api.security.bo.FieldPermission;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.AbstractMetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.permission.filter.FieldPermissionFilter;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.SubEntityVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.*;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

@Slf4j
@Component
public class MetadataDataMethodQueryImpl extends AbstractMetadataDataMethodCoreService {

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityService;

    @Resource
    private FieldPermissionFilter fieldPermissionFilter;

    /**
     * 校验创建数据的完整性
     */
    public void validateDataIntegrity(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {

    }

    /**
     * 设置默认数据
     */
    protected Map<String, Object> processDataAndSetDefaults(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {


        return null;
    }

    /**
     * 功能权限校验
     */
    protected void validatePermission(ProcessContext context) {

//        /**
//         * 查询的功能权限校验
//         */
//        TenantContextHolder.setIgnore(true);
//        RTSecurityContext.mockLoginUser(3386012505007460352L,46699591748616192L);
//        FieldPermission fieldPermission = RTSecurityContext.getMenuFieldPermission(47012574606491648L);
//        System.out.println(fieldPermission);


    }

    /**
     * 数据校验
     * @param context
     */
    protected void validateData(ProcessContext context) {

    }

    /**
     * 优化查询条件
     */
    protected void optimizeQueryConditions(ProcessContext context){

        log.info("优化查询条件");
    }

    /**
     * 存储数据
     * @param context
     */
    protected void storeData(ProcessContext context) {


    }

    @Override
    protected Map<String, Object> getData(ProcessContext context) {
        Long entityId = context.getEntity().getId();
        Object id = context.getId();
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        TenantUtils.executeIgnore(() -> {
            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, quoteTableName(entity.getTableName()), id, fields);
            Map<String, Object> filterMap = null;
            if (null != context.getMetadataPermissionContext()){
                filterMap = fieldPermissionFilter.filterFields(resultData, context.getMetadataPermissionContext().getFieldPermission(), context.getFields());
            }
            if (resultData == null || resultData.isEmpty()) {
                throw exception(BUSINESS_ENTITY_NOT_EXISTS);
            }
            applyFieldStorageStrategies(resultData, fields, FieldValueTransformMode.READ);
            // 获取主表数据 放入上下文
            Map map = buildDataResponse(entity, filterMap == null ? resultData:filterMap , fields);
            context.setProcessedData(map);
        });

        //查询子表数据
        Long sourceEntityId = entityId;
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, sourceEntityId);
        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);
        List<String> subTableIds = new ArrayList<String>();
        List subEntities = new ArrayList();
        for(MetadataEntityRelationshipDO relationshipDO:relationships){
            MetadataEntityFieldDO sourceFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getSourceFieldId()));

            MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
            MetadataEntityFieldDO targetFieldDO = entityFieldRepository.findById(Long.valueOf(relationshipDO.getTargetFieldId()));
            String tableName = targetEntity.getTableName();
            String fieldName = targetFieldDO.getFieldName();

            List<MetadataEntityFieldDO> targetfields = getEntityFields(targetEntity.getId());

            DefaultConfigStore config = new DefaultConfigStore();
            if("parent_id".equals(fieldName)){
                config.and(fieldName, id);
            }else{
                // 获取主表数据关联字段的值
                Map resultData = context.getProcessedData();
                Object value = resultData.get(sourceFieldDO.getFieldName());
                config.and(fieldName, value);
            }
            // 检查子表中是否有软删除字段
            boolean hasDeletedField = targetfields.stream()
                    .anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));
            if(hasDeletedField){
                config.and("deleted", 0);
            }
            // 查询子表数据
            DataSet dataSet = temporaryService.querys(tableName,config);

            List list = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = convertDataRowToMap(row, targetfields);
                // 将Map<String, Object> data 转换为 Map<Long, Object> data
                Map<Long, Object> _subData = new HashMap<>();
                // long的值是targetFieldId, Object是data中的value。 对应关系是：data中的key转换成大写，与targetfields中的fieldName对应，找到对应的fieldId
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String dataKey = entry.getKey();
                    Object dataValue = entry.getValue();

                    // 将data的key转换为大写后，与targetfields中的fieldName进行匹配
                    String dataKeyUpper = dataKey.toUpperCase();
                    for (MetadataEntityFieldDO field : targetfields) {
                        if (field.getFieldName() != null && field.getFieldName().toUpperCase().equals(dataKeyUpper)) {
                            // 找到匹配的字段，使用fieldId作为key
                            _subData.put(field.getId(), dataValue);
                            break;
                        }
                    }
                }

                list.add(_subData);
//                list.add(data);
            }
            SubEntityVo subEntityVo = new SubEntityVo();
            subEntityVo.setSubData(list);
            subEntityVo.setSubEntityId(targetEntity.getId());
            subEntities.add(subEntityVo);
        }
        Map resultData = context.getProcessedData();
        resultData.put("subEntities",subEntities);
        context.setProcessedData(resultData);
        return resultData;
    }

    /**
     * 查询数据
     * @param context
     * @return
     */
    protected Map<String, Object> queryData(ProcessContext context) {
        Long entityId = context.getEntity().getId();
        Map<String, Object> conditions = context.getData();
        Integer pageNo = (Integer)conditions.get("pageNo");
        Integer pageSize = (Integer)conditions.get("pageSize");
        String sortField = (String)conditions.get("sortField");
        String sortDirection = (String)conditions.get("sortDirection");
        Map<String, Object> filters = (Map<String, Object>)conditions.get("filters");
        // 添加调试日志
        log.info("核心服务分页查询参数 - entityId: {}, pageNo: {}, pageSize: {}, pageSize类型: {}",
                entityId, pageNo, pageSize, pageSize != null ? pageSize.getClass().getSimpleName() : "null");

        // 移除多表查询逻辑，直接使用单表分页
        MetadataBusinessEntityDO entity = validateEntityExists(entityId);
        List<MetadataEntityFieldDO> fields = getEntityFields(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) {
            throw exception(DATASOURCE_NOT_EXISTS);
        }
        AnylineService<?> temporaryService = temporaryDatasourceService.createTemporaryService(datasource);
        log.info("成功切换到数据源：{}", datasource.getCode());
        boolean hasDeletedField = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));

        return TenantUtils.executeIgnore(() -> {
            ConfigStore configs = new DefaultConfigStore();
            boolean deletedConditionAdded = false;
            if (filters != null && !filters.isEmpty()) {
                Set<String> names = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String rawKey = entry.getKey();
                    Object rawVal = entry.getValue();
                    if (rawVal == null) {
                        continue;
                    }
                    if ("deleted".equalsIgnoreCase(rawKey) || "tenant_id".equalsIgnoreCase(rawKey)) {
                        continue;
                    }
                    // 兼容上层传入的结构：可能是直接 fieldName -> value，也可能是 conditionKey -> {fieldName, operator, value}
                    if (rawVal instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> cond = (Map<String, Object>) rawVal;
                        String fieldName = (String) cond.getOrDefault("fieldName", rawKey);
                        Object value = cond.get("value");
                        String operator = cond.get("operator") != null ? String.valueOf(cond.get("operator")) : "CONTAINS";
                        if (value == null || !names.contains(fieldName)) {
                            continue;
                        }
                        applyOperatorCondition(configs, fieldName, operator, value);
                    } else {
                        // 退化：直接LIKE
                        if (names.contains(rawKey)) {
                            configs.and(Compare.LIKE, rawKey, rawVal);
                        }
                    }
                }
            }
            if (hasDeletedField && !deletedConditionAdded) {
                configs.and(Compare.EQUAL, "deleted", 0);
                deletedConditionAdded = true;
            }
            Set<String> fieldNames = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
            if (StringUtils.hasText(sortField) && fieldNames.contains(sortField)) {
                String orderClause = sortField;
                orderClause += "desc".equalsIgnoreCase(sortDirection) ? " DESC" : " ASC";
                configs.order(orderClause);
            } else {
                String primaryKeyField = getPrimaryKeyFieldName(fields);
                configs.order(primaryKeyField + " DESC");
            }
            if (pageNo != null && pageSize != null) {
                PageNavi page = new DefaultPageNavi(pageNo, pageSize);
                configs.setPageNavi(page);
                log.info("设置分页参数 - pageNo: {}, pageSize: {}", pageNo, pageSize);
            }
            ConfigStore countConfigs = new DefaultConfigStore();
            if (filters != null && !filters.isEmpty()) {
                Set<String> existingFieldNames = fields.stream().map(MetadataEntityFieldDO::getFieldName).collect(Collectors.toSet());
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String rawKey = entry.getKey();
                    Object rawVal = entry.getValue();
                    if (rawVal == null) {
                        continue;
                    }
                    if ("deleted".equalsIgnoreCase(rawKey) || "tenant_id".equalsIgnoreCase(rawKey)) {
                        continue;
                    }
                    if (rawVal instanceof Map) {
                        @SuppressWarnings("unchecked") Map<String, Object> cond = (Map<String, Object>) rawVal;
                        String fieldName = (String) cond.getOrDefault("fieldName", rawKey);
                        Object value = cond.get("value");
                        String operator = cond.get("operator") != null ? String.valueOf(cond.get("operator")) : "CONTAINS";
                        if (value == null || !existingFieldNames.contains(fieldName)) {
                            continue;
                        }
                        // count 语句保守处理：范围/比较用相同 Compare，模糊仍用 LIKE
                        applyOperatorCondition(countConfigs, fieldName, operator, value);
                    } else {
                        if (existingFieldNames.contains(rawKey)) {
                            countConfigs.and(Compare.LIKE, rawKey, rawVal);
                        }
                    }
                }
            }
            if (hasDeletedField) {
                countConfigs.and(Compare.EQUAL, "deleted", 0);
            }
            long total = temporaryService.count(quoteTableName(entity.getTableName()), countConfigs);
            DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), configs);
            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < dataSet.size(); i++) {
                DataRow row = dataSet.getRow(i);
                Map<String, Object> data = convertDataRowToMap(row, fields);
                list.add(buildDataResponse(entity, data, fields));
            }
            Map<String,Object> result = new HashMap<String,Object>();
            result.put("list",list);
            result.put("total",total);
            context.setProcessedData(result);
            return result;
        });
    }


    /**
     * 格式化结果
     * @param context
     * @return
     */
    protected Map<String, Object> formatResult(ProcessContext context) {
        return context.getProcessedData();
    }


    /**
     * 根据自定义 operator 映射到 Anyline Compare 条件
     * 支持：EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_EQUALS, LESS_THAN, LESS_EQUALS,
     *       CONTAINS, NOT_CONTAINS, EARLIER_THAN, LATER_THAN, RANGE(起始值), EXISTS_IN, NOT_EXISTS_IN
     * 其他或未识别操作符默认 LIKE
     */
    private void applyOperatorCondition(ConfigStore configs, String fieldName, String operator, Object value) {
        if (operator == null) {
            log.debug("[FILTER] field={} op=LIKE(default) value={}", fieldName, value);
            configs.and(Compare.LIKE, fieldName, value);
            return;
        }
        String op = operator.trim().toUpperCase();
        switch (op) {
            case "EQUALS":
                log.debug("[FILTER] field={} op=EQUAL value={}", fieldName, value);
                configs.and(Compare.EQUAL, fieldName, value); break;
            case "NOT_EQUALS":
                log.debug("[FILTER] field={} op=NOT_EQUAL value={}", fieldName, value);
                configs.and(Compare.NOT_EQUAL, fieldName, value); break;
            case "GREATER_THAN":
                log.debug("[FILTER] field={} op> value={}", fieldName, value);
                configs.and(Compare.GREAT, fieldName, value); break;
            case "GREATER_EQUALS":
                log.debug("[FILTER] field={} op>= value={}", fieldName, value);
                configs.and(Compare.GREAT_EQUAL, fieldName, value); break;
            case "LESS_THAN":
                log.debug("[FILTER] field={} op< value={}", fieldName, value);
                configs.and(Compare.LESS, fieldName, value); break;
            case "LESS_EQUALS":
                log.debug("[FILTER] field={} op<= value={}", fieldName, value);
                configs.and(Compare.LESS_EQUAL, fieldName, value); break;
            case "CONTAINS":
                log.debug("[FILTER] field={} op LIKE value=%{}%", fieldName, value);
                configs.and(Compare.LIKE, fieldName, value); break;
            case "NOT_CONTAINS":
                log.debug("[FILTER] field={} op NOT LIKE value=%{}%", fieldName, value);
                configs.and(Compare.NOT_LIKE, fieldName, value); break;
            case "EARLIER_THAN":
                log.debug("[FILTER] field={} op EARLIER(<) value={}", fieldName, value);
                configs.and(Compare.LESS, fieldName, value); break;
            case "LATER_THAN":
                log.debug("[FILTER] field={} op LATER(>) value={}", fieldName, value);
                configs.and(Compare.GREAT, fieldName, value); break;
            case "EXISTS_IN":
                log.debug("[FILTER] field={} op IN value={}", fieldName, value);
                configs.and(Compare.IN, fieldName, value); break;
            case "NOT_EXISTS_IN":
                log.debug("[FILTER] field={} op NOT IN value={}", fieldName, value);
                configs.and(Compare.NOT_IN, fieldName, value); break;
            case "RANGE":
                // 如果 value 是 Map {start,end} 结构，分别添加 >= 与 <=
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked") Map<String,Object> range = (Map<String,Object>) value;
                    Object start = range.get("start");
                    Object end = range.get("end");
                    if (start != null) { configs.and(Compare.GREAT_EQUAL, fieldName, start); }
                    if (end != null) { configs.and(Compare.LESS_EQUAL, fieldName, end); }
                    log.debug("[FILTER] field={} op RANGE start={} end={}", fieldName, start, end);
                } else {
                    configs.and(Compare.GREAT_EQUAL, fieldName, value);
                    log.debug("[FILTER] field={} op RANGE(start only) >= {}", fieldName, value);
                }
                break;
            case "IS_EMPTY":
                log.debug("[FILTER] field={} op =''", fieldName);
                configs.and(Compare.EQUAL, fieldName, ""); break;
            case "IS_NOT_EMPTY":
                log.debug("[FILTER] field={} op !=''", fieldName);
                configs.and(Compare.NOT_EQUAL, fieldName, ""); break;
            default:
                log.debug("[FILTER] field={} op LIKE(default) value={}", fieldName, value);
                configs.and(Compare.LIKE, fieldName, value); break;
        }
    }

}
