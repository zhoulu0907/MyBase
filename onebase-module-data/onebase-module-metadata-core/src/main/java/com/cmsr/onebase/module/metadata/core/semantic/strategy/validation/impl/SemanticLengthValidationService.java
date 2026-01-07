package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationContext;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationService;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@Component
public class SemanticLengthValidationService implements SemanticValidationService {
    public SemanticLengthValidationService() { }

    @Override
    public void validateEntity(List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, SemanticDataMethodOpEnum operationType, SemanticValidationContext context) {
        for (SemanticFieldSchemaDTO field : fields) {
            if (field.getIsSystemField() != null && field.getIsSystemField()) { continue; }
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey()) { continue; }
            Object value = data.get(field.getFieldName());
            if (operationType == SemanticDataMethodOpEnum.UPDATE && value == null) { continue; }
            if (field.getFieldTypeEnum() == SemanticFieldTypeEnum.AUTO_CODE) { continue; }
            if (!supports(field.getFieldType())) { continue; }
            if (value == null) { continue; }
            List<MetadataValidationLengthDO> rules = context.getLengthRules().getOrDefault(field.getFieldUuid(), Collections.emptyList());
            if (rules.isEmpty()) { continue; }
            boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
            if (!hasEnabledRule) { continue; }
            String originalValue = value.toString();
            for (MetadataValidationLengthDO rule : rules) {
                if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) { continue; }
                String stringValue = originalValue;
                if (rule.getTrimBefore() != null && rule.getTrimBefore() == 1) { stringValue = stringValue.trim(); }
                if (rule.getMinLength() != null && stringValue.length() < rule.getMinLength()) {
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage() : "字段[" + field.getDisplayName() + "]长度不能小于" + rule.getMinLength() + "个字符";
                    String prefix = context.getTableName() != null ? "表[" + context.getTableName() + "] " : "";
                    throw new IllegalArgumentException(prefix + errorMessage);
                }
                if (rule.getMaxLength() != null && stringValue.length() > rule.getMaxLength()) {
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage() : "字段[" + field.getDisplayName() + "]长度不能大于" + rule.getMaxLength() + "个字符";
                    String prefix = context.getTableName() != null ? "表[" + context.getTableName() + "] " : "";
                    throw new IllegalArgumentException(prefix + errorMessage);
                }
            }
        }
    }

    @Override
    public String getValidationType() { return "LENGTH"; }

    @Override
    public boolean supports(String fieldType) {
        return "VARCHAR".equalsIgnoreCase(fieldType) || 
               "TEXT".equalsIgnoreCase(fieldType) ||
               "CHAR".equalsIgnoreCase(fieldType);
    }
}
