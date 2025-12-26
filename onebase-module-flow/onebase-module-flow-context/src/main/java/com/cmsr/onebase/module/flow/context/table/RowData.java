package com.cmsr.onebase.module.flow.context.table;

import com.google.common.collect.ForwardingMap;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统大部分情况下是一行数据，所以这里增加了列的类型
 *
 * @Author：huangjie
 * @Date：2025/12/25 9:53
 */
@Data
public class RowData extends ForwardingMap<String, Object> {

    private String tableName;

    private Map<String, ColumnType> columns = new HashMap<>();

    private Map<String, Object> values = new HashMap<>();

    @Override
    protected Map<String, Object> delegate() {
        return values;
    }

    public void addValue(String fieldName, ColumnType columnType, Object value) {
        columns.put(fieldName, columnType);
        values.put(fieldName, value);
    }

    public boolean hasSubTable() {
        return columns.values().stream().anyMatch(columnType -> columnType == ColumnType.SUBTABLE);
    }


    /**
     * 把子表数据平铺，相当于实现 left join。
     * values的类型来判断是一般字段还是子表字段。
     *
     * @return 平铺后的数据列表
     */
    public List<Map<String, Object>> flatRowData() {
        // 第一步：收集所有子表字段和普通字段
        List<String> subTableKeys = new ArrayList<>();
        Map<String, Object> simpleFields = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (columns.get(entry.getKey()) == ColumnType.SUBTABLE) {
                subTableKeys.add(entry.getKey());
            } else {
                simpleFields.put(entry.getKey(), entry.getValue());
            }
        }
        
        // 如果没有子表字段，直接返回当前行的副本
        if (subTableKeys.isEmpty()) {
            List<Map<String, Object>> result = new ArrayList<>();
            result.add(new HashMap<>(simpleFields));
            return result;
        }
        
        // 第二步：获取所有子表数据，验证数据类型
        List<TableData> subTables = new ArrayList<>();
        for (String key : subTableKeys) {
            Object value = values.get(key);
            if (value instanceof TableData) {
                subTables.add((TableData) value);
            } else {
                throw new IllegalArgumentException(
                    String.format("子表字段 '%s' 的数据类型错误，期望为TableData类型，但实际为: %s", 
                                 key, value != null ? value.getClass().getName() : "null"));
            }
        }

        // 第三步：开始构建平铺结果，初始化为基础行（只包含普通字段）
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(new HashMap<>(simpleFields));

        // 第四步：依次处理每个子表，进行类似 left join 的操作
        for (int i = 0; i < subTableKeys.size(); i++) {
            String subTableKey = subTableKeys.get(i);
            TableData subTable = subTables.get(i);
            
            // 为当前子表处理结果创建新的集合
            List<Map<String, Object>> newResult = new ArrayList<>();
            
            // 如果子表为空，进行 left join 操作（生成一行包含null值的结果）
            if (subTable.isEmpty()) {
                for (Map<String, Object> baseRow : result) {
                    Map<String, Object> newRow = new HashMap<>(baseRow);
                    newRow.put(subTableKey, null); // 子表为空时添加null值
                    newResult.add(newRow);
                }
            } else {
                // 如果子表不为空，将基础行的每一行与子表的每一行进行笛卡尔积组合
                for (Map<String, Object> baseRow : result) {
                    for (RowData subRowData : subTable) {
                        Map<String, Object> combinedRow = new HashMap<>(baseRow);
                        
                        // 将子表字段以"前缀.字段名"的格式添加到结果中，避免字段名冲突
                        for (Map.Entry<String, Object> subEntry : subRowData.entrySet()) {
                            String prefixedFieldName = subTableKey + "." + subEntry.getKey();
                            combinedRow.put(prefixedFieldName, subEntry.getValue());
                        }
                        
                        newResult.add(combinedRow);
                    }
                }
            }
            
            // 更新结果集为新生成的结果
            result = newResult;
        }
        
        return result;
    }


}
