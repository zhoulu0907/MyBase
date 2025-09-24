package com.cmsr.onebase.module.metadata.api.datamethod.impl;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.InsertDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.OrderDto;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;
import com.cmsr.onebase.module.metadata.core.service.datamethod.MetadataDataMethodCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.util.QueryConditionUtil;
import com.cmsr.onebase.module.metadata.core.util.FieldValueUtil;
import com.cmsr.onebase.module.metadata.core.util.OperatorUtil;
import com.cmsr.onebase.framework.express.OpEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
/**
 * 数据方法API实现类
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Service
@Slf4j
public class DataMethodApiImpl implements DataMethodApi {


    @Resource
    private MetadataDataMethodCoreService metadataDataMethodCoreService;
    
    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    /**
     * 根据条件查询数据
     * 主要功能：通过传入的条件和条件对应的值，拼接出查询的SQL语句，并排序，然后将查询到的数据组装成对象返回
     * 
     * @param reqDTO 查询请求DTO
     * @return 查询结果列表
     */
    @Override
    public List<EntityFieldDataRespDTO> getDataByCondition(@Valid EntityFieldDataReqDTO reqDTO) {
        log.info("开始根据条件查询数据，实体ID: {}, 条件数量: {}", 
                 reqDTO.getEntityId(), 
                 reqDTO.getConditionDTO() != null ? reqDTO.getConditionDTO().size() : 0);

        // 1. 校验请求参数
        validateRequest(reqDTO);

        // 2. 获取实体字段信息，用于类型转换
        List<MetadataEntityFieldDO> entityFields = metadataEntityFieldCoreService
                .getEntityFieldListByEntityId(reqDTO.getEntityId());
        Map<Long, MetadataEntityFieldDO> fieldMap = entityFields.stream()
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, field -> field));

        // 3. 构建复杂条件查询
        List<Map<String, Object>> complexFilters = buildComplexFilters(reqDTO.getConditionDTO(), fieldMap);
        
        // 4. 构建排序条件
        String sortField = null;
        String sortDirection = "ASC";
        if (!CollectionUtils.isEmpty(reqDTO.getOrderDtos())) {
            OrderDto orderDto = reqDTO.getOrderDtos().get(0); // 取第一个排序条件
            if (orderDto.getFieldId() != null) {
                MetadataEntityFieldDO field = fieldMap.get(Long.valueOf(orderDto.getFieldId()));
                if (field != null) {
                    sortField = field.getFieldName();
                    sortDirection = StringUtils.hasText(orderDto.getSortOrder()) ? 
                                   orderDto.getSortOrder().toUpperCase() : "ASC";
                }
            }
        }

        Integer pageSize = reqDTO.getNum() != null ? reqDTO.getNum() : 1000; // 默认最多1000条

        // 5. 执行查询 - 统一使用复杂条件查询策略
        List<Map<String, Object>> allResults = executeComplexQuery(
            reqDTO.getEntityId(), 
            complexFilters, 
            sortField, 
            sortDirection, 
            pageSize,
            fieldMap
        );
        
        log.info("复杂条件查询完成，共{}条记录", allResults.size());

        // 6. 转换结果为响应DTO
        return convertToResponseDTOs(allResults, entityFields);
    }

    /**
     * 校验请求参数
     * 
     * @param reqDTO 请求DTO
     */
    private void validateRequest(EntityFieldDataReqDTO reqDTO) {
        if (reqDTO.getEntityId() == null) {
            throw new IllegalArgumentException("实体ID不能为空");
        }
    }

    /**
     * 构建查询过滤条件 - 支持完整的AND/OR逻辑
     * 根据条件DTO和操作符构建过滤条件，支持二维数组结构（外层OR，内层AND）
     * 
     * @param conditions 条件列表（二维数组，外层OR，内层AND）
     * @param fieldMap 字段映射Map
     * @return 复杂条件结构，包含多个AND条件组用于OR合并
     */
    private List<Map<String, Object>> buildComplexFilters(List<List<ConditionDTO>> conditions, 
                                                          Map<Long, MetadataEntityFieldDO> fieldMap) {
        List<Map<String, Object>> orConditionGroups = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(conditions)) {
            // 如果没有条件，返回一个空的条件组
            orConditionGroups.add(new HashMap<>());
            return orConditionGroups;
        }

        log.debug("开始构建复杂条件，OR组数量: {}", conditions.size());

        // 处理每个OR条件组（外层数组的每个元素）
        for (int i = 0; i < conditions.size(); i++) {
            List<ConditionDTO> andConditionGroup = conditions.get(i);
            if (CollectionUtils.isEmpty(andConditionGroup)) {
                log.debug("OR组{}为空，跳过", i);
                continue;
            }

            Map<String, Object> andFilters = new HashMap<>();
            boolean hasValidCondition = false;

            log.debug("处理OR组{}，包含{}个AND条件", i, andConditionGroup.size());

            // 处理AND条件组内的每个条件（内层数组的每个元素）
            for (int j = 0; j < andConditionGroup.size(); j++) {
                ConditionDTO condition = andConditionGroup.get(j);
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
                List<String> fieldValues = condition.getFieldValue();

                // 根据操作符处理条件值
                Object filterValue = FieldValueUtil.processConditionValue(operator, fieldValues, field);
                if (filterValue != null) {
                    // 为了支持同一字段的多个条件，使用特殊的key格式
                    String conditionKey = QueryConditionUtil.buildConditionKey(fieldName, operator, j);
                    andFilters.put(conditionKey, QueryConditionUtil.buildConditionObject(fieldName, operator, filterValue));
                    hasValidCondition = true;
                    log.debug("OR组{}添加AND条件: {} {} {}", i, fieldName, operator, filterValue);
                }
            }

            // 如果这个AND组有有效条件，添加到OR组列表中
            if (hasValidCondition) {
                orConditionGroups.add(andFilters);
            }
        }

        if (orConditionGroups.isEmpty()) {
            // 如果没有有效条件，返回一个空的条件组
            orConditionGroups.add(new HashMap<>());
        }

        log.info("构建复杂条件完成，生成{}个OR条件组", orConditionGroups.size());
        return orConditionGroups;
    }

    /**
     * 执行复杂查询 - 支持多个OR条件组的查询和结果合并
     * 
     * @param entityId 实体ID
     * @param complexFilters 复杂条件组列表
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param pageSize 每次查询的页大小
     * @param fieldMap 字段映射
     * @return 合并后的查询结果
     */
    private List<Map<String, Object>> executeComplexQuery(Long entityId,
                                                          List<Map<String, Object>> complexFilters,
                                                          String sortField,
                                                          String sortDirection,
                                                          Integer pageSize,
                                                          Map<Long, MetadataEntityFieldDO> fieldMap) {
        
        Set<String> uniqueRecordIds = new HashSet<>(); // 用于去重
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
                    1, // 第一页
                    pageSize, // 每个条件组最多查询指定数量
                    sortField,
                    sortDirection,
                    executableFilters,
                    null // methodCode
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
     * 处理同一字段的多个条件，智能选择最优执行策略
     * 
     * @param conditionGroup 复杂条件组
     * @param fieldMap 字段映射
     * @return 可执行的查询条件Map
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
                // 单个条件，直接使用
                Map<String, Object> condition = conditions.get(0);
                Object value = processConditionForExecution(condition);
                if (value != null) {
                    executableFilters.put(fieldName, value);
                }
            } else {
                // 多个条件，选择最宽松的条件或合并为范围条件
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
     * 将条件对象转换为可执行的查询条件值
     * 
     * @param condition 条件对象
     * @return 处理后的条件值
     */
    private Object processConditionForExecution(Map<String, Object> condition) {
        String operator = (String) condition.get("operator");
        Object value = condition.get("value");
        
        if (operator == null || value == null) {
            return null;
        }
        
        // 解析操作符
        OpEnum opEnum = OperatorUtil.parseOperator(operator);
        
        // 根据操作符类型处理值
        switch (opEnum) {
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case GREATER_EQUALS:
            case LESS_THAN:
            case LESS_EQUALS:
            case LATER_THAN:
            case EARLIER_THAN:
                // 比较操作符，直接返回值
                return value;

            case CONTAINS:
            case NOT_CONTAINS:
                // 模糊查询操作符，返回原始值用于LIKE查询
                return value;

            case EXISTS_IN:
            case NOT_EXISTS_IN:
                // IN查询操作符，返回值列表
                return value;

            case IS_EMPTY:
            case IS_NOT_EMPTY:
                // 空值检查操作符，不需要值
                return null;

            case RANGE:
                // 范围查询操作符，返回范围对象
                if (value instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> rangeMap = (Map<String, Object>) value;
                    // 可以返回第一个值进行简化处理，或者返回整个范围对象
                    return rangeMap.get("start");
                }
                return value;

            case CONTAINS_ALL:
            case NOT_CONTAINS_ALL:
            case CONTAINS_ANY:
            case NOT_CONTAINS_ANY:
                // 数组包含操作符，返回值列表
                return value;

            default:
                // 未知操作符，默认返回值
                return value;
        }
    }

    /**
     * 合并同一字段的多个条件
     * 智能选择最适合的条件用于查询
     * 
     * @param conditions 条件列表
     * @param fieldName 字段名
     * @return 合并后的条件值
     */
    private Object mergeFieldConditions(List<Map<String, Object>> conditions, String fieldName) {
        if (conditions.isEmpty()) {
            return null;
        }

        // 优先级排序：等于 > 包含 > 大于/小于 > IN > 其他
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
     * 获取操作符的优先级，用于条件合并时的选择
     * 优先级越低越优先选择
     * 
     * @param opEnum 操作符枚举
     * @return 优先级值
     */
    private int getOperatorPriority(OpEnum opEnum) {
        switch (opEnum) {
            case EQUALS:
                return 1; // 最高优先级
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
                return 10; // 最低优先级
        }
    }

    /**
     * 转换查询结果为响应DTO列表
     * 
     * @param queryResults 查询结果
     * @param entityFields 实体字段列表
     * @return 响应DTO列表
     */
    private List<EntityFieldDataRespDTO> convertToResponseDTOs(List<Map<String, Object>> queryResults,
                                                               List<MetadataEntityFieldDO> entityFields) {
        List<EntityFieldDataRespDTO> responseDTOs = new ArrayList<>();

        for (Map<String, Object> result : queryResults) {
            // 获取数据部分
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                continue;
            }

            // 为每个字段创建一个响应DTO
            for (MetadataEntityFieldDO field : entityFields) {
                String fieldName = field.getFieldName();
                Object fieldValue = data.get(fieldName);

                if (fieldValue != null) {
                    EntityFieldDataRespDTO respDTO = new EntityFieldDataRespDTO();
                    respDTO.setFieldId(field.getId());
                    respDTO.setFieldName(fieldName);
                    respDTO.setDisplayName(field.getDisplayName());
                    respDTO.setFieldType(field.getFieldType());
                    respDTO.setJdbcType(FieldValueUtil.inferJdbcType(field.getFieldType()));
                    // 使用Object类型，保持原始数据类型
                    respDTO.setFieldValue(FieldValueUtil.formatFieldValue(fieldValue, field.getFieldType()));

                    responseDTOs.add(respDTO);
                }
            }
        }

        log.info("转换查询结果完成，共{}条记录，生成{}个字段DTO", 
                 queryResults.size(), responseDTOs.size());
        return responseDTOs;
    }

    @Override
    public Integer deleteDataByCondition(@Valid DeleteDataReqDTO reqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDataByCondition'");
    }

    @Override
    public Integer insertData(@Valid InsertDataReqDTO reqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertData'");
    }

    @Override
    public Integer updateData(@Valid UpdateDataReqDTO reqDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateData'");
    }
}
