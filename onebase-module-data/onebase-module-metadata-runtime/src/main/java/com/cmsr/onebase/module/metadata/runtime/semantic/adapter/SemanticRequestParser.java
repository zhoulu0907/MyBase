package com.cmsr.onebase.module.metadata.runtime.semantic.adapter;

import java.util.*;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordContextDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRelationValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRowValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticSortRuleDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticSortDirectionEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticPageBodyVO;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;

@Component
/**
 * 语义请求解析器
 *
 * <p>负责将语义化 VO（Merge/Target/Page/Record）解析为统一的 {@code RecordDTO}，
 * 并填充实体模型、上下文与值模型（包含连接器）。</p>
 */
public class SemanticRequestParser {

    @Resource
    private MetadataEntityFieldCoreService fieldCoreService;

    @Resource
    private MetadataEntityRelationshipRepository relationshipRepository;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    /**
     * 通用解析入口：将语义请求体解析为 RecordDTO
     * @param entityId 实体ID
     * @param body 语义化请求体（支持顶层键/分页/包含控制）
     * @param menuId 菜单ID（上下文）
     * @param traceId 链路追踪ID
     * @return 统一的记录承载对象
     */
    private SemanticRecordDTO parseFromProps(MetadataBusinessEntityDO entity, Map<String, Object> props, SemanticRecordContextDTO context) {
        SemanticRecordDTO record = new SemanticRecordDTO();
        record.setRecordContext(context);

        Long entityId = entity.getId();
        
        SemanticEntitySchemaDTO entitySchemaDTO = new SemanticEntitySchemaDTO();
        entitySchemaDTO.setId(entityId);
        entitySchemaDTO.setTableName(entity.getTableName());
        entitySchemaDTO.setCode(entity.getCode());
        entitySchemaDTO.setDisplayName(entity.getDisplayName());
        entitySchemaDTO.setDatasourceId(entity.getDatasourceId());
        entitySchemaDTO.setConnectors(buildConnectorSchemas(entityId));
        record.setEntitySchema(entitySchemaDTO);

        SemanticEntityValueDTO valueModel = new SemanticEntityValueDTO();
        Map<String, SemanticFieldValueDTO<Object>> data = new HashMap<>();
        Map<String, SemanticRelationValueDTO> connectors = new HashMap<>();

        Set<String> fieldNames = getFieldNameSet(entityId);
        Map<String, Object> p = props == null ? Map.of() : props;
        for (Map.Entry<String, Object> e : p.entrySet()) {
            String key = e.getKey();
            Object raw = e.getValue();
            if (fieldNames.contains(key) || Objects.equals(key, "id") || Objects.equals(key, "deleted")) {
                SemanticFieldValueDTO<Object> v = SemanticFieldValueDTO.<Object>of(guessTypeEnum(raw));
                v.setRawValue(raw);
                data.put(key, v);
            } else {
                SemanticRelationValueDTO cv = toConnectorValue(raw);
                connectors.put(key, cv);
            }
        }
        valueModel.setFieldValueMap(data);
        valueModel.setConnectors(connectors);
        record.setEntityValue(valueModel);
        return record;
    }

    /**
     * 解析合并请求体：用于创建/更新场景
     * @param entityId 实体ID
     * @param body 合并请求体
     * @param menuId 菜单ID
     * @param traceId 链路追踪ID
     * @return RecordDTO
     */
    public SemanticRecordDTO parseMerge(MetadataBusinessEntityDO entity, SemanticMergeBodyVO body, Long menuId, String traceId, SemanticMethodCodeEnum methodCode) {
        SemanticRecordContextDTO context = new SemanticRecordContextDTO();
        context.setMenuId(menuId);
        context.setTraceId(traceId);
        if (methodCode != null) {
            context.setMethodCode(methodCode);
            if (methodCode == SemanticMethodCodeEnum.CREATE) {
                context.setOperationType(com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.CREATE);
            } else if (methodCode == SemanticMethodCodeEnum.UPDATE) {
                context.setOperationType(com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.UPDATE);
            }
        }
        Map<String, Object> props = body == null ? Map.of() : body.getProperties();
        return parseFromProps(entity, props, context);
    }

    /**
     * 解析目标请求体：用于删除/详情场景
     * @param entityId 实体ID
     * @param body 目标请求体
     * @param menuId 菜单ID
     * @param traceId 链路追踪ID
     * @return RecordDTO
     */
    public SemanticRecordDTO parseTarget(MetadataBusinessEntityDO entity, SemanticTargetBodyVO body, Long menuId, String traceId, SemanticMethodCodeEnum methodCode) {
        SemanticRecordContextDTO context = new SemanticRecordContextDTO();
        context.setMenuId(menuId);
        context.setTraceId(traceId);
        if (body != null) {
            context.setContainRelation(body.getContainRelation());
            context.setContainSubTable(body.getContainSubTable());
            if (methodCode != null) {
                context.setMethodCode(methodCode);
                if (methodCode == SemanticMethodCodeEnum.DELETE) {
                    context.setOperationType(com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.DELETE);
                } else if (methodCode == SemanticMethodCodeEnum.GET) {
                    context.setOperationType(com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.GET);
                }
            }
        }
        Map<String, Object> props = body == null ? Map.of() : body.getData();
        return parseFromProps(entity, props, context);
    }

    /**
     * 解析分页请求体：用于分页查询场景
     * @param entityId 实体ID
     * @param body 分页请求体
     * @param menuId 菜单ID
     * @param traceId 链路追踪ID
     * @return RecordDTO
     */
    public SemanticRecordDTO parsePage(MetadataBusinessEntityDO entity, SemanticPageBodyVO body, Long menuId, String traceId, SemanticMethodCodeEnum methodCode) {
        SemanticRecordContextDTO context = new SemanticRecordContextDTO();
        context.setMenuId(menuId);
        context.setTraceId(traceId);
        if (body != null) {
            context.setPageNo(body.getPageNo());
            context.setPageSize(body.getPageSize());
            if (body.getSortField() != null) {
                SemanticSortRuleDTO rule = new SemanticSortRuleDTO();
                rule.setField(body.getSortField());
                if (body.getSortDirection() != null) {
                    try {
                        rule.setDirection(SemanticSortDirectionEnum.valueOf(body.getSortDirection()));
                    } catch (IllegalArgumentException ignore) {}
                }
                context.setSortBy(List.of(rule));
            }
            if (body.getFilters() != null) {
                context.setFilters(body.getFilters());
            }
            if (methodCode != null) {
                context.setMethodCode(methodCode);
                if (methodCode == SemanticMethodCodeEnum.GET_PAGE) {
                    context.setOperationType(com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.GET_PAGE);
                }
            }
        }
        Map<String, Object> props = body == null ? Map.of() : body.getData();
        return parseFromProps(entity, props, context);
    }

    

    /**
     * 获取实体的字段名集合
     * @param entityId 实体ID
     * @return 字段名集合
     */
    private Set<String> getFieldNameSet(Long entityId) {
        List<MetadataEntityFieldDO> fields = fieldCoreService.getEntityFieldListByEntityId(entityId);
        Set<String> set = new HashSet<>();
        for (MetadataEntityFieldDO f : fields) {
            if (f.getFieldName() != null) {
                set.add(f.getFieldName());
            }
        }
        return set;
    }

    /**
     * 构造连接器模型列表：依据主实体与关系配置生成
     * @param sourceEntityId 源实体ID
     * @return 连接器模型列表
     */
    private List<SemanticRelationSchemaDTO> buildConnectorSchemas(Long sourceEntityId) {
        List<MetadataEntityRelationshipDO> rels = relationshipRepository.getRelationshipsByMasterEntityId(sourceEntityId);
        List<SemanticRelationSchemaDTO> list = new ArrayList<>();
        if (rels == null) {
            return list;
        }
        for (MetadataEntityRelationshipDO r : rels) {
            SemanticRelationSchemaDTO s = new SemanticRelationSchemaDTO();
            s.setName(r.getRelationName());
            s.setSourceEntityId(r.getSourceEntityId());
            s.setTargetEntityId(r.getTargetEntityId());
            s.setSourceKeyFieldId(toLongSafe(r.getSourceFieldId()));
            s.setTargetKeyFieldId(toLongSafe(r.getTargetFieldId()));
            s.setType(resolveConnectorType(r));
            s.setRelationshipType(r.getRelationshipType());
            s.setCardinality(resolveCardinality(r));
            list.add(s);
        }
        return list;
    }

    /**
     * 安全转换为 Long
     * @param s 文本
     * @return Long 或 null
     */
    private Long toLongSafe(String s) {
        if (s == null) return null;
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 解析连接器类型：目标实体为中间表则为 SUBTABLE，否则为 RELATION
     * @param r 关系DO
     * @return 连接器类型
     */
    private SemanticConnectorTypeEnum resolveConnectorType(MetadataEntityRelationshipDO r) {
        var target = businessEntityCoreService.getBusinessEntity(r.getTargetEntityId());
        if (target != null && target.getEntityType() != null && target.getEntityType() == 3) {
            return SemanticConnectorTypeEnum.SUBTABLE;
        }
        return SemanticConnectorTypeEnum.RELATION;
    }

    /**
     * 解析连接器基数：ONE_TO_ONE/MANY_TO_ONE 视为 ONE，其余视为 MANY
     * @param r 关系DO
     * @return 基数枚举
     */
    private SemanticConnectorCardinalityEnum resolveCardinality(MetadataEntityRelationshipDO r) {
        String t = r.getRelationshipType();
        if (t == null) return SemanticConnectorCardinalityEnum.MANY;
        t = t.toUpperCase(Locale.ROOT);
        if ("ONE_TO_ONE".equals(t) || "MANY_TO_ONE".equals(t)) {
            return SemanticConnectorCardinalityEnum.ONE;
        }
        return SemanticConnectorCardinalityEnum.MANY;
    }

    /**
     * 解析连接器值：Map 视为单行，List<Map> 视为多行
     * @param raw 原始对象
     * @return 连接器值
     */
    private SemanticRelationValueDTO toConnectorValue(Object raw) {
        SemanticRelationValueDTO v = new SemanticRelationValueDTO();
        if (raw instanceof Map<?, ?> map) {
            SemanticRowValueDTO row = toRowValue(map);
            v.setRowValue(row);
        } else if (raw instanceof List<?> list) {
            List<SemanticRowValueDTO> rows = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    rows.add(toRowValue(m));
                }
            }
            v.setRowValueList(rows);
        }
        return v;
    }

    /**
     * 解析行值：提取 id/deleted 特殊键，其余写入字段集合
     * @param map 行映射
     * @return 行值对象
     */
    private SemanticRowValueDTO toRowValue(Map<?, ?> map) {
        SemanticRowValueDTO row = new SemanticRowValueDTO();
        Map<String, SemanticFieldValueDTO<Object>> fields = new HashMap<>();
        for (Map.Entry<?, ?> e : map.entrySet()) {
            String k = String.valueOf(e.getKey());
            Object val = e.getValue();
            if ("id".equals(k)) {
                row.setId(val);
            } else if ("deleted".equals(k)) {
                row.setDeleted(val == null ? null : Boolean.valueOf(String.valueOf(val)));
            } else {
                SemanticFieldValueDTO<Object> v = SemanticFieldValueDTO.<Object>of(guessTypeEnum(val));
                v.setRawValue(val);
                v.setFieldName(k);
                fields.put(k, v);
            }
        }
        row.setFields(fields);
        return row;
    }

    /**
     * 简单类型推断：Number/Boolean，否则默认 STRING
     * @param raw 原始值
     * @return 类型代码
     */
    private SemanticFieldTypeEnum guessTypeEnum(Object raw) {
        if (raw instanceof Number) return SemanticFieldTypeEnum.NUMBER;
        if (raw instanceof Boolean) return SemanticFieldTypeEnum.BOOLEAN;
        if (raw instanceof List<?>) return SemanticFieldTypeEnum.MULTI_SELECT;
        return SemanticFieldTypeEnum.TEXT;
    }
}
