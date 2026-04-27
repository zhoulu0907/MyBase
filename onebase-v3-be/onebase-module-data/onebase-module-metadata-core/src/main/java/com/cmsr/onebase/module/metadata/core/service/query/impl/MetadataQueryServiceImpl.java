package com.cmsr.onebase.module.metadata.core.service.query.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        log.info("开始执行领域查询，实体ID: {}, 全局AND条件数: {}, 条件组数量: {}", 
                 queryRequest.getEntityId(),
                 queryRequest.getAndConditions() != null ? queryRequest.getAndConditions().size() : 0,
                 queryRequest.getConditionGroups() != null ? queryRequest.getConditionGroups().size() : 0);

        // 1. 获取实体字段信息
        List<MetadataEntityFieldDO> entityFields = metadataEntityFieldCoreService
                .getEntityFieldListByEntityId(queryRequest.getEntityId());
        Map<Long, MetadataEntityFieldDO> fieldMap = entityFields.stream()
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, field -> field));

        // 2. 构建复杂条件查询
        // 逻辑：(andConditions) AND ((group1) OR (group2) OR ...)
        List<Map<String, Object>> complexFilters = buildComplexFilters(
                queryRequest.getAndConditions(),
                queryRequest.getConditionGroups(), 
                fieldMap);

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

    // 4. 执行单SQL OR 查询
    var pageResult = metadataDataMethodCoreService.getDataPageOr(
        queryRequest.getEntityId(), 1, pageSize,
        sortField, sortDirection,
        complexFilters,
        null);
    List<Map<String, Object>> allResults = pageResult.getList();
    log.info("领域查询完成，共{}条记录", pageResult.getTotal());

        // 5. 转换结果为行数据列表
        List<RowData> rowDataList = convertToRowDataList(allResults, entityFields);

        QueryResult result = new QueryResult();
        result.setRowDataList(rowDataList);
        result.setTotal((long) rowDataList.size());

        return result;
    }

    /**
     * 构建查询过滤条件 - 支持完整的AND/OR逻辑
     * 逻辑：(andConditions) AND ((group1) OR (group2) OR ...)
     * 
     * @param andConditions 全局AND条件列表
     * @param conditionGroups 条件组列表（二维数组，外层OR，内层AND）
     * @param fieldMap 字段映射Map
     * @return 复杂条件结构
     */
    private List<Map<String, Object>> buildComplexFilters(List<QueryCondition> andConditions,
                                                          List<List<QueryCondition>> conditionGroups, 
                                                          Map<Long, MetadataEntityFieldDO> fieldMap) {
        List<Map<String, Object>> orConditionGroups = new ArrayList<>();
        
        // 构建全局 AND 条件的 Map
        Map<String, Object> globalAndFilters = buildAndConditionsMap(andConditions, fieldMap);
        
        if (CollectionUtils.isEmpty(conditionGroups)) {
            // 如果没有 OR 条件组，但有全局 AND 条件，则返回全局 AND 条件
            if (!globalAndFilters.isEmpty()) {
                orConditionGroups.add(globalAndFilters);
            } else {
                orConditionGroups.add(new HashMap<>());
            }
            return orConditionGroups;
        }

        log.debug("开始构建复杂条件，全局AND条件数: {}, OR组数量: {}", 
                  andConditions != null ? andConditions.size() : 0, conditionGroups.size());

        // 处理每个OR条件组
        for (int i = 0; i < conditionGroups.size(); i++) {
            List<QueryCondition> andConditionGroup = conditionGroups.get(i);
            if (CollectionUtils.isEmpty(andConditionGroup)) {
                log.debug("OR组{}为空，跳过", i);
                continue;
            }

            // 复制全局 AND 条件作为基础
            Map<String, Object> andFilters = new HashMap<>(globalAndFilters);
            boolean hasValidCondition = !andFilters.isEmpty();

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
                    String conditionKey = QueryConditionUtil.buildConditionKey(fieldName, operator, 
                                                                              j + globalAndFilters.size());
                    andFilters.put(conditionKey, QueryConditionUtil.buildConditionObject(fieldName, operator, filterValue));
                    hasValidCondition = true;
                    log.debug("OR组{}添加AND条件: {} {} {}", i, fieldName, operator, filterValue);
                }
            }

            if (hasValidCondition) {
                log.debug("OR组{}汇总AND条件键: {}", i, andFilters.keySet());
                orConditionGroups.add(andFilters);
            } else {
                log.debug("OR组{}无有效条件，未加入执行列表", i);
            }
        }

        if (orConditionGroups.isEmpty()) {
            // 如果所有 OR 组都无效，但有全局 AND 条件，则返回全局 AND 条件
            if (!globalAndFilters.isEmpty()) {
                orConditionGroups.add(globalAndFilters);
            } else {
                orConditionGroups.add(new HashMap<>());
            }
        }

        log.info("构建复杂条件完成，生成{}个OR条件组", orConditionGroups.size());
        return orConditionGroups;
    }

    /**
     * 构建全局 AND 条件的 Map
     * 
     * @param andConditions 全局AND条件列表
     * @param fieldMap 字段映射Map
     * @return AND条件Map
     */
    private Map<String, Object> buildAndConditionsMap(List<QueryCondition> andConditions, 
                                                       Map<Long, MetadataEntityFieldDO> fieldMap) {
        Map<String, Object> andFilters = new HashMap<>();
        
        if (CollectionUtils.isEmpty(andConditions)) {
            return andFilters;
        }

        log.debug("开始构建全局AND条件，数量: {}", andConditions.size());

        for (int i = 0; i < andConditions.size(); i++) {
            QueryCondition condition = andConditions.get(i);
            if (condition.getFieldId() == null || condition.getOperator() == null) {
                log.debug("全局AND条件{}无效，跳过", i);
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
                String conditionKey = QueryConditionUtil.buildConditionKey(fieldName, operator, i);
                andFilters.put(conditionKey, QueryConditionUtil.buildConditionObject(fieldName, operator, filterValue));
                log.debug("添加全局AND条件: {} {} {}", fieldName, operator, filterValue);
            }
        }

        log.debug("全局AND条件构建完成，有效条件数: {}", andFilters.size());
        return andFilters;
    }

    /**
     * 执行复杂查询 - 支持多个OR条件组的查询和结果合并
     */
    // executeComplexQuery 已废弃（改为单SQL OR 实现）

    /**
     * 将复杂条件组转换为可执行的查询条件
     */

    /**
     * 处理单个条件用于执行
     */

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
