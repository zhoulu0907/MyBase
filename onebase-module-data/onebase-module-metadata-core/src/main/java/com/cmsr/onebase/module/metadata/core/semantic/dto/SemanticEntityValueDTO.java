package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Schema(description = "值模型 DTO")
@Data
public class SemanticEntityValueDTO {
    @Schema(description = "主实体ID")
    private Object id;

    @Schema(description = "主实体字段值")
    private Map<String, SemanticFieldValueDTO<Object>> fieldValueMap;

    @Schema(description = "关联对象值")
    private Map<String, SemanticRelationValueDTO> connectors;

    public SemanticFieldValueDTO<Object> getFieldValueById(Long fieldId) {
        if (fieldValueMap == null || fieldId == null) return null;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> entry : fieldValueMap.entrySet()) {
            SemanticFieldValueDTO<Object> v = entry.getValue();
            if (v != null && fieldId.equals(v.getFieldId())) return v;
        }
        return null;
    }

    public SemanticFieldValueDTO<Object> getFieldValueByUuid(String fieldUuid) {
        if (fieldValueMap == null || fieldUuid == null) return null;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> entry : fieldValueMap.entrySet()) {
            SemanticFieldValueDTO<Object> v = entry.getValue();
            if (v != null && fieldUuid.equals(v.getFieldUuid())) return v;
        }
        return null;
    }

    public SemanticFieldValueDTO<Object> getConnectorFieldValue(String connectorName, String fieldName) {
        if (connectorName == null || fieldName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValue() == null) return null;
        Map<String, SemanticFieldValueDTO<Object>> fields = relation.getRowValue().getFields();
        if (fields == null) return null;
        return fields.get(fieldName);
    }

    public SemanticFieldValueDTO<Object> getConnectorFieldValueAt(String connectorName, int index, String fieldName) {
        if (connectorName == null || fieldName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValueList() == null) return null;
        List<SemanticRowValueDTO> rows = relation.getRowValueList();
        if (index < 0 || index >= rows.size()) return null;
        Map<String, SemanticFieldValueDTO<Object>> fields = rows.get(index).getFields();
        if (fields == null) return null;
        return fields.get(fieldName);
    }

    public SemanticFieldValueDTO<Object> getFieldValueByTableAndField(String tableName, String fieldName) {
        if (fieldName == null) return null;
        if (connectors != null && tableName != null) {
            SemanticFieldValueDTO<Object> v = getConnectorFieldValue(tableName, fieldName);
            if (v != null) return v;
        }
        if (fieldValueMap == null) return null;
        return fieldValueMap.get(fieldName);
    }

    public SemanticFieldValueDTO<Object> getFieldValueByTableAndFieldAt(String tableName, int index, String fieldName) {
        if (tableName == null || fieldName == null) return null;
        return getConnectorFieldValueAt(tableName, index, fieldName);
    }

    public Map<String, Object> getCurrentEntityRawValueForJson() {
        Map<String, Object> result = new HashMap<>();
        if (fieldValueMap == null) return result;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> entry : fieldValueMap.entrySet()) {
            String name = entry.getKey();
            SemanticFieldValueDTO<Object> v = entry.getValue();
            result.put(name, v == null ? null : v.getRawValueForJson());
        }
        return result;
    }

    public Map<String, Object> getCurrentEntityRawMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (fieldValueMap == null) return result;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> entry : fieldValueMap.entrySet()) {
            String name = entry.getKey();
            SemanticFieldValueDTO<Object> v = entry.getValue();
            result.put(name, v == null ? null : v.getRawValue());
        }
        return result;
    }

    public Map<String, Object> getGlobalRawMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> self = getCurrentEntityRawMap();
        if (self != null) {
            for (Map.Entry<String, Object> e : self.entrySet()) {
                result.put(e.getKey(), e.getValue());
            }
        }
        if (connectors != null) {
            for (String name : connectors.keySet()) {
                result.put(name, isConnectorMany(name) ? getConnectorRawList(name) : getConnectorRawObject(name));
            }
        }
        return result;
    }

    public Map<String, Object> getGlobalRawMapForJson() {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> self = getCurrentEntityRawValueForJson();
        if (self != null) {
            for (Map.Entry<String, Object> e : self.entrySet()) {
                result.put(e.getKey(), e.getValue());
            }
        }
        if (connectors != null) {
            for (String name : connectors.keySet()) {
                result.put(name, isConnectorMany(name) ? getConnectorRawListForJson(name) : getConnectorRawObjectForJson(name));
            }
        }
        return result;
    }

    public Map<String, Object> getConnectorRawObject(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValue() == null) return null;
        SemanticRowValueDTO row = relation.getRowValue();
        Map<String, Object> obj = new LinkedHashMap<>();
        Map<String, SemanticFieldValueDTO<Object>> fields = row.getFields();
        if (fields != null) {
            for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
                obj.put(e.getKey(), e.getValue() == null ? null : e.getValue().getRawValue());
            }
        }
        return obj;
    }

    public Map<String, Object> getConnectorRawObjectForJson(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValue() == null) return null;
        SemanticRowValueDTO row = relation.getRowValue();
        Map<String, Object> obj = new LinkedHashMap<>();
        Map<String, SemanticFieldValueDTO<Object>> fields = row.getFields();
        if (fields != null) {
            for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
                obj.put(e.getKey(), e.getValue() == null ? null : e.getValue().getRawValueForJson());
            }
        }
        return obj;
    }

    public Map<String, SemanticFieldValueDTO<Object>> getConnectorDTOObject(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValue() == null) return null;
        SemanticRowValueDTO row = relation.getRowValue();
        return row.getFields();
    }

    public List<Map<String, Object>> getConnectorRawList(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValueList() == null) return null;
        List<SemanticRowValueDTO> rows = relation.getRowValueList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (SemanticRowValueDTO row : rows) {
            Map<String, Object> obj = new LinkedHashMap<>();
            Map<String, SemanticFieldValueDTO<Object>> fields = row.getFields();
            if (fields != null) {
                for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
                    obj.put(e.getKey(), e.getValue() == null ? null : e.getValue().getRawValue());
                }
            }
            list.add(obj);
        }
        return list;
    }

    public List<Map<String, Object>> getConnectorRawListForJson(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValueList() == null) return null;
        List<SemanticRowValueDTO> rows = relation.getRowValueList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (SemanticRowValueDTO row : rows) {
            Map<String, Object> obj = new LinkedHashMap<>();
            Map<String, SemanticFieldValueDTO<Object>> fields = row.getFields();
            if (fields != null) {
                for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : fields.entrySet()) {
                    obj.put(e.getKey(), e.getValue() == null ? null : e.getValue().getRawValueForJson());
                }
            }
            list.add(obj);
        }
        return list;
    }

    public List<Map<String, SemanticFieldValueDTO<Object>>> getConnectorDTOList(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValueList() == null) return null;
        List<SemanticRowValueDTO> rows = relation.getRowValueList();
        List<Map<String, SemanticFieldValueDTO<Object>>> list = new ArrayList<>();
        for (SemanticRowValueDTO row : rows) {
            Map<String, SemanticFieldValueDTO<Object>> fields = row.getFields();
            list.add(fields == null ? new LinkedHashMap<>() : fields);
        }
        return list;
    }

    public SemanticConnectorCardinalityEnum getConnectorCardinality(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null) return null;
        if (relation.getRowValueList() != null) return SemanticConnectorCardinalityEnum.MANY;
        if (relation.getRowValue() != null) return SemanticConnectorCardinalityEnum.ONE;
        return null;
    }

    public boolean isConnectorOne(String connectorName) {
        return SemanticConnectorCardinalityEnum.ONE == getConnectorCardinality(connectorName);
    }

    public boolean isConnectorMany(String connectorName) {
        return SemanticConnectorCardinalityEnum.MANY == getConnectorCardinality(connectorName);
    }
}
