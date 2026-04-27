package com.cmsr.onebase.module.flow.context.table;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/12/25 10:35
 */
@Data
public class TableFieldSchemas implements Serializable {

    private Map<String, Map<String, SemanticFieldSchemaDTO>> tableFieldSchemas = new HashMap<>();


    public boolean isTableName(String fieldName) {
        return tableFieldSchemas.containsKey(fieldName);
    }

    public SemanticFieldTypeEnum getFieldTypeEnum(String tableName, String fieldName) {
        Map<String, SemanticFieldSchemaDTO> fieldSchemaMap = tableFieldSchemas.get(tableName);
        if (fieldSchemaMap == null) {
            return SemanticFieldTypeEnum.TEXT;
        }
        SemanticFieldSchemaDTO semanticFieldSchemaDTO = fieldSchemaMap.get(fieldName);
        if (semanticFieldSchemaDTO == null) {
            return SemanticFieldTypeEnum.TEXT;
        } else {
            return semanticFieldSchemaDTO.getFieldTypeEnum();
        }
    }

}
