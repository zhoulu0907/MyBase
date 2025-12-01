package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation;

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
    private final Map<Long, List<MetadataValidationLengthDO>> lengthRules;
    private final Map<Long, List<MetadataValidationFormatDO>> formatRules;
    private final Map<Long, List<MetadataValidationRangeDO>> rangeRules;
    private final Map<Long, List<MetadataValidationRequiredDO>> requiredRules;
    private final Map<Long, List<MetadataValidationUniqueDO>> uniqueRules;
    private final Map<Long, Boolean> uniqueExists;
    private final List<MetadataValidationRuleGroupDO> selfDefinedRuleGroups;
    private final Map<Long, List<MetadataValidationRuleDefinitionDO>> ruleDefinitionsByGroup;
    private final Map<Long, String> fieldIdToNameMap;
    private final String tableName;

    public SemanticValidationContext(
            Map<Long, List<MetadataValidationLengthDO>> lengthRules,
            Map<Long, List<MetadataValidationFormatDO>> formatRules,
            Map<Long, List<MetadataValidationRangeDO>> rangeRules,
            Map<Long, List<MetadataValidationRequiredDO>> requiredRules,
            Map<Long, List<MetadataValidationUniqueDO>> uniqueRules,
            Map<Long, Boolean> uniqueExists,
            List<MetadataValidationRuleGroupDO> selfDefinedRuleGroups,
            Map<Long, List<MetadataValidationRuleDefinitionDO>> ruleDefinitionsByGroup,
            Map<Long, String> fieldIdToNameMap,
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
        this.fieldIdToNameMap = fieldIdToNameMap;
        this.tableName = tableName;
    }

    public Map<Long, List<MetadataValidationLengthDO>> getLengthRules() { return lengthRules; }
    public Map<Long, List<MetadataValidationFormatDO>> getFormatRules() { return formatRules; }
    public Map<Long, List<MetadataValidationRangeDO>> getRangeRules() { return rangeRules; }
    public Map<Long, List<MetadataValidationRequiredDO>> getRequiredRules() { return requiredRules; }
    public Map<Long, List<MetadataValidationUniqueDO>> getUniqueRules() { return uniqueRules; }
    public Map<Long, Boolean> getUniqueExists() { return uniqueExists; }
    public List<MetadataValidationRuleGroupDO> getSelfDefinedRuleGroups() { return selfDefinedRuleGroups; }
    public Map<Long, List<MetadataValidationRuleDefinitionDO>> getRuleDefinitionsByGroup() { return ruleDefinitionsByGroup; }
    public Map<Long, String> getFieldIdToNameMap() { return fieldIdToNameMap; }
    public String getTableName() { return tableName; }

    public SemanticValidationContext copyWithTableName(String tableName) {
        return new SemanticValidationContext(lengthRules, formatRules, rangeRules, requiredRules, uniqueRules, uniqueExists, selfDefinedRuleGroups, ruleDefinitionsByGroup, fieldIdToNameMap, tableName);
    }
}
