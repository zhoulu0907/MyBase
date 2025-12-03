package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.framework.common.util.object.ObjectUtils;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldOptionRepository;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordContextDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticPageBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticSortDirectionEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import com.mybatisflex.core.query.QueryWrapper;

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

    @Resource
    private MetadataEntityFieldOptionRepository fieldOptionRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    /**
     * 按实体编码装配合并请求
     *
     * @param tableName  表名称
     * @param body       合并请求体（顶层属性为主实体字段或连接器名）
     * @param menuId     菜单ID
     * @param traceId    链路追踪ID
     * @return 统一记录对象
     */
    public SemanticRecordDTO assembleMergeBody(String tableName,
                                               SemanticMergeBodyVO body,
                                               Long menuId,
                                               String traceId,
                                               SemanticMethodCodeEnum methodCode,
                                               MetadataDataMethodOpEnum operationType) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByTableName(tableName);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        return parseMerge(entity, body, menuId, traceId, methodCode, operationType);
    }

    /**
     * 按表名装配分页请求体
     */
    public SemanticRecordDTO assemblePageBody(String tableName, SemanticPageBodyVO body, Long menuId, String traceId,
                                              SemanticMethodCodeEnum methodCode, MetadataDataMethodOpEnum operationType) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByTableName(tableName);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        MetadataDataMethodOpEnum op = operationType == null ? MetadataDataMethodOpEnum.GET_PAGE : operationType;
        SemanticRecordContextDTO context = buildContext(menuId, traceId, SemanticMethodCodeEnum.GET_PAGE, op);
        if (body != null) {
            context.setPageNo(body.getPageNo());
            context.setPageSize(body.getPageSize());
            context.setFilters(body.getFilters());
            if (body.getSortBy() != null && !body.getSortBy().isEmpty()) {
                context.setSortBy(body.getSortBy());
            }
        }
        SemanticRecordDTO record = new SemanticRecordDTO();
        record.setRecordContext(context);
        record.setEntitySchema(buildEntitySchema(entity));
        return record;
    }

    /**
     * 按表名装配目标请求体（详情/删除）
     */
    public SemanticRecordDTO assembleTargetBody(String tableName, SemanticTargetBodyVO body, Long menuId, String traceId,
                                                SemanticMethodCodeEnum methodCode,
                                                MetadataDataMethodOpEnum operationType) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByTableName(tableName);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        SemanticMethodCodeEnum method = methodCode == null ? SemanticMethodCodeEnum.GET : methodCode;
        MetadataDataMethodOpEnum op = operationType == null ? ((method == SemanticMethodCodeEnum.DELETE) ? MetadataDataMethodOpEnum.DELETE : MetadataDataMethodOpEnum.GET) : operationType;
        SemanticRecordContextDTO context = buildContext(menuId, traceId, method, op);
        context.setContainSubTable(body == null ? null : body.getContainSubTable());
        context.setContainRelation(body == null ? null : body.getContainRelation());
        SemanticRecordDTO record = new SemanticRecordDTO();
        record.setRecordContext(context);
        record.setEntitySchema(buildEntitySchema(entity));
        SemanticEntityValueDTO value = new SemanticEntityValueDTO();
        if (body != null) { value.setId(body.getId()); }
        record.setEntityValue(value);
        return record;
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
    private SemanticRecordDTO parseMerge(MetadataBusinessEntityDO entity,
                                         SemanticMergeBodyVO body,
                                         Long menuId,
                                         String traceId,
                                         SemanticMethodCodeEnum methodCode,
                                         MetadataDataMethodOpEnum operationType) {
        SemanticRecordContextDTO context = buildContext(menuId, traceId, methodCode, operationType);
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
        List<MetadataEntityFieldDO> entityFields = fieldCoreService.getEntityFieldListByEntityId(entityId);

        SemanticEntitySchemaDTO entitySchema = buildEntitySchema(entity);
        record.setEntitySchema(entitySchema);

        SemanticEntityValueDTO entityValue = new SemanticEntityValueDTO();
        Map<String, SemanticFieldValueDTO<Object>> entityFieldValues = new HashMap<>();
        Map<String, SemanticRelationValueDTO> relationValues = new HashMap<>();

        Set<String> entityFieldNameSet = getFieldNameSet(entityFields);
        Map<String, MetadataEntityFieldDO> entityFieldMetaByName = buildFieldMetaMap(entityFields);
        Map<String, Object> properties = props == null ? Map.of() : props;
        properties.forEach((propName, propRawValue) -> {
            if (entityFieldNameSet.contains(propName) || "id".equals(propName)) {
                entityFieldValues.put(propName, toFieldValue(propRawValue, entityFieldMetaByName.get(propName), propName));
            } else {
                relationValues.put(propName, toConnectorValue(propName, propRawValue, entitySchema.getConnectors()));
            }
        });
        entityValue.setFieldValueMap(entityFieldValues);
        entityValue.setConnectors(relationValues);
        entityValue.setId(properties.get("id"));
        record.setEntityValue(entityValue);
        return record;
    }

    private SemanticEntitySchemaDTO buildEntitySchema(MetadataBusinessEntityDO entity) {
        SemanticEntitySchemaDTO entitySchema = new SemanticEntitySchemaDTO();
        entitySchema.setId(entity.getId());
        entitySchema.setTableName(entity.getTableName());
        entitySchema.setCode(entity.getCode());
        entitySchema.setDisplayName(entity.getDisplayName());
        entitySchema.setDatasourceId(entity.getDatasourceId());
        List<MetadataEntityFieldDO> entityFields = fieldCoreService.getEntityFieldListByEntityId(entity.getId());
        entitySchema.setFields(buildFieldSchemas(entityFields));
        entitySchema.setConnectors(buildConnectorSchemas(entity.getId()));
        return entitySchema;
    }

    private SemanticRecordContextDTO buildContext(Long menuId, String traceId,
                                                  SemanticMethodCodeEnum methodCode,
                                                  MetadataDataMethodOpEnum operationType) {
        SemanticRecordContextDTO context = new SemanticRecordContextDTO();
        context.setMenuId(menuId);
        context.setTraceId(traceId);
        if (methodCode != null) { context.setMethodCode(methodCode); }
        if (operationType != null) { context.setOperationType(operationType); }
        return context;
    }


    private Set<String> getFieldNameSet(List<MetadataEntityFieldDO> fields) {
        Set<String> fieldNameSet = new HashSet<>();
        if (fields == null) return fieldNameSet;
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
        if (relationships == null || relationships.isEmpty()) { return connectorSchemas; }

        Set<Long> targetIds = new HashSet<>();
        for (MetadataEntityRelationshipDO r : relationships) { if (r.getTargetEntityId() != null) { targetIds.add(r.getTargetEntityId()); } }

        Map<Long, MetadataBusinessEntityDO> targetEntityMap = new HashMap<>();
        if (!targetIds.isEmpty()) {
            QueryWrapper qw = new QueryWrapper();
            qw.in(MetadataBusinessEntityDO::getId, targetIds);
            List<MetadataBusinessEntityDO> targets = businessEntityCoreService.findAllByConfig(qw);
            if (targets != null) {
                for (MetadataBusinessEntityDO t : targets) { if (t != null && t.getId() != null) { targetEntityMap.put(t.getId(), t); } }
            }
        }

        Map<Long, List<SemanticFieldSchemaDTO>> relationAttrsByTargetId = new HashMap<>();
        if (!targetIds.isEmpty()) {
            QueryWrapper fqw = new QueryWrapper();
            fqw.in(MetadataEntityFieldDO::getEntityId, targetIds);
            List<MetadataEntityFieldDO> allTargetFields = entityFieldRepository.list(fqw);
            List<SemanticFieldSchemaDTO> allSchemas = buildFieldSchemas(allTargetFields);
            Map<Long, Long> fieldIdToEntityId = new HashMap<>();
            for (MetadataEntityFieldDO f : allTargetFields) {
                if (f.getId() != null && f.getEntityId() != null) { fieldIdToEntityId.put(f.getId(), f.getEntityId()); }
            }
            for (SemanticFieldSchemaDTO s : allSchemas) {
                Long fid = s.getId();
                Long eid = fid == null ? null : fieldIdToEntityId.get(fid);
                if (eid == null) { continue; }
                relationAttrsByTargetId.computeIfAbsent(eid, k -> new ArrayList<>()).add(s);
            }
        }

        for (MetadataEntityRelationshipDO relationship : relationships) {
            SemanticRelationSchemaDTO schema = new SemanticRelationSchemaDTO();
            schema.setRelationName(relationship.getRelationName());
            schema.setSourceEntityId(relationship.getSourceEntityId());
            schema.setTargetEntityId(relationship.getTargetEntityId());
            schema.setSourceKeyFieldId(relationship.getSourceFieldId());
            schema.setTargetKeyFieldId(relationship.getTargetFieldId());
            schema.setSelectFieldId(relationship.getSelectFieldId() != null ? String.valueOf(relationship.getSelectFieldId()) : null);
            schema.setRelationshipType(RelationshipTypeEnum.getByType(relationship.getRelationshipType()));
            schema.setCardinality(resolveCardinality(relationship));

            MetadataBusinessEntityDO target = relationship.getTargetEntityId() == null ? null : targetEntityMap.get(relationship.getTargetEntityId());
            if (target != null) {
                schema.setTargetEntityCode(target.getCode());
                schema.setTargetEntityDisplayName(target.getDisplayName());
                schema.setTargetEntityTableName(target.getTableName());
            }
            List<SemanticFieldSchemaDTO> relationAttrs = relationship.getTargetEntityId() == null ? Collections.emptyList() : relationAttrsByTargetId.getOrDefault(relationship.getTargetEntityId(), Collections.emptyList());
            schema.setRelationAttributes(relationAttrs);
            connectorSchemas.add(schema);
        }
        return connectorSchemas;
    }

    private List<SemanticFieldSchemaDTO> buildFieldSchemas(List<MetadataEntityFieldDO> fields) {
        List<SemanticFieldSchemaDTO> list = new ArrayList<>();
        if (fields == null) { return list; }
        Map<Long, SemanticFieldSchemaDTO> byId = new HashMap<>();
        for (MetadataEntityFieldDO f : fields) {
            SemanticFieldSchemaDTO s = new SemanticFieldSchemaDTO();
            s.setId(f.getId());
            s.setFieldCode(f.getFieldCode());
            s.setFieldName(f.getFieldName());
            s.setDisplayName(f.getDisplayName());
            s.setFieldType(f.getFieldType());
            s.setDataLength(f.getDataLength());
            s.setDecimalPlaces(f.getDecimalPlaces());
            s.setIsRequired(f.getIsRequired() == null ? null : f.getIsRequired() == 1);
            s.setIsUnique(f.getIsUnique() == null ? null : f.getIsUnique() == 1);
            s.setIsSystemField(f.getIsSystemField() == null ? null : f.getIsSystemField() == 1);
            s.setIsPrimaryKey(f.getIsPrimaryKey() == null ? null : f.getIsPrimaryKey() == 1);
            s.setDictTypeId(f.getDictTypeId());
            list.add(s);
            if (s.getId() != null) { byId.put(s.getId(), s); }
        }
        List<Long> selectFieldIds = new ArrayList<>();
        for (SemanticFieldSchemaDTO s : list) {
            if ((s.getFieldTypeEnum() == SemanticFieldTypeEnum.SELECT
                    || s.getFieldTypeEnum() == SemanticFieldTypeEnum.MULTI_SELECT)
                    && ObjectUtils.isBlank(s.getDictTypeId())) {
                if (s.getDictTypeId() == null && s.getId() != null) {
                     selectFieldIds.add(s.getId()); 
                }
            }
        }
        if (!selectFieldIds.isEmpty()) {
            var options = fieldOptionRepository.findAllByFieldIds(selectFieldIds);
            if (options != null) {
                for (var opt : options) {
                    Long fid = opt.getFieldId();
                    SemanticFieldSchemaDTO s = fid == null ? null : byId.get(fid);
                    if (s == null) { continue; }
                    if (s.getFieldOptions() == null) { s.setFieldOptions(new ArrayList<>()); }
                    s.getFieldOptions().add(opt);
                }
            }
        }
        return list;
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
     * 解析连接器基数：ONE_TO_ONE/MANY_TO_ONE 视为 ONE，其余视为 MANY
     */
    private SemanticConnectorCardinalityEnum resolveCardinality(MetadataEntityRelationshipDO relationship) {
        String t = relationship.getRelationshipType();
        if (t == null) return SemanticConnectorCardinalityEnum.MANY;
        if (RelationshipTypeEnum.isOneToOneType(t) || RelationshipTypeEnum.isManyToOneType(t)) {
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
                if (Objects.equals(connectorName, schema.getTargetEntityTableName())) {
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
        SemanticFieldTypeEnum typeEnum = null;
        if (entityFieldDO != null && entityFieldDO.getFieldType() != null) {
            typeEnum = SemanticFieldTypeEnum.ofCode(entityFieldDO.getFieldType());
        }
        if (typeEnum == null) {
            typeEnum = guessEnumByRaw(raw);
        }
        if (typeEnum == null) {
            typeEnum = SemanticFieldTypeEnum.TEXT;
        }
        SemanticFieldValueDTO<Object> fieldValueDTO = SemanticFieldValueDTO.<Object>ofType(typeEnum);
        fieldValueDTO.setRawValue(raw);
        fieldValueDTO.setFieldId(entityFieldDO == null ? null : entityFieldDO.getId());
        fieldValueDTO.setFieldName(entityFieldDO == null ? fieldName : entityFieldDO.getFieldName());
        return fieldValueDTO;
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
        return buildFieldMetaMap(fields);
    }

    private Map<String, MetadataEntityFieldDO> buildFieldMetaMap(List<MetadataEntityFieldDO> fields) {
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
