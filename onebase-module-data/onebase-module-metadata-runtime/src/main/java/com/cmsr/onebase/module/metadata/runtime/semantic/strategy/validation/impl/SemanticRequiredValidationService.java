package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SemanticRequiredValidationService implements SemanticValidationService {
    private final MetadataValidationRequiredRepository requiredRepository;
    public SemanticRequiredValidationService(MetadataValidationRequiredRepository requiredRepository) { this.requiredRepository = requiredRepository; }

    @Override
    public void validate(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        List<MetadataValidationRequiredDO> rules = requiredRepository.findByFieldId(field.getId());
        if (rules.isEmpty()) { return; }
        boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
        if (!hasEnabledRule) { return; }
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            String errorMessage = rules.stream()
                    .filter(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1)
                    .map(MetadataValidationRequiredDO::getPromptMessage)
                    .filter(msg -> msg != null && !msg.trim().isEmpty())
                    .findFirst()
                    .orElse("字段[" + field.getDisplayName() + "]为必填字段");
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public String getValidationType() { return "REQUIRED"; }

    @Override
    public boolean supports(String fieldType) { return true; }
}
