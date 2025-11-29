package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 值模型 DTO
 * <p>承载主实体字段值与连接器值。</p>
 */
@Schema(description = "值模型 DTO")
@Data
public class SemanticEntityValueDTO {
    @Schema(description = "主实体字段值")
    private Map<String, SemanticFieldValueDTO<Object>> fieldValueMap;

    @Schema(description = "关联对象值")
    private Map<String, SemanticRelationValueDTO> connectors;

    /**
     * 根据字段ID获取当前实体字段值DTO
     *
     * @param fieldId 字段ID
     * @return 字段值DTO；不存在时返回null
     */
    public SemanticFieldValueDTO<Object> getFieldValueById(Long fieldId) {
        if (fieldValueMap == null || fieldId == null) return null;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> entry : fieldValueMap.entrySet()) {
            SemanticFieldValueDTO<Object> v = entry.getValue();
            if (v != null && fieldId.equals(v.getFieldId())) return v;
        }
        return null;
    }

    /**
     * 根据字段UUID获取当前实体字段值DTO
     *
     * @param fieldUuid 字段UUID
     * @return 字段值DTO；不存在时返回null
     */
    public SemanticFieldValueDTO<Object> getFieldValueByUuid(String fieldUuid) {
        if (fieldValueMap == null || fieldUuid == null) return null;
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> entry : fieldValueMap.entrySet()) {
            SemanticFieldValueDTO<Object> v = entry.getValue();
            if (v != null && fieldUuid.equals(v.getFieldUuid())) return v;
        }
        return null;
    }

    /**
     * 获取单行关联的指定字段值DTO
     *
     * @param connectorName 连接器名称
     * @param fieldName 字段名称
     * @return 字段值DTO；非ONE或不存在时返回null
     */
    public SemanticFieldValueDTO<Object> getConnectorFieldValue(String connectorName, String fieldName) {
        if (connectorName == null || fieldName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValue() == null) return null;
        Map<String, SemanticFieldValueDTO<Object>> fields = relation.getRowValue().getFields();
        if (fields == null) return null;
        return fields.get(fieldName);
    }

    /**
     * 获取多行关联在指定下标的字段值DTO
     *
     * @param connectorName 连接器名称
     * @param index 行下标
     * @param fieldName 字段名称
     * @return 字段值DTO；非MANY或越界或不存在时返回null
     */
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

    /**
     * 按表名与字段名获取字段值DTO（优先关联后回退主实体）
     *
     * @param tableName 表名（连接器名）；为空则仅查主实体
     * @param fieldName 字段名
     * @return 字段值DTO；不存在时返回null
     */
    public SemanticFieldValueDTO<Object> getFieldValueByTableAndField(String tableName, String fieldName) {
        if (fieldName == null) return null;
        if (connectors != null && tableName != null) {
            SemanticFieldValueDTO<Object> v = getConnectorFieldValue(tableName, fieldName);
            if (v != null) return v;
        }
        if (fieldValueMap == null) return null;
        return fieldValueMap.get(fieldName);
    }

    /**
     * 按表名与下标获取列表关联的字段值DTO
     *
     * @param tableName 表名（连接器名）
     * @param index 行下标
     * @param fieldName 字段名
     * @return 字段值DTO；不存在时返回null
     */
    public SemanticFieldValueDTO<Object> getFieldValueByTableAndFieldAt(String tableName, int index, String fieldName) {
        if (tableName == null || fieldName == null) return null;
        return getConnectorFieldValueAt(tableName, index, fieldName);
    }

    /**
     * 获取当前实体（主表）原始值Map
     *
     * <p>键为字段名，值为原始值；不含关联对象。</p>
     */
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

    /**
     * 获取全局原始值结构
     *
     * <p>主实体字段平铺在顶层；连接器以名称作为键：
     * - ONE：返回 Map（字段名→原始值）
     * - MANY：返回 List<Map>（每个元素为字段名→原始值）
     * </p>
     */
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

    /**
     * 获取单行关联的原始值Map
     *
     * @param connectorName 连接器名称
     * @return 字段名→原始值的Map；非ONE或不存在时返回null
     */
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

    /**
     * 获取单行关联的字段值 DTO Map
     *
     * <p>当连接器基数为 ONE 时，返回该行的字段名到 {@code SemanticFieldValueDTO} 的映射；
     * 否则返回 {@code null}。</p>
     *
     * @param connectorName 连接器名称
     * @return 单行字段值 DTO Map；不是单行或不存在时返回 {@code null}
     */
    public Map<String, SemanticFieldValueDTO<Object>> getConnectorDTOObject(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null || relation.getRowValue() == null) return null;
        SemanticRowValueDTO row = relation.getRowValue();
        return row.getFields();
    }

    /**
     * 获取多行关联的原始值列表
     *
     * @param connectorName 连接器名称
     * @return List，每个元素为字段名→原始值的Map；非MANY或不存在时返回null
     */
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

    /**
     * 获取连接器基数（ONE/MANY）
     *
     * @param connectorName 连接器名称
     * @return 基数枚举；未知或不存在时返回null
     */
    public SemanticConnectorCardinalityEnum getConnectorCardinality(String connectorName) {
        if (connectorName == null || connectors == null) return null;
        SemanticRelationValueDTO relation = connectors.get(connectorName);
        if (relation == null) return null;
        if (relation.getRowValueList() != null) return SemanticConnectorCardinalityEnum.MANY;
        if (relation.getRowValue() != null) return SemanticConnectorCardinalityEnum.ONE;
        return null;
    }

    /**
     * 判断连接器是否为ONE基数
     */
    public boolean isConnectorOne(String connectorName) {
        return SemanticConnectorCardinalityEnum.ONE == getConnectorCardinality(connectorName);
    }

    /**
     * 判断连接器是否为MANY基数
     */
    public boolean isConnectorMany(String connectorName) {
        return SemanticConnectorCardinalityEnum.MANY == getConnectorCardinality(connectorName);
    }
}
