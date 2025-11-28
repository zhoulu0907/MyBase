package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataPermissionContext;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.permission.filter.FieldPermissionFilter;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategy;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategyFactory;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SemanticDataFetcher {
    @Resource
    private FieldPermissionFilter fieldPermissionFilter;
    @Resource
    private FieldValueStorageStrategyFactory fieldValueStorageStrategyFactory;
    @Resource
    private SemanticTableNameQuoter tableNameQuoter;

    public Map<String, Object> fetch(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();
        Object id = context.getId();
        if (entity == null || fields == null || fields.isEmpty() || temporaryService == null || id == null) { return null; }
        return TenantUtils.executeIgnore(() -> {
            Map<String, Object> resultData = queryDataByIdWithService(temporaryService, tableNameQuoter.quote(entity.getTableName()), id, fields);
            if (resultData == null) { return null; }
            applyFieldStorageStrategies(resultData, fields, FieldValueTransformMode.READ, context);
            Map<String, Object> filtered = filterQueryResultFields(resultData, context);
            context.setProcessedData(filtered);
            return filtered;
        });
    }

    private Map<String, Object> filterQueryResultFields(Map<String, Object> data, ProcessContext context) {
        MetadataPermissionContext permissionContext = context.getMetadataPermissionContext();
        if (permissionContext == null || permissionContext.getFieldPermission() == null) { return data; }
        return fieldPermissionFilter.filterFields(data, permissionContext.getFieldPermission(), context.getFields());
    }

    public Map<String, Object> queryDataByIdWithService(AnylineService<?> service, String tableName, Object id, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(primaryKeyField, id);
        boolean hasDeletedField = fields.stream().anyMatch(field -> "deleted".equalsIgnoreCase(field.getFieldName()));
        if (hasDeletedField) { configStore.and("deleted", 0); }
        DataSet dataSet = service.querys(tableName, configStore);
        if (dataSet == null || dataSet.size() == 0) { return null; }
        DataRow dataRow = dataSet.getRow(0);
        Map<String, Object> map = convertDataRowToMap(dataRow, fields);
        return map;
    }

    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        List<MetadataEntityFieldDO> pkCandidates = fields.stream()
                .filter(field -> com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
                .filter(field -> !com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsSystemField()))
                .toList();
        Optional<String> idNamed = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        Optional<String> firstPk = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(MetadataEntityFieldDO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }

    private void applyFieldStorageStrategies(Map<String, Object> data, List<MetadataEntityFieldDO> fields,
                                             FieldValueTransformMode mode, ProcessContext context) {
        if (data == null || fields == null || fields.isEmpty()) { return; }
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            if (!data.containsKey(fieldName)) { continue; }
            Object val = data.get(fieldName);
            FieldValueStorageStrategy strategy = fieldValueStorageStrategyFactory.getStrategy(field.getFieldType());
            Object transformed = strategy.transform(val, mode, context, field);
            data.put(fieldName, transformed);
        }
    }

    private Map<String, Object> convertDataRowToMap(DataRow dataRow, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> resultMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            String fieldType = field.getFieldType();
            Object value = dataRow.get(fieldName);
            if (value != null) {
                if (needsJsonDeserialization(fieldType, value)) {
                    try {
                        Object deserializedValue = com.cmsr.onebase.framework.common.util.json.JsonUtils.parseObject(value.toString(), Object.class);
                        resultMap.put(fieldName, deserializedValue);
                    } catch (Exception e) {
                        resultMap.put(fieldName, value);
                    }
                } else if ("DATETIME".equals(fieldType) && value instanceof Timestamp timestamp) {
                    Instant instant = timestamp.toInstant();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
                    String timeStr = dateTimeFormatter.format(instant);
                    resultMap.put(fieldName, timeStr);
                } else {
                    resultMap.put(fieldName, value);
                }
            }
        }
        return resultMap;
    }

    private boolean needsJsonDeserialization(String fieldType, Object fieldValue) {
        if (fieldType == null || fieldValue == null) { return false; }
        if (!(fieldValue instanceof String)) { return false; }
        String upperFieldType = fieldType.toUpperCase();
        boolean isComplexType = upperFieldType.contains("SELECT") || upperFieldType.contains("MULTI") ||
                upperFieldType.contains("ADDRESS") || upperFieldType.contains("FILE") || upperFieldType.contains("ATTACHMENT") ||
                upperFieldType.contains("IMAGE") || upperFieldType.contains("USER") || upperFieldType.contains("DEPARTMENT") ||
                upperFieldType.contains("DATA") || upperFieldType.contains("GEOGRAPHY") || upperFieldType.contains("GEO") ||
                upperFieldType.equals("JSONB") || upperFieldType.equals("JSON");
        String strValue = fieldValue.toString().trim();
        boolean looksLikeJson = strValue.startsWith("{") || strValue.startsWith("[");
        return isComplexType && looksLikeJson;
    }
}

