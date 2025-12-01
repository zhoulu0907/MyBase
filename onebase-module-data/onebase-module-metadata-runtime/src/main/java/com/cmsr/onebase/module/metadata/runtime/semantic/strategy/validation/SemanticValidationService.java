package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;

import java.util.Map;

public interface SemanticValidationService {
    void validateEntity(java.util.List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, MetadataDataMethodOpEnum operationType, SemanticValidationContext context);
    String getValidationType();
    boolean supports(String fieldType);
}
