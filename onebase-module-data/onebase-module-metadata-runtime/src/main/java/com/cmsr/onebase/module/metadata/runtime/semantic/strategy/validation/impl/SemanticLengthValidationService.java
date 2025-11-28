package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SemanticLengthValidationService implements SemanticValidationService {
    private final MetadataValidationLengthRepository lengthRepository;
    public SemanticLengthValidationService(MetadataValidationLengthRepository lengthRepository) { this.lengthRepository = lengthRepository; }

    @Override
    public void validate(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        if (value == null) { return; }
        List<MetadataValidationLengthDO> rules = lengthRepository.findByFieldId(field.getId());
        if (rules.isEmpty()) { return; }
        boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
        if (!hasEnabledRule) { return; }
        String originalValue = value.toString();
        for (MetadataValidationLengthDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) { continue; }
            String stringValue = originalValue;
            if (rule.getTrimBefore() != null && rule.getTrimBefore() == 1) { stringValue = stringValue.trim(); }
            if (rule.getMinLength() != null && stringValue.length() < rule.getMinLength()) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage() : "字段[" + field.getDisplayName() + "]长度不能小于" + rule.getMinLength() + "个字符";
                throw new IllegalArgumentException(errorMessage);
            }
            if (rule.getMaxLength() != null && stringValue.length() > rule.getMaxLength()) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage() : "字段[" + field.getDisplayName() + "]长度不能大于" + rule.getMaxLength() + "个字符";
                throw new IllegalArgumentException(errorMessage);
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
