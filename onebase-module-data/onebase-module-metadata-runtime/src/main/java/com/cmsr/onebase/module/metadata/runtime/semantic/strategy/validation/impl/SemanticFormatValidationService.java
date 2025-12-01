package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Collections;

@Component
public class SemanticFormatValidationService implements SemanticValidationService {
    public SemanticFormatValidationService() { }

    @Override
    public void validateEntity(List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, MetadataDataMethodOpEnum operationType, SemanticValidationContext context) {
        for (SemanticFieldSchemaDTO field : fields) {
            if (field.getIsSystemField() != null && field.getIsSystemField()) { continue; }
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey()) { continue; }
            Object value = data.get(field.getFieldName());
            if (operationType == MetadataDataMethodOpEnum.UPDATE && value == null) { continue; }
            if (field.getFieldTypeEnum() == SemanticFieldTypeEnum.AUTO_CODE) { continue; }
            if (!supports(field.getFieldType())) { continue; }
            if (value == null) { continue; }
            List<MetadataValidationFormatDO> rules = context.getFormatRules().getOrDefault(field.getId(), Collections.emptyList());
            if (rules.isEmpty()) { continue; }
            boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
            if (!hasEnabledRule) { continue; }
            String stringValue = value.toString();
            for (MetadataValidationFormatDO rule : rules) {
                if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) { continue; }
                String regexPattern = rule.getRegexPattern();
                if (regexPattern == null || regexPattern.trim().isEmpty()) { continue; }
                boolean isValid = isValidRegex(stringValue, regexPattern, rule.getFlags());
                if (!isValid) {
                    String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                            ? rule.getPromptMessage() : "字段[" + field.getDisplayName() + "]格式不正确";
                    String prefix = context.getTableName() != null ? "表[" + context.getTableName() + "] " : "";
                    throw new IllegalArgumentException(prefix + errorMessage);
                }
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
