package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;

import java.util.List;
import java.util.Map;

public class SemanticValidationContext {
    private final Map<String, List<MetadataValidationLengthDO>> lengthRules;
    private final Map<String, List<MetadataValidationFormatDO>> formatRules;
    private final Map<String, List<MetadataValidationRangeDO>> rangeRules;
    private final Map<String, List<MetadataValidationRequiredDO>> requiredRules;
    private final Map<String, List<MetadataValidationUniqueDO>> uniqueRules;
    private final Map<String, Boolean> uniqueExists;
    private final List<MetadataValidationRuleGroupDO> selfDefinedRuleGroups;
    private final Map<String, List<MetadataValidationRuleDefinitionDO>> ruleDefinitionsByGroup;
    private final Map<String, String> fieldUuidToNameMap;
    private final String tableName;

    public SemanticValidationContext(
            Map<String, List<MetadataValidationLengthDO>> lengthRules,
            Map<String, List<MetadataValidationFormatDO>> formatRules,
            Map<String, List<MetadataValidationRangeDO>> rangeRules,
            Map<String, List<MetadataValidationRequiredDO>> requiredRules,
            Map<String, List<MetadataValidationUniqueDO>> uniqueRules,
            Map<String, Boolean> uniqueExists,
            List<MetadataValidationRuleGroupDO> selfDefinedRuleGroups,
            Map<String, List<MetadataValidationRuleDefinitionDO>> ruleDefinitionsByGroup,
            Map<String, String> fieldUuidToNameMap,
            String tableName
    ) {
        this.lengthRules = lengthRules;
        this.formatRules = formatRules;
        this.rangeRules = rangeRules;
        this.requiredRules = requiredRules;
        this.uniqueRules = uniqueRules;
        this.uniqueExists = uniqueExists;
        this.selfDefinedRuleGroups = selfDefinedRuleGroups;
        this.ruleDefinitionsByGroup = ruleDefinitionsByGroup;
        this.fieldUuidToNameMap = fieldUuidToNameMap;
        this.tableName = tableName;
    }

    public Map<String, List<MetadataValidationLengthDO>> getLengthRules() { return lengthRules; }
    public Map<String, List<MetadataValidationFormatDO>> getFormatRules() { return formatRules; }
    public Map<String, List<MetadataValidationRangeDO>> getRangeRules() { return rangeRules; }
    public Map<String, List<MetadataValidationRequiredDO>> getRequiredRules() { return requiredRules; }
    public Map<String, List<MetadataValidationUniqueDO>> getUniqueRules() { return uniqueRules; }
    public Map<String, Boolean> getUniqueExists() { return uniqueExists; }
    public List<MetadataValidationRuleGroupDO> getSelfDefinedRuleGroups() { return selfDefinedRuleGroups; }
    public Map<String, List<MetadataValidationRuleDefinitionDO>> getRuleDefinitionsByGroup() { return ruleDefinitionsByGroup; }
    public Map<String, String> getFieldUuidToNameMap() { return fieldUuidToNameMap; }
    public String getTableName() { return tableName; }

    public SemanticValidationContext copyWithTableName(String tableName) {
        return new SemanticValidationContext(lengthRules, formatRules, rangeRules, requiredRules, uniqueRules, uniqueExists, selfDefinedRuleGroups, ruleDefinitionsByGroup, fieldUuidToNameMap, tableName);
    }
}
