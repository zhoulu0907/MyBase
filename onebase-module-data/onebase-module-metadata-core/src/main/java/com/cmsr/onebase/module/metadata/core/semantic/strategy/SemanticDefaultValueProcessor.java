package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.framework.uid.UidGenerator;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategy;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategyFactory;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Deprecated
public class SemanticDefaultValueProcessor {
    @Resource
    private UidGenerator uidGenerator;
    @Resource
    private FieldValueStorageStrategyFactory fieldValueStorageStrategyFactory;

    public Map<String, Object> process(ProcessContext context) {
        Map<String, Object> data = context.getData();
        List<MetadataEntityFieldDO> fields = context.getFields();
        var op = context.getOperationType();
        Map<String, Object> processed = data == null ? new HashMap<>() : new HashMap<>(data);
        if (op == com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.CREATE) {
            String pk = getPrimaryKeyFieldName(fields);
            if (!processed.containsKey(pk) || processed.get(pk) == null) {
                processed.put(pk, uidGenerator.getUID());
            }
            String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            for (MetadataEntityFieldDO f : fields) {
                String name = f.getFieldName();
                if (name == null) continue;
                if (BooleanStatusEnum.isYes(f.getIsSystemField())) {
                    switch (name.toLowerCase()) {
                        case "created_time":
                        case "createtime": processed.put(name, now); break;
                        case "updated_time":
                        case "updatetime": processed.put(name, now); break;
                        case "deleted": processed.put(name, 0); break;
                        case "lock_version":
                        case "lockversion": processed.put(name, 0); break;
                        default:
                            if (org.springframework.util.StringUtils.hasText(f.getDefaultValue())) {
                                processed.putIfAbsent(name, f.getDefaultValue());
                            }
                    }
                } else {
                    if (!processed.containsKey(name) && org.springframework.util.StringUtils.hasText(f.getDefaultValue())) {
                        processed.put(name, f.getDefaultValue());
                    }
                }
            }
        }
        if (op == com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum.UPDATE) {
            String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            for (MetadataEntityFieldDO f : fields) {
                String name = f.getFieldName();
                if (name == null) continue;
                if (BooleanStatusEnum.isYes(f.getIsSystemField())) {
                    if ("updated_time".equalsIgnoreCase(name) || "updatetime".equalsIgnoreCase(name)) {
                        processed.put(name, now);
                    }
                }
            }
        }
        processComplexTypeFields(fields, processed);
        return processed;
    }

    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        List<MetadataEntityFieldDO> pkCandidates = fields.stream()
                .filter(field -> BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
                .filter(field -> !BooleanStatusEnum.isYes(field.getIsSystemField()))
                .toList();
        var idNamed = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(java.util.Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) return idNamed.get();
        var firstPk = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(java.util.Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) return firstPk.get();
        boolean hasId = fields.stream().map(MetadataEntityFieldDO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) return "id";
        return "id";
    }

    private void processComplexTypeFields(List<MetadataEntityFieldDO> fields, Map<String, Object> data) {
        if (fields == null || data == null) return;
        for (MetadataEntityFieldDO field : fields) {
            String name = field.getFieldName();
            if (!data.containsKey(name)) continue;
            Object val = data.get(name);
            FieldValueStorageStrategy strategy = fieldValueStorageStrategyFactory.getStrategy(field.getFieldType());
            Object transformed = strategy.transform(val, FieldValueTransformMode.STORE, null, field);
            data.put(name, transformed);
        }
    }
}
