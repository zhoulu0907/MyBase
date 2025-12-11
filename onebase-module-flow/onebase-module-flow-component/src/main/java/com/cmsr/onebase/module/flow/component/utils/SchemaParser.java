package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.module.flow.context.enums.FieldTypeConvertor;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaParser {
    public static Map<String, Object> parseBySchemaDef(Map<String, Object> data, List<PropertyDefine> schemaList) {
        Map<String, Object> dataExtracted = new HashMap<>();
        for (PropertyDefine schemaDef : schemaList) {
            Map<String, Object> parseResult = parseStructure(data, schemaDef);
            dataExtracted.putAll(parseResult);
        }
        return dataExtracted;
    }

    private static Map<String, Object> parseStructure(Map<String, Object> data, PropertyDefine schemaDef) {
        Map<String, Object> parseResult = new HashMap<>();
        String fieldName = schemaDef.getName();
        String fieldType = schemaDef.getType();
        SemanticFieldTypeEnum semanticFieldTypeEnum = SemanticFieldTypeEnum.ofCode(fieldType);
        if (semanticFieldTypeEnum == null) {
            throw new IllegalArgumentException("不存在的数据类型: " + fieldType);
        }
        Object rawValue = data.get(fieldName);
        if (rawValue == null) {
            return parseResult;
        }

        Object converted = FieldTypeConvertor.convert(semanticFieldTypeEnum, rawValue);
        parseResult.put(fieldName, converted);
        return parseResult;
    }
}
