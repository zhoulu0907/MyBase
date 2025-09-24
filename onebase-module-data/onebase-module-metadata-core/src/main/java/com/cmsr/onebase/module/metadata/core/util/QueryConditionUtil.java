package com.cmsr.onebase.module.metadata.core.util;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

/**
 * 查询条件处理工具类
 * 用于处理查询结果的通用操作
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Slf4j
public class QueryConditionUtil {

    /**
     * 从查询结果中提取记录ID用于去重
     * 
     * @param result 查询结果
     * @return 记录ID字符串
     */
    public static String extractRecordId(Map<String, Object> result) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        if (data != null) {
            // 尝试获取主键ID
            Object id = data.get("id");
            if (id != null) {
                return String.valueOf(id);
            }
            
            // 如果没有id字段，尝试其他可能的主键字段
            for (String key : data.keySet()) {
                if (key.toLowerCase().contains("id") && data.get(key) != null) {
                    return String.valueOf(data.get(key));
                }
            }
        }
        
        // 如果无法提取ID，返回结果的hash作为唯一标识
        return String.valueOf(result.hashCode());
    }

    /**
     * 对合并后的结果进行排序
     * 
     * @param results 结果列表
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     */
    public static void sortMergedResults(List<Map<String, Object>> results, String sortField, String sortDirection) {
        results.sort((r1, r2) -> {
            Object val1 = extractSortValue(r1, sortField);
            Object val2 = extractSortValue(r2, sortField);
            
            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return "ASC".equalsIgnoreCase(sortDirection) ? -1 : 1;
            if (val2 == null) return "ASC".equalsIgnoreCase(sortDirection) ? 1 : -1;
            
            int comparison = compareValues(val1, val2);
            return "DESC".equalsIgnoreCase(sortDirection) ? -comparison : comparison;
        });
    }

    /**
     * 从结果中提取排序值
     * 
     * @param result 查询结果
     * @param sortField 排序字段
     * @return 排序值
     */
    private static Object extractSortValue(Map<String, Object> result, String sortField) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        return data != null ? data.get(sortField) : null;
    }

    /**
     * 比较两个值
     * 
     * @param val1 值1
     * @param val2 值2
     * @return 比较结果
     */
    @SuppressWarnings("unchecked")
    private static int compareValues(Object val1, Object val2) {
        if (val1 instanceof Comparable && val2 instanceof Comparable) {
            // 尝试直接比较，如果类型不匹配会抛出异常
            if (val1.getClass().equals(val2.getClass())) {
                return ((Comparable<Object>) val1).compareTo(val2);
            }
        }
        // 类型不匹配或不可比较时按字符串比较
        return String.valueOf(val1).compareTo(String.valueOf(val2));
    }

    /**
     * 构建条件键名，支持同一字段的多个条件
     * 
     * @param fieldName 字段名
     * @param operator 操作符
     * @param index 条件索引
     * @return 条件键名
     */
    public static String buildConditionKey(String fieldName, String operator, int index) {
        return fieldName + "_" + operator.toLowerCase().replaceAll("[^a-z0-9]", "_") + "_" + index;
    }

    /**
     * 构建条件对象，包含字段名、操作符和值
     * 
     * @param fieldName 字段名
     * @param operator 操作符
     * @param value 条件值
     * @return 条件对象
     */
    public static Map<String, Object> buildConditionObject(String fieldName, String operator, Object value) {
        Map<String, Object> conditionObj = new HashMap<>();
        conditionObj.put("fieldName", fieldName);
        conditionObj.put("operator", operator);
        conditionObj.put("value", value);
        return conditionObj;
    }
}
