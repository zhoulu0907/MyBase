package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategy;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategyFactory;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Deprecated
public class SemanticResultFormatter {
    @Resource
    private FieldValueStorageStrategyFactory fieldValueStorageStrategyFactory;
    @Resource
    private SemanticDataFetcher dataFetcher;

    public Map<String, Object> format(ProcessContext context) {
        Map<String, Object> processedData = context.getProcessedData();
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();
        return new HashMap<>();
        // return TenantUtils.executeIgnore(() -> {
        //     Object primaryKeyValue = getPrimaryKeyValue(processedData, fields);
        //     if (primaryKeyValue == null) {
        //         applyFieldStorageStrategies(processedData, fields, FieldValueTransformMode.READ, context);
        //         return buildDataResponse(entity, processedData, fields);
        //     }
        //     Map<String, Object> resultData = dataFetcher.queryDataByIdWithService(temporaryService, entity.getTableName(), primaryKeyValue, fields);
        //     applyFieldStorageStrategies(resultData, fields, FieldValueTransformMode.READ, context);
        //     return buildDataResponse(entity, resultData, fields);
        // });
    }

    public Map<String, Object> format(MetadataBusinessEntityDO entity,
                                      List<MetadataEntityFieldDO> fields,
                                      Map<String, Object> data,
                                      ProcessContext context) {
        Map<String, Object> resultData = (data == null) ? new HashMap<>() : new HashMap<>(data);
        applyFieldStorageStrategies(resultData, fields, FieldValueTransformMode.READ, context);
        return buildDataResponse(entity, resultData, fields);
    }

    private Object getPrimaryKeyValue(Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);
        return data.get(primaryKeyField);
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

    private Map<String, Object> buildDataResponse(MetadataBusinessEntityDO entity, Map<String, Object> data, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> response = new HashMap<>();
        response.put("entityId", String.valueOf(entity.getId()));
        response.put("entityName", entity.getDisplayName());
        response.put("data", data);
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            fieldTypeMap.put(field.getFieldName(), field.getFieldType());
        }
        response.put("fieldType", fieldTypeMap);
        return response;
    }
}
