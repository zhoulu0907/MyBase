package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;

import java.util.Map;

public interface SemanticValidationService {
    void validate(MetadataEntityFieldDO field, Object value, Map<String, Object> data);
    String getValidationType();
    boolean supports(String fieldType);
}

