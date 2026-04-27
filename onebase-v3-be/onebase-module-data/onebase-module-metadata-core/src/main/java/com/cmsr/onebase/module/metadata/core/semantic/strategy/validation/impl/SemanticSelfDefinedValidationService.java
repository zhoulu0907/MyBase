package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.enums.OpEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataValidationRuleTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationContext;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationService;

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
    public void validateEntity(List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, SemanticDataMethodOpEnum operationType, SemanticValidationContext context) {
        List<MetadataValidationRuleGroupDO> ruleGroups = context.getSelfDefinedRuleGroups();
        if (ruleGroups.isEmpty()) { return; }
        Map<String, String> fieldUuidToNameMap = context.getFieldUuidToNameMap();
        for (SemanticFieldSchemaDTO field : fields) {
            if (field.getIsSystemField() != null && field.getIsSystemField()) { continue; }
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey()) { continue; }
            Object value = data.get(field.getFieldName());
            if (operationType == SemanticDataMethodOpEnum.UPDATE && value == null) { continue; }
            if (field.getFieldTypeEnum() == SemanticFieldTypeEnum.AUTO_CODE) { continue; }
            if (!supports(field.getFieldType())) { continue; }
            Map<String, Object> contextMap = buildCompleteContext(field, value, data, fieldUuidToNameMap);
            for (MetadataValidationRuleGroupDO ruleGroup : ruleGroups) { validateRuleGroup(ruleGroup, contextMap, fieldUuidToNameMap, field, context); }
        }
    }

    private void validateRuleGroup(MetadataValidationRuleGroupDO ruleGroup, Map<String, Object> context, Map<String, String> fieldUuidToNameMap, SemanticFieldSchemaDTO field, SemanticValidationContext svcContext) {
        List<MetadataValidationRuleDefinitionDO> rules = svcContext.getRuleDefinitionsByGroup().getOrDefault(ruleGroup.getGroupUuid(), Collections.emptyList());
        if (rules.isEmpty()) { return; }
        String expression = buildExpression(rules, fieldUuidToNameMap, context);
        Serializable compiled = MVEL.compileExpression(expression, parserContext);
        Object result = MVEL.executeExpression(compiled, context);
        if (Boolean.TRUE.equals(result)) {
            String message = Optional.ofNullable(ruleGroup.getPopPrompt()).filter(s -> !s.trim().isEmpty()).orElse(field.getDisplayName() + "不满足自定义校验规则：" + ruleGroup.getRgName());
            throw new IllegalArgumentException(message);
        }
    }

    private String buildExpression(List<MetadataValidationRuleDefinitionDO> allRules, Map<String, String> fieldUuidToNameMap, Map<String, Object> context) {
        List<MetadataValidationRuleDefinitionDO> topRules = allRules.stream().filter(rule -> rule.getParentRuleUuid() == null).collect(Collectors.toList());
        if (topRules.isEmpty()) { return "false"; }
        List<String> topExpressions = new ArrayList<>();
        for (MetadataValidationRuleDefinitionDO topRule : topRules) {
            String expr = buildRuleExpression(topRule, allRules, fieldUuidToNameMap, context);
            if (expr != null && !expr.trim().isEmpty()) { topExpressions.add("(" + expr + ")"); }
        }
        return topExpressions.isEmpty() ? "false" : String.join(" || ", topExpressions);
    }

    private String buildRuleExpression(MetadataValidationRuleDefinitionDO rule, List<MetadataValidationRuleDefinitionDO> allRules, Map<String, String> fieldUuidToNameMap, Map<String, Object> context) {
        if ("CONDITION".equals(rule.getLogicType())) { return buildConditionExpression(rule, fieldUuidToNameMap, context); }
        if ("LOGIC".equals(rule.getLogicType())) {
            List<MetadataValidationRuleDefinitionDO> children = findChildRules(rule.getDefinitionUuid(), allRules);
            if (children.isEmpty()) { return handleEmptyLogicNode(rule, fieldUuidToNameMap, context); }
            List<String> childExpressions = new ArrayList<>();
            for (MetadataValidationRuleDefinitionDO child : children) {
                String childExpr = buildRuleExpression(child, allRules, fieldUuidToNameMap, context);
                if (childExpr != null && !childExpr.trim().isEmpty()) { childExpressions.add("(" + childExpr + ")"); }
            }
            if (childExpressions.isEmpty()) { return "false"; }
            String connector = "AND".equals(rule.getLogicOperator()) ? " && " : " || ";
            return String.join(connector, childExpressions);
        }
        return "false";
    }

    private String handleEmptyLogicNode(MetadataValidationRuleDefinitionDO rule, Map<String, String> fieldUuidToNameMap, Map<String, Object> context) {
        if (rule.getOperator() != null && !rule.getOperator().trim().isEmpty()) { return buildConditionExpression(rule, fieldUuidToNameMap, context); }
        return "AND".equals(rule.getLogicOperator()) ? "true" : "false";
    }

    private String buildConditionExpression(MetadataValidationRuleDefinitionDO rule, Map<String, String> fieldUuidToNameMap, Map<String, Object> context) {
        String fieldName = getFieldName(rule, fieldUuidToNameMap);
        if (fieldName == null) { return "false"; }
        OpEnum operator = OpEnum.valueOf(rule.getOperator());
        Object value = rule.getFieldValue();
        Object value2 = rule.getFieldValue2();
        // 当value_type为variables时，field_value存储的是另一个字段的ID，需要解析为字段名或直接获取字段值
        String valueType = rule.getValueType();
        if ("variables".equalsIgnoreCase(valueType)) {
            // field_value存储的是字段ID，需要转换为字段名以便在表达式中引用
            String targetFieldName = resolveFieldIdToName(value, fieldUuidToNameMap);
            String targetFieldName2 = resolveFieldIdToName(value2, fieldUuidToNameMap);
            return buildVariableOperatorExpression(fieldName, operator, targetFieldName, targetFieldName2);
        }
        return buildOperatorExpression(fieldName, operator, value, value2);
    }

    /**
     * 将字段ID解析为字段名
     *
     * @param fieldId 字段ID（可能是Long或String类型）
     * @param fieldUuidToNameMap 字段UUID到字段名的映射
     * @return 字段名，如果找不到则返回null
     */
    private String resolveFieldIdToName(Object fieldId, Map<String, String> fieldUuidToNameMap) {
        if (fieldId == null) { return null; }
        String fieldIdStr = String.valueOf(fieldId);
        // 尝试通过UUID直接查找
        if (fieldUuidToNameMap.containsKey(fieldIdStr)) {
            return fieldUuidToNameMap.get(fieldIdStr);
        }
        // 尝试通过ID匹配（遍历查找）
        for (Map.Entry<String, String> entry : fieldUuidToNameMap.entrySet()) {
            // 字段UUID可能是长整型ID的字符串形式
            if (entry.getKey().equals(fieldIdStr)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 构建变量类型的操作表达式（字段与字段比较）
     */
    private String buildVariableOperatorExpression(String fieldName, OpEnum operator, String targetFieldName, String targetFieldName2) {
        if (targetFieldName == null) { return "false"; }
        switch (operator) {
            case EQUALS: return String.format("(%s != null && %s != null && %s == %s)", fieldName, targetFieldName, fieldName, targetFieldName);
            case NOT_EQUALS: return String.format("(%s != null && %s != null && %s != %s)", fieldName, targetFieldName, fieldName, targetFieldName);
            case GREATER_THAN: return String.format("(%s != null && %s != null && %s > %s)", fieldName, targetFieldName, fieldName, targetFieldName);
            case GREATER_EQUALS: return String.format("(%s != null && %s != null && %s >= %s)", fieldName, targetFieldName, fieldName, targetFieldName);
            case LESS_THAN: return String.format("(%s != null && %s != null && %s < %s)", fieldName, targetFieldName, fieldName, targetFieldName);
            case LESS_EQUALS: return String.format("(%s != null && %s != null && %s <= %s)", fieldName, targetFieldName, fieldName, targetFieldName);
            case RANGE:
                if (targetFieldName2 == null) { return "false"; }
                return String.format("(%s != null && %s != null && %s != null && %s >= %s && %s <= %s)", fieldName, targetFieldName, targetFieldName2, fieldName, targetFieldName, fieldName, targetFieldName2);
            default: return "false";
        }
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

    private String getFieldName(MetadataValidationRuleDefinitionDO rule, Map<String, String> fieldUuidToNameMap) {
        String fieldCode = rule.getFieldCode();
        if (fieldCode != null && !fieldCode.trim().isEmpty()) { return fieldCode; }
        String fieldUuid = rule.getFieldUuid();
        if (fieldUuid != null && fieldUuidToNameMap.containsKey(fieldUuid)) { return fieldUuidToNameMap.get(fieldUuid); }
        return null;
    }

    private List<MetadataValidationRuleDefinitionDO> findChildRules(String parentUuid, List<MetadataValidationRuleDefinitionDO> allRules) {
        return allRules.stream().filter(rule -> parentUuid.equals(rule.getParentRuleUuid())).collect(Collectors.toList());
    }

    

    private Map<String, Object> buildCompleteContext(SemanticFieldSchemaDTO field, Object value, Map<String, Object> data, Map<String, String> fieldUuidToNameMap) {
        Map<String, Object> context = new HashMap<>();
        for (String fieldName : fieldUuidToNameMap.values()) { context.put(fieldName, null); }
        if (data != null) { context.putAll(data); }
        context.put(field.getFieldName(), value);
        return context;
    }

    @Override
    public String getValidationType() { return MetadataValidationRuleTypeEnum.SELF_DEFINED.getCode(); }

        @Override
    public boolean supports(String fieldType) {
        return true;
    }
}
