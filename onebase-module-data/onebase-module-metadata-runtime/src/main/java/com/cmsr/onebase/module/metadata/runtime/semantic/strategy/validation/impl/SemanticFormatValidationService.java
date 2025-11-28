package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SemanticFormatValidationService implements SemanticValidationService {
    private final MetadataValidationFormatRepository formatRepository;
    public SemanticFormatValidationService(MetadataValidationFormatRepository formatRepository) { this.formatRepository = formatRepository; }

    @Override
    public void validate(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        if (value == null) { return; }
        List<MetadataValidationFormatDO> rules = formatRepository.findByFieldId(field.getId());
        if (rules.isEmpty()) { return; }
        boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
        if (!hasEnabledRule) { return; }
        String stringValue = value.toString();
        for (MetadataValidationFormatDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) { continue; }
            String regexPattern = rule.getRegexPattern();
            if (regexPattern == null || regexPattern.trim().isEmpty()) { continue; }
            boolean isValid = isValidRegex(stringValue, regexPattern, rule.getFlags());
            if (!isValid) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage() : "字段[" + field.getDisplayName() + "]格式不正确";
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    private boolean isValidRegex(String value, String pattern, String flags) {
        int regexFlags = 0;
        if (flags != null) {
            if (flags.contains("i")) regexFlags |= Pattern.CASE_INSENSITIVE;
            if (flags.contains("m")) regexFlags |= Pattern.MULTILINE;
            if (flags.contains("s")) regexFlags |= Pattern.DOTALL;
        }
        try {
            Pattern compiledPattern = Pattern.compile(pattern, regexFlags);
            return compiledPattern.matcher(value).matches();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getValidationType() { return "FORMAT"; }

    @Override
    public boolean supports(String fieldType) { return true; }
}
