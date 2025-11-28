package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.enums.OpEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleDefinitionRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationService;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SemanticSelfDefinedValidationService implements SemanticValidationService {
    @Resource
    private MetadataValidationRuleGroupRepository ruleGroupRepository;
    @Resource
    private MetadataValidationRuleDefinitionRepository ruleDefinitionRepository;
    @Resource
    private com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository entityFieldRepository;

    private final ParserContext parserContext;

    public SemanticSelfDefinedValidationService() {
        this.parserContext = new ParserContext();
        parserContext.addImport("LocalDate", LocalDate.class);
        parserContext.addImport("LocalDateTime", LocalDateTime.class);
        parserContext.addImport("LocalTime", LocalTime.class);
        parserContext.addImport("Arrays", Arrays.class);
        parserContext.addImport("Collections", Collections.class);
    }

    @Override
    public void validate(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        List<MetadataValidationRuleGroupDO> ruleGroups = findActiveRuleGroups(field.getEntityId());
        if (ruleGroups.isEmpty()) { return; }
        Map<Long, String> fieldIdToNameMap = buildFieldIdToNameMap(field.getEntityId());
        Map<String, Object> context = buildCompleteContext(field, value, data, fieldIdToNameMap);
        for (MetadataValidationRuleGroupDO ruleGroup : ruleGroups) { validateRuleGroup(ruleGroup, context, fieldIdToNameMap, field); }
    }

    private void validateRuleGroup(MetadataValidationRuleGroupDO ruleGroup, Map<String, Object> context, Map<Long, String> fieldIdToNameMap, MetadataEntityFieldDO field) {
        List<MetadataValidationRuleDefinitionDO> rules = ruleDefinitionRepository.selectByGroupId(ruleGroup.getId());
        if (rules.isEmpty()) { return; }
        String expression = buildExpression(rules, fieldIdToNameMap);
        Serializable compiled = MVEL.compileExpression(expression, parserContext);
        Object result = MVEL.executeExpression(compiled, context);
        if (Boolean.TRUE.equals(result)) {
            String message = Optional.ofNullable(ruleGroup.getPopPrompt()).filter(s -> !s.trim().isEmpty()).orElse("字段[" + field.getDisplayName() + "]不满足自定义校验规则：" + ruleGroup.getRgName());
            throw new IllegalArgumentException(message);
        }
    }

    private String buildExpression(List<MetadataValidationRuleDefinitionDO> allRules, Map<Long, String> fieldIdToNameMap) {
        List<MetadataValidationRuleDefinitionDO> topRules = allRules.stream().filter(rule -> rule.getParentRuleId() == null).collect(Collectors.toList());
        if (topRules.isEmpty()) { return "false"; }
        List<String> topExpressions = new ArrayList<>();
        for (MetadataValidationRuleDefinitionDO topRule : topRules) {
            String expr = buildRuleExpression(topRule, allRules, fieldIdToNameMap);
            if (expr != null && !expr.trim().isEmpty()) { topExpressions.add("(" + expr + ")"); }
        }
        return topExpressions.isEmpty() ? "false" : String.join(" || ", topExpressions);
    }

    private String buildRuleExpression(MetadataValidationRuleDefinitionDO rule, List<MetadataValidationRuleDefinitionDO> allRules, Map<Long, String> fieldIdToNameMap) {
        if ("CONDITION".equals(rule.getLogicType())) { return buildConditionExpression(rule, fieldIdToNameMap); }
        if ("LOGIC".equals(rule.getLogicType())) {
            List<MetadataValidationRuleDefinitionDO> children = findChildRules(rule.getId(), allRules);
            if (children.isEmpty()) { return handleEmptyLogicNode(rule, fieldIdToNameMap); }
            List<String> childExpressions = new ArrayList<>();
            for (MetadataValidationRuleDefinitionDO child : children) {
                String childExpr = buildRuleExpression(child, allRules, fieldIdToNameMap);
                if (childExpr != null && !childExpr.trim().isEmpty()) { childExpressions.add("(" + childExpr + ")"); }
            }
            if (childExpressions.isEmpty()) { return "false"; }
            String connector = "AND".equals(rule.getLogicOperator()) ? " && " : " || ";
            return String.join(connector, childExpressions);
        }
        return "false";
    }

    private String handleEmptyLogicNode(MetadataValidationRuleDefinitionDO rule, Map<Long, String> fieldIdToNameMap) {
        if (rule.getOperator() != null && !rule.getOperator().trim().isEmpty()) { return buildConditionExpression(rule, fieldIdToNameMap); }
        return "AND".equals(rule.getLogicOperator()) ? "true" : "false";
    }

    private String buildConditionExpression(MetadataValidationRuleDefinitionDO rule, Map<Long, String> fieldIdToNameMap) {
        String fieldName = getFieldName(rule, fieldIdToNameMap);
        if (fieldName == null) { return "false"; }
        OpEnum operator = OpEnum.valueOf(rule.getOperator());
        Object value = rule.getFieldValue();
        Object value2 = rule.getFieldValue2();
        return buildOperatorExpression(fieldName, operator, value, value2);
    }

    private String buildOperatorExpression(String fieldName, OpEnum operator, Object value, Object value2) {
        switch (operator) {
            case EQUALS: return String.format("(%s != null && %s == %s)", fieldName, fieldName, formatValue(value));
            case NOT_EQUALS: return String.format("(%s != null && %s != %s)", fieldName, fieldName, formatValue(value));
            case GREATER_THAN: return String.format("(%s != null && %s > %s)", fieldName, fieldName, formatValue(value));
            case GREATER_EQUALS: return String.format("(%s != null && %s >= %s)", fieldName, fieldName, formatValue(value));
            case LESS_THAN: return String.format("(%s != null && %s < %s)", fieldName, fieldName, formatValue(value));
            case LESS_EQUALS: return String.format("(%s != null && %s <= %s)", fieldName, fieldName, formatValue(value));
            case CONTAINS: return String.format("(%s != null && %s.toString().contains(%s))", fieldName, fieldName, formatValue(value));
            case NOT_CONTAINS: return String.format("(%s != null && !%s.toString().contains(%s))", fieldName, fieldName, formatValue(value));
            case EXISTS_IN: return String.format("(%s != null && %s.contains(%s.toString()))", fieldName, formatValue(value), fieldName);
            case NOT_EXISTS_IN: return String.format("(%s != null && !%s.contains(%s.toString()))", fieldName, formatValue(value), fieldName);
            case RANGE: return String.format("(%s != null && %s >= %s && %s <= %s)", fieldName, fieldName, formatValue(value), fieldName, formatValue(value2));
            case IS_EMPTY: return String.format("(%s == null || %s.toString().trim().isEmpty())", fieldName, fieldName);
            case IS_NOT_EMPTY: return String.format("(%s != null && !%s.toString().trim().isEmpty())", fieldName, fieldName);
            case LATER_THAN: return String.format("(%s != null && %s.isAfter(%s))", fieldName, fieldName, formatDateValue(value));
            case EARLIER_THAN: return String.format("(%s != null && %s.isBefore(%s))", fieldName, fieldName, formatDateValue(value));
            default: return "false";
        }
    }

    private String formatValue(Object value) {
        if (value == null) { return "null"; }
        if (value instanceof String) { return "\"" + value.toString().replace("\"", "\\\"") + "\""; }
        if (value instanceof Number || value instanceof Boolean) { return value.toString(); }
        if (value instanceof Collection) { return formatCollectionValue((Collection<?>) value); }
        return "\"" + value.toString() + "\"";
    }

    private String formatDateValue(Object value) {
        if (value instanceof java.time.LocalDate d) { return String.format("LocalDate.of(%d, %d, %d)", d.getYear(), d.getMonthValue(), d.getDayOfMonth()); }
        if (value instanceof java.time.LocalDateTime dt) { return String.format("LocalDateTime.of(%d, %d, %d, %d, %d, %d)", dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()); }
        if (value instanceof java.time.LocalTime t) { return String.format("LocalTime.of(%d, %d, %d)", t.getHour(), t.getMinute(), t.getSecond()); }
        return formatValue(value);
    }

    private String formatCollectionValue(Collection<?> collection) {
        String items = collection.stream().map(this::formatValue).collect(Collectors.joining(", "));
        return "[" + items + "]";
    }

    private String getFieldName(MetadataValidationRuleDefinitionDO rule, Map<Long, String> fieldIdToNameMap) {
        String fieldCode = rule.getFieldCode();
        if (fieldCode != null && !fieldCode.trim().isEmpty()) { return fieldCode; }
        Long fieldId = rule.getFieldId();
        if (fieldId != null && fieldIdToNameMap.containsKey(fieldId)) { return fieldIdToNameMap.get(fieldId); }
        return null;
    }

    private List<MetadataValidationRuleDefinitionDO> findChildRules(Long parentId, List<MetadataValidationRuleDefinitionDO> allRules) {
        return allRules.stream().filter(rule -> parentId.equals(rule.getParentRuleId())).collect(Collectors.toList());
    }

    private List<MetadataValidationRuleGroupDO> findActiveRuleGroups(Long entityId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationRuleGroupDO.ENTITY_ID, entityId);
        cs.and(MetadataValidationRuleGroupDO.VALIDATION_TYPE, "SELF_DEFINED");
        cs.and(MetadataValidationRuleGroupDO.RG_STATUS, 1);
        cs.and("deleted", 0);
        return ruleGroupRepository.findAllByConfig(cs);
    }

    private Map<Long, String> buildFieldIdToNameMap(Long entityId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and("entity_id", entityId);
        cs.and("deleted", 0);
        return entityFieldRepository.findAllByConfig(cs).stream().collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName));
    }

    private Map<String, Object> buildCompleteContext(MetadataEntityFieldDO field, Object value, Map<String, Object> data, Map<Long, String> fieldIdToNameMap) {
        Map<String, Object> context = new HashMap<>();
        for (String fieldName : fieldIdToNameMap.values()) { context.put(fieldName, null); }
        if (data != null) { context.putAll(data); }
        context.put(field.getFieldName(), value);
        return context;
    }

    @Override
    public String getValidationType() { return "SELF_DEFINED"; }

        @Override
    public boolean supports(String fieldType) {
        return true;
    }
}
