package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordContextDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

/**
 * 合并请求装配器
 *
 * <p>负责将语义化合并请求体（主实体字段与连接器数据）解析并装配为统一的
 * {@code SemanticRecordDTO}，包含：上下文、实体模型（含连接器模型）与值模型。
 * 同时依据实体字段的元数据将字段值映射到 {@code SemanticFieldValueDTO}，并设置
 * {@code fieldTypeEnum}/{@code basicType} 等类型信息。</p>
 */
@Component
public class SemanticMergeRecordAssembler {

    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    @Resource
    private MetadataEntityFieldCoreService fieldCoreService;

    @Resource
    private MetadataEntityRelationshipRepository relationshipRepository;

    /**
     * 按实体编码装配合并请求
     *
     * @param entityCode 实体编码
     * @param body       合并请求体（顶层属性为主实体字段或连接器名）
     * @param menuId     菜单ID
     * @param traceId    链路追踪ID
     * @return 统一记录对象
     */
    public SemanticRecordDTO assemble(String entityCode, SemanticMergeBodyVO body, Long menuId, String traceId) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        return parseMerge(entity, body, menuId, traceId, SemanticMethodCodeEnum.CREATE);
    }

    /**
     * 解析合并请求体并设置上下文
     *
     * @param entity    业务实体DO
     * @param body      合并请求体
     * @param menuId    菜单ID
     * @param traceId   链路追踪ID
     * @param methodCode 方法枚举（CREATE/UPDATE）
     * @return 统一记录对象
     */
    private SemanticRecordDTO parseMerge(MetadataBusinessEntityDO entity, SemanticMergeBodyVO body, Long menuId, String traceId, SemanticMethodCodeEnum methodCode) {
        SemanticRecordContextDTO context = new SemanticRecordContextDTO();
        context.setMenuId(menuId);
        context.setTraceId(traceId);
        if (methodCode != null) {
            context.setMethodCode(methodCode);
            if (methodCode == SemanticMethodCodeEnum.CREATE) {
                context.setOperationType(MetadataDataMethodOpEnum.CREATE);
            } else if (methodCode == SemanticMethodCodeEnum.UPDATE) {
                context.setOperationType(MetadataDataMethodOpEnum.UPDATE);
            }
        }
        Map<String, Object> props = body == null ? Map.of() : body.getProperties();
        return parseFromProps(entity, props, context);
    }

    /**
     * 通用属性解析：构建实体模型与值模型
     *
     * @param entity  业务实体DO
     * @param props   顶层属性（字段名或连接器名）
     * @param context 记录上下文
     * @return 统一记录对象
     */
    private SemanticRecordDTO parseFromProps(MetadataBusinessEntityDO entity, Map<String, Object> props, SemanticRecordContextDTO context) {
        SemanticRecordDTO record = new SemanticRecordDTO();
        record.setRecordContext(context);

        Long entityId = entity.getId();

        SemanticEntitySchemaDTO entitySchema = new SemanticEntitySchemaDTO();
        entitySchema.setId(entityId);
        entitySchema.setTableName(entity.getTableName());
        entitySchema.setCode(entity.getCode());
        entitySchema.setDisplayName(entity.getDisplayName());
        entitySchema.setConnectors(buildConnectorSchemas(entityId));
        record.setEntitySchema(entitySchema);

        SemanticEntityValueDTO entityValue = new SemanticEntityValueDTO();
        Map<String, SemanticFieldValueDTO<Object>> entityFieldValues = new HashMap<>();
        Map<String, SemanticRelationValueDTO> relationValues = new HashMap<>();

        Set<String> entityFieldNameSet = getFieldNameSet(entityId);
        Map<String, MetadataEntityFieldDO> entityFieldMetaByName = buildFieldMetaMap(entityId);
        Map<String, Object> properties = props == null ? Map.of() : props;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propName = entry.getKey();
            Object propRawValue = entry.getValue();
            if (entityFieldNameSet.contains(propName) || Objects.equals(propName, "id") || Objects.equals(propName, "deleted")) {
                MetadataEntityFieldDO fieldMeta = entityFieldMetaByName.get(propName);
                SemanticFieldValueDTO<Object> fieldValueDto = toFieldValue(propRawValue, fieldMeta, propName);
                entityFieldValues.put(propName, fieldValueDto);
            } else {
                SemanticRelationValueDTO relationValueDto = toConnectorValue(propName, propRawValue, entitySchema.getConnectors());
                relationValues.put(propName, relationValueDto);
            }
        }
        entityValue.setFieldValueMap(entityFieldValues);
        entityValue.setConnectors(relationValues);
        record.setEntityValue(entityValue);
        return record;
    }

    private Set<String> getFieldNameSet(Long entityId) {
        List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
        Set<String> fieldNameSet = new HashSet<>();
        for (MetadataEntityFieldDO field : fields) {
            if (field.getFieldName() != null) {
                fieldNameSet.add(field.getFieldName());
            }
        }
        return fieldNameSet;
    }

    /**
     * 构建连接器模型列表
     *
     * @param sourceEntityId 源实体ID
     * @return 连接器模型集合
     */
    private List<SemanticRelationSchemaDTO> buildConnectorSchemas(Long sourceEntityId) {
        List<MetadataEntityRelationshipDO> relationships = relationshipRepository.getRelationshipsByMasterEntityId(sourceEntityId);
        List<SemanticRelationSchemaDTO> connectorSchemas = new ArrayList<>();
        if (relationships == null) {
            return connectorSchemas;
        }
        for (MetadataEntityRelationshipDO relationship : relationships) {
            SemanticRelationSchemaDTO schema = new SemanticRelationSchemaDTO();
            schema.setName(relationship.getRelationName());
            schema.setSourceEntityId(relationship.getSourceEntityId());
            schema.setTargetEntityId(relationship.getTargetEntityId());
            schema.setSourceKeyFieldId(toLongSafe(relationship.getSourceFieldId()));
            schema.setTargetKeyFieldId(toLongSafe(relationship.getTargetFieldId()));
            schema.setType(resolveConnectorType(relationship));
            schema.setRelationshipType(relationship.getRelationshipType());
            schema.setCardinality(resolveCardinality(relationship));
            connectorSchemas.add(schema);
        }
        return connectorSchemas;
    }

    private Long toLongSafe(String text) {
        if (text == null) return null;
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 解析连接器类型：目标实体为中间表则为 SUBTABLE，否则为 RELATION
     */
    private SemanticConnectorTypeEnum resolveConnectorType(MetadataEntityRelationshipDO relationship) {
        var target = businessEntityCoreService.getBusinessEntity(relationship.getTargetEntityId());
        if (target != null && target.getEntityType() != null && target.getEntityType() == 3) {
            return SemanticConnectorTypeEnum.SUBTABLE;
        }
        return SemanticConnectorTypeEnum.RELATION;
    }

    /**
     * 解析连接器基数：ONE_TO_ONE/MANY_TO_ONE 视为 ONE，其余视为 MANY
     */
    private SemanticConnectorCardinalityEnum resolveCardinality(MetadataEntityRelationshipDO relationship) {
        String relationshipType = relationship.getRelationshipType();
        if (relationshipType == null) return SemanticConnectorCardinalityEnum.MANY;
        relationshipType = relationshipType.toUpperCase(Locale.ROOT);
        if ("ONE_TO_ONE".equals(relationshipType) || "MANY_TO_ONE".equals(relationshipType)) {
            return SemanticConnectorCardinalityEnum.ONE;
        }
        return SemanticConnectorCardinalityEnum.MANY;
    }

    /**
     * 解析连接器值：Map 为单行，List<Map> 为多行；按连接器名匹配目标实体
     */
    private SemanticRelationValueDTO toConnectorValue(String connectorName, Object raw, List<SemanticRelationSchemaDTO> connectorSchemas) {
        SemanticRelationValueDTO relationValue = new SemanticRelationValueDTO();
        Long targetEntityId = null;
        if (connectorSchemas != null) {
            for (SemanticRelationSchemaDTO schema : connectorSchemas) {
                if (Objects.equals(connectorName, schema.getName())) {
                    targetEntityId = schema.getTargetEntityId();
                    break;
                }
            }
        }
        if (raw instanceof Map<?, ?> map) {
            SemanticRowValueDTO rowValue = toRowValue(targetEntityId, map);
            relationValue.setRowValue(rowValue);
        } else if (raw instanceof List<?> list) {
            List<SemanticRowValueDTO> rowValues = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    rowValues.add(toRowValue(targetEntityId, m));
                }
            }
            relationValue.setRowValueList(rowValues);
        }
        return relationValue;
    }

    /**
     * 解析一行连接器数据：提取 id/deleted，其余字段根据目标实体字段元数据映射为字段值DTO
     */
    private SemanticRowValueDTO toRowValue(Long targetEntityId, Map<?, ?> map) {
        SemanticRowValueDTO rowValue = new SemanticRowValueDTO();
        Map<String, SemanticFieldValueDTO<Object>> rowFieldValues = new HashMap<>();
        Map<String, MetadataEntityFieldDO> targetFieldMetaByName = targetEntityId == null ? Collections.emptyMap() : buildFieldMetaMap(targetEntityId);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String fieldName = String.valueOf(entry.getKey());
            Object fieldRawValue = entry.getValue();
            if ("id".equals(fieldName)) {
                rowValue.setId(fieldRawValue);
            } else if ("deleted".equals(fieldName)) {
                rowValue.setDeleted(fieldRawValue == null ? null : Boolean.valueOf(String.valueOf(fieldRawValue)));
            } else {
                MetadataEntityFieldDO fieldMeta = targetFieldMetaByName.get(fieldName);
                rowFieldValues.put(fieldName, toFieldValue(fieldRawValue, fieldMeta, fieldName));
            }
        }
        rowValue.setFields(rowFieldValues);
        return rowValue;
    }

    /**
     * 构造字段值DTO：设置原始值、字段标识与类型枚举/基础类型
     */
    private SemanticFieldValueDTO<Object> toFieldValue(Object raw, MetadataEntityFieldDO entityFieldDO, String fieldName) {
        SemanticFieldValueDTO<Object> v = new SemanticFieldValueDTO<>();
        v.setRawValue(raw);
        v.setFieldId(entityFieldDO == null ? null : entityFieldDO.getId());
        v.setFieldName(entityFieldDO == null ? fieldName : entityFieldDO.getFieldName());

        SemanticFieldTypeEnum typeEnum = null;
        if (entityFieldDO != null && entityFieldDO.getFieldType() != null) {
            typeEnum = SemanticFieldTypeEnum.ofCode(entityFieldDO.getFieldType());
        }
        if (typeEnum == null) {
            typeEnum = guessEnumByRaw(raw);
        }
        v.setFieldTypeEnum(typeEnum);
        return v;
    }

    /**
     * 根据原始值类型进行枚举推断（元数据缺失时兜底）
     */
    private SemanticFieldTypeEnum guessEnumByRaw(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Boolean) return SemanticFieldTypeEnum.BOOLEAN;
        if (raw instanceof java.time.LocalDate) return SemanticFieldTypeEnum.DATE;
        if (raw instanceof java.time.LocalDateTime) return SemanticFieldTypeEnum.DATETIME;
        if (raw instanceof Number) return SemanticFieldTypeEnum.NUMBER;
        if (raw instanceof java.util.List<?>) return SemanticFieldTypeEnum.MULTI_SELECT;
        return SemanticFieldTypeEnum.TEXT;
    }

    /**
     * 构建字段名到字段元数据的映射
     */
    private Map<String, MetadataEntityFieldDO> buildFieldMetaMap(Long entityId) {
        List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
        Map<String, MetadataEntityFieldDO> map = new HashMap<>();
        if (fields != null) {
            for (MetadataEntityFieldDO f : fields) {
                if (f.getFieldName() != null) {
                    map.put(f.getFieldName(), f);
                }
            }
        }
        return map;
    }
}
