package com.cmsr.onebase.module.metadata.core.service.datamethod.validator;

import java.util.Map;

public interface PrefetchableValidationService extends ValidationService {

    void preloadBatchRules(Map<String, Map<String, ? extends java.util.List<?>>> rulesByType);

    void clearPrefetchedRules();
}
