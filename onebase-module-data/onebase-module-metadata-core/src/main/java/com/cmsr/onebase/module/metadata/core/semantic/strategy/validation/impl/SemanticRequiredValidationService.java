package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationContext;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationService;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@Component
public class SemanticRequiredValidationService implements SemanticValidationService {
    public SemanticRequiredValidationService() { }

    @Override
    public void validateEntity(List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, SemanticDataMethodOpEnum operationType, SemanticValidationContext context) {
        for (SemanticFieldSchemaDTO field : fields) {
            if (field.getIsSystemField() != null && field.getIsSystemField()) { continue; }
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey()) { continue; }
            Object value = data.get(field.getFieldName());
            if (operationType == SemanticDataMethodOpEnum.UPDATE && value == null) { continue; }
            if (field.getFieldTypeEnum() == SemanticFieldTypeEnum.AUTO_CODE) { continue; }
            if (!supports(field.getFieldType())) { continue; }
            List<MetadataValidationRequiredDO> rules = context.getRequiredRules().getOrDefault(field.getFieldUuid(), Collections.emptyList());
            if (rules.isEmpty()) { continue; }
            boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
            if (!hasEnabledRule) { continue; }
            if (value == null || String.valueOf(value).trim().isEmpty()) {
                String errorMessage = rules.stream()
                        .filter(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1)
                        .map(MetadataValidationRequiredDO::getPromptMessage)
                        .filter(msg -> msg != null && !msg.trim().isEmpty())
                        .findFirst()
                        .orElse("字段[" + field.getDisplayName() + "]为必填字段");
                String prefix = context.getTableName() != null ? "表[" + context.getTableName() + "] " : "";
                throw new IllegalArgumentException(prefix + errorMessage);
            }
        }
    }

    @Override
    public String getValidationType() { return "REQUIRED"; }

    @Override
    public boolean supports(String fieldType) { return true; }
}
