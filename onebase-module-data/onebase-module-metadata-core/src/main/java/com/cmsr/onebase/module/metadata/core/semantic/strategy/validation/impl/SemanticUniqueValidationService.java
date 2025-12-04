package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationContext;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SemanticUniqueValidationService implements SemanticValidationService {
    public SemanticUniqueValidationService() { }


    @Override
    public void validateEntity(java.util.List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, MetadataDataMethodOpEnum operationType, SemanticValidationContext context) {
        for (SemanticFieldSchemaDTO field : fields) {
            if (field.getIsSystemField() != null && field.getIsSystemField()) { continue; }
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey()) { continue; }
            Object value = data.get(field.getFieldName());
            if (operationType == MetadataDataMethodOpEnum.UPDATE && value == null) { continue; }
            if (field.getFieldTypeEnum() == SemanticFieldTypeEnum.AUTO_CODE) { continue; }
            List<MetadataValidationUniqueDO> rules = context.getUniqueRules().getOrDefault(field.getId(), java.util.Collections.emptyList());
            if (rules.isEmpty()) { continue; }
            boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
            if (!hasEnabledRule) { continue; }
            Boolean exist = context.getUniqueExists().get(field.getId());
            boolean isExist = exist != null && exist;
            if (isExist) {
                String prefix = context.getTableName() != null ? "表[" + context.getTableName() + "] " : "";
                throw new IllegalArgumentException(prefix + "字段[" + field.getDisplayName() + "]值已存在");
            }
        }
    }

    @Override
    public String getValidationType() { return "UNIQUE"; }

    @Override
    public boolean supports(String fieldType) { return true; }

    public Map<String, Boolean> checkUniqueExistsBatch(SemanticEntitySchemaDTO entity, List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, Map<String, List<MetadataValidationUniqueDO>> uniqueRules) {
        Map<String, Boolean> result = new java.util.HashMap<>();
        if (fields == null || fields.isEmpty() || data == null) { return result; }

        java.util.List<SemanticFieldSchemaDTO> candidates = new java.util.ArrayList<>();
        java.util.Map<String, Object> targetValuesByName = new java.util.HashMap<>();
        for (SemanticFieldSchemaDTO f : fields) {
            List<MetadataValidationUniqueDO> urs = uniqueRules.get(f.getFieldUuid());
            if (urs == null || urs.isEmpty()) { continue; }
            boolean enabled = urs.stream().anyMatch(u -> u.getIsEnabled() != null && u.getIsEnabled() == 1);
            if (!enabled) { continue; }
            Object value = data.get(f.getFieldName());
            if (value == null) { continue; }
            candidates.add(f);
            targetValuesByName.put(f.getFieldName(), value);
            result.put(f.getFieldUuid(), false);
        }

        if (candidates.isEmpty()) { return result; }

        boolean hasDeletedColumn = fields.stream().anyMatch(item -> "deleted".equalsIgnoreCase(item.getFieldName()));
        Optional<String> primaryKeyField = fields.stream()
                .filter(item -> item.getIsPrimaryKey() != null && item.getIsPrimaryKey())
                .map(SemanticFieldSchemaDTO::getFieldName)
                .findFirst();
        String pkName = primaryKeyField.orElse("id");
        Object currentId = data.get(pkName);

        StringBuilder orCond = new StringBuilder();
        java.util.List<Object> params = new java.util.ArrayList<>();
        for (int i = 0; i < candidates.size(); i++) {
            SemanticFieldSchemaDTO f = candidates.get(i);
            if (i == 0) { orCond.append("("); } else { orCond.append(" OR "); }
            orCond.append(f.getFieldName()).append(" = ?");
            params.add(targetValuesByName.get(f.getFieldName()));
        }
        orCond.append(")");

        QueryWrapper qw = QueryWrapper.create()
                .from(entity.getTableName())
                .where(orCond.toString(), params.toArray());
        if (hasDeletedColumn) { qw.and("deleted = ?", 0); }
        if (currentId != null) { qw.and(pkName + " <> ?", currentId); }

        qw.select(pkName);
        for (SemanticFieldSchemaDTO f : candidates) { qw.select(f.getFieldName()); }

        java.util.List<Row> rows = Db.selectListByQuery(entity.getTableName(), qw);
        if (rows == null || rows.isEmpty()) { return result; }

        for (Row row : rows) {
            for (SemanticFieldSchemaDTO f : candidates) {
                Object dbVal = row.get(f.getFieldName());
                Object targetVal = targetValuesByName.get(f.getFieldName());
                if (dbVal != null && targetVal != null && String.valueOf(dbVal).equals(String.valueOf(targetVal))) {
                    result.put(f.getFieldUuid(), true);
                }
            }
        }
        return result;
    }
}
