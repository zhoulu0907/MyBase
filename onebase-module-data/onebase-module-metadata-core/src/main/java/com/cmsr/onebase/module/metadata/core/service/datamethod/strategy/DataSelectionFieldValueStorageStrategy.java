package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据选择字段存储策略
 * DATA_SELECTION 和 MULTI_DATA_SELECTION 字段类型使用关联表存储选择的数据
 * 存储时：将选择的数据ID存储到关联表，字段本身存储为空或占位符
 * 读取时：从关联表查询并返回选择的数据ID列表
 *
 * @author matianyu
 * @date 2025-11-15
 */
@Slf4j
@Component
public class DataSelectionFieldValueStorageStrategy implements FieldValueStorageStrategy {

    private static final String DEFAULT_ID_KEY = "id";
    private static final String FALLBACK_ID_KEY = "value";

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;
    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;
    @Resource
    private com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService businessEntityService;
    @Resource
    private UidGenerator uidGenerator;

    @Override
    public boolean supports(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return false;
        }
        String upperType = fieldType.toUpperCase();
        return "DATA_SELECTION".equals(upperType) || "MULTI_DATA_SELECTION".equals(upperType);
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode) {
        if (mode == FieldValueTransformMode.READ) {
            // 读取模式：返回原始值，关联表的查询在外部处理
            return rawValue;
        }
        // 存储模式：提取ID列表，用于后续创建关联表记录
        // 字段本身存储为空或占位符，实际数据存储在关联表中
        List<String> ids = extractIdentifiers(rawValue);
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        // 返回逗号分隔的ID字符串，用于后续处理关联表
        return String.join(",", ids);
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode, ProcessContext context, MetadataEntityFieldDO field) {
        if (context == null || field == null) {
            // 如果没有上下文，回退到基础方法
            return transform(rawValue, mode);
        }

        if (mode == FieldValueTransformMode.STORE) {
            // 存储模式：处理关联表记录
            return handleStore(rawValue, context, field);
        } else {
            // 读取模式：从关联表查询并返回数据ID列表
            return handleRead(context, field);
        }
    }

    @Override
    public int getOrder() {
        // 优先级高于 ComplexObjectFieldValueStorageStrategy，确保 DATA_SELECTION 优先匹配
        return -1;
    }

    /**
     * 从原始值中提取数据ID列表
     *
     * @param rawValue 原始值
     * @return ID列表
     */
    public List<String> extractIdentifiers(Object rawValue) {
        if (rawValue == null) {
            return new ArrayList<>();
        }

        if (rawValue instanceof String stringValue) {
            if (!StringUtils.hasText(stringValue)) {
                return new ArrayList<>();
            }
            String trimmed = stringValue.trim();
            if (trimmed.startsWith("[")) {
                return parseJsonArray(trimmed);
            }
            if (trimmed.startsWith("{")) {
                return parseJsonObject(trimmed);
            }
            if (!trimmed.contains(",")) {
                return List.of(trimmed);
            }
            String[] segments = trimmed.split(",");
            return collectFromArray(segments);
        }

        if (rawValue instanceof Map<?, ?> mapValue) {
            String identifier = extractIdFromMap(mapValue);
            return StringUtils.hasText(identifier) ? List.of(identifier) : new ArrayList<>();
        }

        if (rawValue instanceof Collection<?> collection) {
            return collection.stream()
                    .map(this::extractIdentifierFromElement)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        return List.of(rawValue.toString());
    }

    private List<String> collectFromArray(String[] values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                result.add(value.trim());
            }
        }
        return result;
    }

    private List<String> parseJsonArray(String json) {
        try {
            List<?> array = JsonUtils.parseArray(json, Object.class);
            if (CollectionUtils.isEmpty(array)) {
                return new ArrayList<>();
            }
            return array.stream()
                    .map(this::extractIdentifierFromElement)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private List<String> parseJsonObject(String json) {
        try {
            Map<?, ?> map = JsonUtils.parseObject(json, Map.class);
            String identifier = extractIdFromMap(map);
            return StringUtils.hasText(identifier) ? List.of(identifier.trim()) : new ArrayList<>();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private String extractIdentifierFromElement(Object element) {
        if (element == null) {
            return null;
        }
        if (element instanceof Map<?, ?> map) {
            return extractIdFromMap(map);
        }
        return element.toString();
    }

    private String extractIdFromMap(Map<?, ?> map) {
        Object idValue = map.get(DEFAULT_ID_KEY);
        if (idValue == null) {
            idValue = map.get(FALLBACK_ID_KEY);
        }
        return idValue == null ? null : idValue.toString();
    }

    /**
     * 处理存储模式：创建或更新关联表记录
     *
     * @param rawValue 原始值（包含选择的数据ID）
     * @param context 处理上下文
     * @param field   字段定义
     * @return 字段值（存储时返回null，因为实际数据存储在关联表中）
     */
    private Object handleStore(Object rawValue, ProcessContext context, MetadataEntityFieldDO field) {
        // 获取当前记录ID
        Object recordId = context.getId();
        if (recordId == null) {
            log.warn("DATA_SELECTION 字段存储时，记录ID为空，字段：{}", field.getFieldName());
            return null;
        }

        // 提取选择的数据ID列表
        List<String> selectedDataIds = extractIdentifiers(rawValue);

        // 获取关联表服务
        AnylineService<?> temporaryService = context.getTemporaryService();
        if (temporaryService == null) {
            log.warn("DATA_SELECTION 字段存储时，temporaryService 为空，字段：{}", field.getFieldName());
            return null;
        }

        // 查询关联关系：通过源字段ID查找关联关系
        MetadataEntityRelationshipDO relationship = findRelationshipBySourceFieldId(field.getId(), context.getEntityId());
        if (relationship == null) {
            log.warn("DATA_SELECTION 字段存储时，未找到关联关系，字段ID：{}，实体ID：{}", field.getId(), context.getEntityId());
            return null;
        }

        // 获取目标实体和目标字段信息
        MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationship.getTargetEntityId());
        if (targetEntity == null) {
            log.warn("DATA_SELECTION 字段存储时，目标实体不存在，目标实体ID：{}", relationship.getTargetEntityId());
            return null;
        }

        MetadataEntityFieldDO targetField = entityFieldRepository.findById(Long.valueOf(relationship.getTargetFieldId()));
        if (targetField == null) {
            log.warn("DATA_SELECTION 字段存储时，目标字段不存在，目标字段ID：{}", relationship.getTargetFieldId());
            return null;
        }

        String targetTableName = targetEntity.getTableName();
        String targetFieldName = targetField.getFieldName();

        // 在租户上下文中执行关联表操作
        TenantUtils.executeIgnore(() -> {
            // 先清除目标实体表中，目标字段等于当前记录ID的所有记录（将目标字段设为null）
            clearRelationRecords(temporaryService, targetTableName, targetFieldName, recordId);

            // 如果新的选择列表不为空，更新目标实体表中被选择的数据记录
            if (!CollectionUtils.isEmpty(selectedDataIds)) {
                for (String selectedDataId : selectedDataIds) {
                    if (!StringUtils.hasText(selectedDataId)) {
                        continue;
                    }
                    updateTargetRecord(temporaryService, targetTableName, targetFieldName, selectedDataId, recordId);
                }
            }
        });

        // 字段本身存储为null，因为实际数据存储在关联表中
        return null;
    }

    /**
     * 处理读取模式：从关联表查询并返回数据ID列表
     *
     * @param context 处理上下文
     * @param field   字段定义
     * @return 数据ID列表（逗号分隔的字符串，或列表）
     */
    private Object handleRead(ProcessContext context, MetadataEntityFieldDO field) {
        // 获取当前记录ID
        Object recordId = context.getId();
        if (recordId == null) {
            log.warn("DATA_SELECTION 字段读取时，记录ID为空，字段：{}", field.getFieldName());
            return null;
        }

        // 获取关联表服务
        AnylineService<?> temporaryService = context.getTemporaryService();
        if (temporaryService == null) {
            log.warn("DATA_SELECTION 字段读取时，temporaryService 为空，字段：{}", field.getFieldName());
            return null;
        }

        // 查询关联关系：通过源字段ID查找关联关系
        MetadataEntityRelationshipDO relationship = findRelationshipBySourceFieldId(field.getId(), context.getEntityId());
        if (relationship == null) {
            log.warn("DATA_SELECTION 字段读取时，未找到关联关系，字段ID：{}，实体ID：{}", field.getId(), context.getEntityId());
            return null;
        }

        // 获取目标实体和目标字段信息
        MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationship.getTargetEntityId());
        if (targetEntity == null) {
            log.warn("DATA_SELECTION 字段读取时，目标实体不存在，目标实体ID：{}", relationship.getTargetEntityId());
            return null;
        }

        MetadataEntityFieldDO targetField = entityFieldRepository.findById(Long.valueOf(relationship.getTargetFieldId()));
        if (targetField == null) {
            log.warn("DATA_SELECTION 字段读取时，目标字段不存在，目标字段ID：{}", relationship.getTargetFieldId());
            return null;
        }

        String targetTableName = targetEntity.getTableName();
        String targetFieldName = targetField.getFieldName();

        // 在租户上下文中查询关联记录
        List<String> selectedDataIds = TenantUtils.executeIgnore(() -> {
            return queryRelationRecords(temporaryService, targetTableName, targetFieldName, recordId);
        });

        if (CollectionUtils.isEmpty(selectedDataIds)) {
            return null;
        }

        // 根据字段类型返回不同格式
        boolean isMulti = "MULTI_DATA_SELECTION".equalsIgnoreCase(field.getFieldType());
        if (isMulti) {
            // 多选：返回列表
            return selectedDataIds;
        } else {
            // 单选：返回第一个ID
            return selectedDataIds.get(0);
        }
    }

    /**
     * 通过源字段ID查找关联关系
     *
     * @param sourceFieldId 源字段ID
     * @param sourceEntityId 源实体ID
     * @return 关联关系
     */
    private MetadataEntityRelationshipDO findRelationshipBySourceFieldId(Long sourceFieldId, Long sourceEntityId) {
        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and(MetadataEntityRelationshipDO.SOURCE_FIELD_ID, String.valueOf(sourceFieldId));
            configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, sourceEntityId);
            List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);
            if (CollectionUtils.isEmpty(relationships)) {
                return null;
            }
            // 返回第一个匹配的关联关系
            return relationships.get(0);
        } catch (Exception ex) {
            log.warn("查询关联关系失败，源字段ID：{}，源实体ID：{}，原因：{}", sourceFieldId, sourceEntityId, ex.getMessage());
            return null;
        }
    }

    /**
     * 清除关联记录：将目标实体表中，目标字段等于当前记录ID的所有记录的目标字段设为null
     *
     * @param service        数据服务
     * @param targetTableName 目标表名
     * @param targetFieldName 目标字段名
     * @param recordId       当前记录ID
     */
    private void clearRelationRecords(AnylineService<?> service, String targetTableName, String targetFieldName, Object recordId) {
        try {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(targetFieldName, recordId);
            DataRow updateRow = new DataRow();
            updateRow.put(targetFieldName, null);
            service.update(targetTableName, updateRow, configStore);
        } catch (Exception ex) {
            log.warn("清除关联记录失败，表：{}，字段：{}，记录ID：{}，原因：{}", targetTableName, targetFieldName, recordId, ex.getMessage());
        }
    }

    /**
     * 更新目标记录：在目标实体表中，将被选择的数据记录的目标字段更新为当前记录ID
     *
     * @param service        数据服务
     * @param targetTableName 目标表名
     * @param targetFieldName 目标字段名
     * @param selectedDataId  被选择的数据ID
     * @param recordId        当前记录ID
     */
    private void updateTargetRecord(AnylineService<?> service, String targetTableName, String targetFieldName, String selectedDataId, Object recordId) {
        try {
            // 获取目标实体的主键字段名（假设主键字段名为 "id"）
            ConfigStore configStore = new DefaultConfigStore();
            configStore.and("id", selectedDataId);
            DataRow updateRow = new DataRow();
            updateRow.put(targetFieldName, recordId);
            service.update(targetTableName, updateRow, configStore);
        } catch (Exception ex) {
            log.warn("更新目标记录失败，表：{}，字段：{}，选择数据ID：{}，记录ID：{}，原因：{}",
                    targetTableName, targetFieldName, selectedDataId, recordId, ex.getMessage());
        }
    }

    /**
     * 查询关联记录：从目标实体表中，查询目标字段等于当前记录ID的记录，返回这些记录的ID列表
     *
     * @param service        数据服务
     * @param targetTableName 目标表名
     * @param targetFieldName 目标字段名
     * @param recordId        当前记录ID
     * @return 被选择的数据ID列表
     */
    private List<String> queryRelationRecords(AnylineService<?> service, String targetTableName, String targetFieldName, Object recordId) {
        try {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.and(targetFieldName, recordId);
            DataSet dataSet = service.querys(targetTableName, configStore);
            if (dataSet == null || dataSet.isEmpty()) {
                return new ArrayList<>();
            }
            List<DataRow> rows = dataSet.getRows();
            if (CollectionUtils.isEmpty(rows)) {
                return new ArrayList<>();
            }
            return rows.stream()
                    .map(row -> {
                        Object id = row.get("id");
                        return id != null ? id.toString() : null;
                    })
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.warn("查询关联记录失败，表：{}，字段：{}，记录ID：{}，原因：{}", targetTableName, targetFieldName, recordId, ex.getMessage());
            return new ArrayList<>();
        }
    }
}

