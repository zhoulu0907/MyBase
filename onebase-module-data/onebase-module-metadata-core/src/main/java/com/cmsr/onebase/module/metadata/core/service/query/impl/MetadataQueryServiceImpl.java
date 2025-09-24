package com.cmsr.onebase.module.metadata.core.service.query.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.framework.express.OpEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.FieldData;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryCondition;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryOrder;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;
import com.cmsr.onebase.module.metadata.core.domain.query.RowData;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.query.MetadataQueryService;
import com.cmsr.onebase.module.metadata.core.util.FieldValueUtil;
import com.cmsr.onebase.module.metadata.core.util.OperatorUtil;
import com.cmsr.onebase.module.metadata.core.util.QueryConditionUtil;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 元数据查询服务实现类
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Service
@Slf4j
public class MetadataQueryServiceImpl implements MetadataQueryService {

    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    @Override
    public QueryResult queryByConditions(QueryRequest queryRequest) {
        log.info("开始执行领域查询，实体ID: {}, 条件组数量: {}", 
                 queryRequest.getEntityId(), 
                 queryRequest.getConditionGroups() != null ? queryRequest.getConditionGroups().size() : 0);

        // 1. 获取实体字段信息
        List<MetadataEntityFieldDO> entityFields = metadataEntityFieldCoreService
                .getEntityFieldListByEntityId(queryRequest.getEntityId());
        Map<Long, MetadataEntityFieldDO> fieldMap = entityFields.stream()
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, field -> field));

        // 2. 构建复杂条件查询
        List<Map<String, Object>> complexFilters = buildComplexFilters(queryRequest.getConditionGroups(), fieldMap);

        // 3. 构建排序条件
        String sortField = null;
        String sortDirection = "ASC";
        if (!CollectionUtils.isEmpty(queryRequest.getOrders())) {
            QueryOrder order = queryRequest.getOrders().get(0); // 取第一个排序条件
            if (order.getFieldId() != null) {
                MetadataEntityFieldDO field = fieldMap.get(order.getFieldId());
                if (field != null) {
                    sortField = field.getFieldName();
                    sortDirection = StringUtils.hasText(order.getDirection()) ? 
                                   order.getDirection().toUpperCase() : "ASC";
                }
            }
        }

        Integer pageSize = queryRequest.getLimit() != null ? queryRequest.getLimit() : 1000; // 默认最多1000条

        // 4. 执行查询
        List<Map<String, Object>> allResults = executeComplexQuery(
            queryRequest.getEntityId(), 
            complexFilters, 
            sortField, 
            sortDirection, 
            pageSize,
            fieldMap
        );

        log.info("领域查询完成，共{}条记录", allResults.size());

        // 5. 转换结果为行数据列表
        List<RowData> rowDataList = convertToRowDataList(allResults, entityFields);

        QueryResult result = new QueryResult();
        result.setRowDataList(rowDataList);
        result.setTotal((long) rowDataList.size());

        return result;
    }

    /**
     * 构建查询过滤条件 - 支持完整的AND/OR逻辑
     * 
     * @param conditionGroups 条件组列表（二维数组，外层OR，内层AND）
     * @param fieldMap 字段映射Map
     * @return 复杂条件结构
     */
    private List<Map<String, Object>> buildComplexFilters(List<List<QueryCondition>> conditionGroups, 
                                                          Map<Long, MetadataEntityFieldDO> fieldMap) {
        List<Map<String, Object>> orConditionGroups = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(conditionGroups)) {
            orConditionGroups.add(new HashMap<>());
            return orConditionGroups;
        }

        log.debug("开始构建复杂条件，OR组数量: {}", conditionGroups.size());

        // 处理每个OR条件组
        for (int i = 0; i < conditionGroups.size(); i++) {
            List<QueryCondition> andConditionGroup = conditionGroups.get(i);
            if (CollectionUtils.isEmpty(andConditionGroup)) {
                log.debug("OR组{}为空，跳过", i);
                continue;
            }

            Map<String, Object> andFilters = new HashMap<>();
            boolean hasValidCondition = false;

            log.debug("处理OR组{}，包含{}个AND条件", i, andConditionGroup.size());

            // 处理AND条件组内的每个条件
            for (int j = 0; j < andConditionGroup.size(); j++) {
                QueryCondition condition = andConditionGroup.get(j);
                if (condition.getFieldId() == null || condition.getOperator() == null) {
                    log.debug("OR组{}的AND条件{}无效，跳过", i, j);
                    continue;
                }

                MetadataEntityFieldDO field = fieldMap.get(condition.getFieldId());
                if (field == null) {
                    log.warn("字段ID {}不存在，跳过此条件", condition.getFieldId());
                    continue;
                }

                String fieldName = field.getFieldName();
                String operator = condition.getOperator();
                List<String> fieldValues = condition.getFieldValues();

                // 根据操作符处理条件值
                Object filterValue = FieldValueUtil.processConditionValue(operator, fieldValues, field);
                if (filterValue != null) {
                    String conditionKey = QueryConditionUtil.buildConditionKey(fieldName, operator, j);
                    andFilters.put(conditionKey, QueryConditionUtil.buildConditionObject(fieldName, operator, filterValue));
                    hasValidCondition = true;
                    log.debug("OR组{}添加AND条件: {} {} {}", i, fieldName, operator, filterValue);
                }
            }

            if (hasValidCondition) {
                orConditionGroups.add(andFilters);
            }
        }

        if (orConditionGroups.isEmpty()) {
            orConditionGroups.add(new HashMap<>());
        }

        log.info("构建复杂条件完成，生成{}个OR条件组", orConditionGroups.size());
        return orConditionGroups;
    }

    /**
     * 执行复杂查询 - 支持多个OR条件组的查询和结果合并
     */
    private List<Map<String, Object>> executeComplexQuery(Long entityId,
                                                          List<Map<String, Object>> complexFilters,
                                                          String sortField,
                                                          String sortDirection,
                                                          Integer pageSize,
                                                          Map<Long, MetadataEntityFieldDO> fieldMap) {
        
        Set<String> uniqueRecordIds = new HashSet<>();
        List<Map<String, Object>> mergedResults = new ArrayList<>();
        
        log.info("开始执行复杂OR查询，共{}个条件组", complexFilters.size());
        
        // 为每个OR条件组执行查询
        for (int i = 0; i < complexFilters.size(); i++) {
            Map<String, Object> conditionGroup = complexFilters.get(i);
            
            // 将复杂条件组转换为可执行的查询条件
            Map<String, Object> executableFilters = convertToExecutableFilters(conditionGroup, fieldMap);
            
            log.debug("执行OR条件组{}，转换后的查询条件: {}", i, executableFilters);
            
            // 执行单个条件组的查询
            var pageResult = metadataDataMethodCoreService.getDataPage(
                    entityId,
                    1,
                    pageSize,
                    sortField,
                    sortDirection,
                    executableFilters,
                    null
            );
            
            List<Map<String, Object>> groupResults = pageResult.getList();
            log.debug("OR条件组{}查询到{}条记录", i, groupResults.size());
            
            // 合并结果，进行去重
            for (Map<String, Object> result : groupResults) {
                String recordId = QueryConditionUtil.extractRecordId(result);
                if (recordId != null && !uniqueRecordIds.contains(recordId)) {
                    uniqueRecordIds.add(recordId);
                    mergedResults.add(result);
                }
            }
        }
        
        // 对合并后的结果进行排序
        if (StringUtils.hasText(sortField)) {
            QueryConditionUtil.sortMergedResults(mergedResults, sortField, sortDirection);
        }
        
        // 限制最终结果数量
        if (mergedResults.size() > pageSize) {
            mergedResults = mergedResults.subList(0, pageSize);
        }
        
        log.info("复杂查询完成，去重后共{}条记录", mergedResults.size());
        return mergedResults;
    }

    /**
     * 将复杂条件组转换为可执行的查询条件
     */
    private Map<String, Object> convertToExecutableFilters(Map<String, Object> conditionGroup,
                                                           Map<Long, MetadataEntityFieldDO> fieldMap) {
        Map<String, Object> executableFilters = new HashMap<>();
        Map<String, List<Map<String, Object>>> fieldConditions = new HashMap<>();
        
        // 按字段分组条件
        for (Map.Entry<String, Object> entry : conditionGroup.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> conditionObj = (Map<String, Object>) entry.getValue();
                String fieldName = (String) conditionObj.get("fieldName");
                
                if (fieldName != null) {
                    fieldConditions.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(conditionObj);
                }
            }
        }
        
        // 为每个字段选择最优的条件
        for (Map.Entry<String, List<Map<String, Object>>> fieldEntry : fieldConditions.entrySet()) {
            String fieldName = fieldEntry.getKey();
            List<Map<String, Object>> conditions = fieldEntry.getValue();
            
            if (conditions.size() == 1) {
                Map<String, Object> condition = conditions.get(0);
                Object value = processConditionForExecution(condition);
                if (value != null) {
                    executableFilters.put(fieldName, value);
                }
            } else {
                Object mergedValue = mergeFieldConditions(conditions, fieldName);
                if (mergedValue != null) {
                    executableFilters.put(fieldName, mergedValue);
                }
            }
        }
        
        return executableFilters;
    }

    /**
     * 处理单个条件用于执行
     */
    private Object processConditionForExecution(Map<String, Object> condition) {
        String operator = (String) condition.get("operator");
        Object value = condition.get("value");
        
        if (operator == null || value == null) {
            return null;
        }
        
        OpEnum opEnum = OperatorUtil.parseOperator(operator);
        
        switch (opEnum) {
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case GREATER_EQUALS:
            case LESS_THAN:
            case LESS_EQUALS:
            case LATER_THAN:
            case EARLIER_THAN:
            case CONTAINS:
            case NOT_CONTAINS:
            case EXISTS_IN:
            case NOT_EXISTS_IN:
                return value;

            case IS_EMPTY:
            case IS_NOT_EMPTY:
                return null;

            case RANGE:
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> rangeMap = (Map<String, Object>) value;
                    return rangeMap.get("start");
                }
                return value;

            case CONTAINS_ALL:
            case NOT_CONTAINS_ALL:
            case CONTAINS_ANY:
            case NOT_CONTAINS_ANY:
                return value;

            default:
                return value;
        }
    }

    /**
     * 合并同一字段的多个条件
     */
    private Object mergeFieldConditions(List<Map<String, Object>> conditions, String fieldName) {
        if (conditions.isEmpty()) {
            return null;
        }

        Map<String, Object> bestCondition = null;
        int bestPriority = Integer.MAX_VALUE;

        for (Map<String, Object> condition : conditions) {
            String operator = (String) condition.get("operator");
            OpEnum opEnum = OperatorUtil.parseOperator(operator);
            int priority = getOperatorPriority(opEnum);

            if (priority < bestPriority) {
                bestPriority = priority;
                bestCondition = condition;
            }
        }

        return bestCondition != null ? processConditionForExecution(bestCondition) : null;
    }

    /**
     * 获取操作符的优先级
     */
    private int getOperatorPriority(OpEnum opEnum) {
        switch (opEnum) {
            case EQUALS:
                return 1;
            case CONTAINS:
                return 2;
            case GREATER_THAN:
            case GREATER_EQUALS:
            case LESS_THAN:
            case LESS_EQUALS:
            case LATER_THAN:
            case EARLIER_THAN:
                return 3;
            case EXISTS_IN:
            case NOT_EXISTS_IN:
                return 4;
            case NOT_EQUALS:
                return 5;
            case NOT_CONTAINS:
                return 6;
            case RANGE:
                return 7;
            case IS_EMPTY:
            case IS_NOT_EMPTY:
                return 8;
            case CONTAINS_ALL:
            case NOT_CONTAINS_ALL:
            case CONTAINS_ANY:
            case NOT_CONTAINS_ANY:
                return 9;
            default:
                return 10;
        }
    }

    /**
     * 转换查询结果为行数据列表（按行组织数据）
     */
    private List<RowData> convertToRowDataList(List<Map<String, Object>> queryResults,
                                               List<MetadataEntityFieldDO> entityFields) {
        List<RowData> rowDataList = new ArrayList<>();

        for (int i = 0; i < queryResults.size(); i++) {
            Map<String, Object> result = queryResults.get(i);
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                continue;
            }

            // 为这一行创建字段数据列表
            List<FieldData> rowFieldDataList = new ArrayList<>();
            
            for (MetadataEntityFieldDO field : entityFields) {
                String fieldName = field.getFieldName();
                Object fieldValue = data.get(fieldName);

                // 为每个字段创建字段数据（即使值为null也创建，保持字段结构完整）
                FieldData fieldData = new FieldData();
                fieldData.setFieldId(field.getId());
                fieldData.setFieldName(fieldName);
                fieldData.setDisplayName(field.getDisplayName());
                fieldData.setFieldType(field.getFieldType());
                fieldData.setFieldValue(fieldValue != null ? 
                    FieldValueUtil.formatFieldValue(fieldValue, field.getFieldType()) : null);

                rowFieldDataList.add(fieldData);
            }

            // 创建行数据对象
            RowData rowData = new RowData();
            rowData.setFieldDataList(rowFieldDataList);
            rowData.setRowId(QueryConditionUtil.extractRecordId(result)); // 使用记录ID作为行标识
            
            rowDataList.add(rowData);
        }

        log.info("转换查询结果完成，共{}条记录，生成{}行数据", 
                 queryResults.size(), rowDataList.size());
        return rowDataList;
    }
}
