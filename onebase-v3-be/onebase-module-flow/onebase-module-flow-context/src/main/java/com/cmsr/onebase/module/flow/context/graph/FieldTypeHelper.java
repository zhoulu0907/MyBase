package com.cmsr.onebase.module.flow.context.graph;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/12/15 9:48
 */
public class FieldTypeHelper {


    public static void processConditionList(List<Conditions> filterCondition, Object arg, int section) {
        if (CollectionUtils.isEmpty(filterCondition)) {
            return;
        }
        for (Conditions conditions : filterCondition) {
            processConditionItem(conditions.getConditions(), arg, section);
        }
    }

    /**
     * section 2 是为：tableName.fieldName
     * section 3 是为：节点id.tableName.fieldName
     *
     * @param fields
     * @param arg
     * @param section
     */
    public static void processConditionItem(List<ConditionItem> fields, Object arg, int section) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        for (ConditionItem conditionItem : fields) {
            String fieldKey = conditionItem.getFieldKey();
            if (StringUtils.isEmpty(fieldKey)) {
                continue;
            }
            String[] split = StringUtils.split(fieldKey, '.');
            if (split.length < section) {
                continue;
            }
            String tableName = split[section - 2];
            String fieldName = split[section - 1];
            if (arg instanceof Map fieldInfoMap) {
                SemanticFieldTypeEnum fieldTypeEnum = findFieldTypeEnum(tableName, fieldName, fieldInfoMap);
                conditionItem.setFieldTypeEnum(fieldTypeEnum);
            }
        }
    }

    public static SemanticFieldTypeEnum findFieldTypeEnum(String tableName, String fieldName, Map<String, Map<String, SemanticFieldSchemaDTO>> fieldInfoMap) {
        Map<String, SemanticFieldSchemaDTO> fieldSchemaMap = fieldInfoMap.get(tableName);
        if (fieldSchemaMap == null) {
            return SemanticFieldTypeEnum.TEXT;
        }
        String name = convertToFieldName(fieldName);
        SemanticFieldSchemaDTO semanticFieldSchemaDTO = fieldSchemaMap.get(name);
        if (semanticFieldSchemaDTO == null) {
            return SemanticFieldTypeEnum.TEXT;
        }
        return semanticFieldSchemaDTO.getFieldTypeEnum();
    }

    public static String convertToFieldName(String fieldKey) {
        if (StringUtils.countMatches(fieldKey, ".") > 0) {
            return StringUtils.substringAfter(fieldKey, ".");
        } else {
            return fieldKey;
        }
    }

    public static Set<String> extractTableNames(List<Conditions> filterCondition) {
        if (CollectionUtils.isEmpty(filterCondition)) {
            return Set.of();
        }
        Set<String> tableNames = new HashSet<>();
        for (Conditions conditions : filterCondition) {
            for (ConditionItem condition : conditions.getConditions()) {
                String fieldKey = condition.getFieldKey();
                if (StringUtils.isEmpty(fieldKey)) {
                    continue;
                }
                String[] strings = StringUtils.split(fieldKey, '.');
                if (strings.length == 3) {
                    tableNames.add(strings[1]);
                }
            }
        }
        return tableNames;
    }

}
