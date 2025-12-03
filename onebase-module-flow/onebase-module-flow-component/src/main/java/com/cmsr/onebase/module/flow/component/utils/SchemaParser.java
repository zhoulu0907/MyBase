package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.module.flow.context.enums.FieldTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SchemaParser {
    public static Map<String, Object> parseBySchemaDef(Map<String, Object> data, PropertyDefine jsonSchemaDef) {
        Map<String, Object> parseResult;
        parseResult = parseStructure(data, jsonSchemaDef);
        // validate required
        Set<String> requiredFields = jsonSchemaDef.getRequired();
        if (CollectionUtils.isEmpty(requiredFields)) {
            return parseResult;
        }
        checkRequiredFields(requiredFields, parseResult.keySet());
        return parseResult;
    }

    private static Map<String, Object> parseStructure(Map<String, Object> data, PropertyDefine jsonSchemaDef) {
        Map<String, Object> parseResult = new HashMap<>();
        Map<String, PropertyDefine> subFields = jsonSchemaDef.getProperties();
        for (String fieldName : subFields.keySet()) {
            PropertyDefine subFieldDef = subFields.get(fieldName);
            Object fieldValue = parseFieldBySchema(data.get(fieldName), subFieldDef);
            parseResult.put(fieldName, fieldValue);
        }
        // validate required
        Set<String> requiredFields = jsonSchemaDef.getRequired();
        if (CollectionUtils.isEmpty(requiredFields)) {
            return parseResult;
        }
        checkRequiredFields(requiredFields, parseResult.keySet());
        return parseResult;
    }

    private static Object parseFieldBySchema(Object data, PropertyDefine jsonSchemaDef) {
        String fieldTypeStr = jsonSchemaDef.getType();
        FieldTypeEnum fieldType = FieldTypeEnum.getByName(fieldTypeStr);
        if (fieldType == null) {
            throw new IllegalArgumentException("不存在的数据类型: " + fieldTypeStr);
        }
        JdbcTypeEnum jdbcType = fieldType.getJdbcType();
        return JdbcTypeConvertor.convert(jdbcType.getCode(), data);
    }

    private static void checkRequiredFields(Set<String> requiredFields, Set<String> resultKeySet) {
        boolean passed = resultKeySet.containsAll(requiredFields);
        if (!passed) {
            Collection<String> subtractRequired = CollectionUtils.subtract(requiredFields, resultKeySet);
            throw new IllegalArgumentException("必填参数缺失: " + subtractRequired);
        }
    }
}
