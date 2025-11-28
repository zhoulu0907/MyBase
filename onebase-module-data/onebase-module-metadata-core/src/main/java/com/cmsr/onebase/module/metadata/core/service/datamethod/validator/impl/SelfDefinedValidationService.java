package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;


import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.OpEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleDefinitionRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义规则校验服务
 * 
 * 基于 MVEL 表达式引擎实现复杂的自定义校验逻辑
 *
 * @author qd
 * @date 2025-10-10
 */
@Slf4j
@Component
public class SelfDefinedValidationService implements ValidationService {

    @Resource
    private MetadataValidationRuleGroupRepository ruleGroupRepository;
    @Resource
    private MetadataValidationRuleDefinitionRepository ruleDefinitionRepository;
    @Resource
    private com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository entityFieldRepository;

    private final ParserContext parserContext;

    public SelfDefinedValidationService() {
        this.parserContext = new ParserContext();
        // 导入常用的Java类
        parserContext.addImport("LocalDate", LocalDate.class);
        parserContext.addImport("LocalDateTime", LocalDateTime.class);
        parserContext.addImport("LocalTime", LocalTime.class);
        parserContext.addImport("Arrays", Arrays.class);
        parserContext.addImport("Collections", Collections.class);
    }

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        List<MetadataValidationRuleGroupDO> ruleGroups = findActiveRuleGroups(entityId);
        if (ruleGroups.isEmpty()) {
            return;
        }

        // 构建字段映射
        Map<Long, String> fieldIdToNameMap = buildFieldIdToNameMap(entityId);
        
        // 准备完整的上下文（包含所有字段，不存在的字段设为 null）
        Map<String, Object> context = buildCompleteContext(field, value, data, fieldIdToNameMap);

        for (MetadataValidationRuleGroupDO ruleGroup : ruleGroups) {
            validateRuleGroup(ruleGroup, context, fieldIdToNameMap, field);
        }
    }

    private void validateRuleGroup(MetadataValidationRuleGroupDO ruleGroup,
                                    Map<String, Object> context,
                                    Map<Long, String> fieldIdToNameMap,
                                    MetadataEntityFieldDO field) {
        try {
            List<MetadataValidationRuleDefinitionDO> rules = ruleDefinitionRepository.selectByGroupId(ruleGroup.getId());
            if (rules.isEmpty()) {
                log.warn("规则组无规则定义：groupId={}", ruleGroup.getId());
                return;
            }

            // 构建 MVEL 表达式
            String expression = buildExpression(rules, fieldIdToNameMap);
            log.debug("生成的MVEL表达式：{}", expression);

            // 编译并执行表达式
            Serializable compiled = MVEL.compileExpression(expression, parserContext);
            Object result = MVEL.executeExpression(compiled, context);

            // 如果表达式结果为 true，表示触发规则，抛出异常
            if (Boolean.TRUE.equals(result)) {
                String message = Optional.ofNullable(ruleGroup.getPopPrompt())
                        .filter(s -> !s.trim().isEmpty())
                        .orElse("字段[" + field.getDisplayName() + "]不满足自定义校验规则：" + ruleGroup.getRgName());

                log.info("校验失败：field={}, group={}, expression={}", field.getFieldName(), ruleGroup.getRgName(), expression);
                throw new IllegalArgumentException(message);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("规则组执行失败：groupId={}", ruleGroup.getId(), e);
            throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]校验执行失败：" + e.getMessage());
        }
    }

    /**
     * 构建 MVEL 表达式
     * 将规则定义树转换为表达式字符串
     *
     * @param allRules 所有规则定义
     * @param fieldIdToNameMap fieldId到fieldName的映射
     * @return MVEL 表达式字符串
     */
    private String buildExpression(List<MetadataValidationRuleDefinitionDO> allRules, Map<Long, String> fieldIdToNameMap) {
        // 获取顶级规则（顶级规则之间是 OR 关系）
        List<MetadataValidationRuleDefinitionDO> topRules = allRules.stream()
                .filter(rule -> rule.getParentRuleId() == null)
                .collect(Collectors.toList());

        if (topRules.isEmpty()) {
            return "false";
        }

        List<String> topExpressions = new ArrayList<>();
        for (MetadataValidationRuleDefinitionDO topRule : topRules) {
            String expr = buildRuleExpression(topRule, allRules, fieldIdToNameMap);
            if (expr != null && !expr.trim().isEmpty()) {
                topExpressions.add("(" + expr + ")");
            }
        }

        return topExpressions.isEmpty() ? "false" : String.join(" || ", topExpressions);
    }

    /**
     * 构建单个规则的表达式
     *
     * @param rule 规则定义
     * @param allRules 所有规则
     * @param fieldIdToNameMap 字段映射
     * @return 表达式字符串
     */
    private String buildRuleExpression(MetadataValidationRuleDefinitionDO rule,
                                        List<MetadataValidationRuleDefinitionDO> allRules,
                                        Map<Long, String> fieldIdToNameMap) {
        if ("CONDITION".equals(rule.getLogicType())) {
            return buildConditionExpression(rule, fieldIdToNameMap);
        }

        if ("LOGIC".equals(rule.getLogicType())) {
            List<MetadataValidationRuleDefinitionDO> children = findChildRules(rule.getId(), allRules);

            // 无子规则时的处理
            if (children.isEmpty()) {
                return handleEmptyLogicNode(rule, fieldIdToNameMap);
            }

            // 构建子规则表达式
            List<String> childExpressions = new ArrayList<>();
            for (MetadataValidationRuleDefinitionDO child : children) {
                String childExpr = buildRuleExpression(child, allRules, fieldIdToNameMap);
                if (childExpr != null && !childExpr.trim().isEmpty()) {
                    childExpressions.add("(" + childExpr + ")");
                }
            }

            if (childExpressions.isEmpty()) {
                return "false";
            }

            String connector = "AND".equals(rule.getLogicOperator()) ? " && " : " || ";
            return String.join(connector, childExpressions);
        }

        log.warn("未知规则类型：ruleId={}, type={}", rule.getId(), rule.getLogicType());
        return "false";
    }

    private String handleEmptyLogicNode(MetadataValidationRuleDefinitionDO rule, Map<Long, String> fieldIdToNameMap) {
        // 如果有 operator，按条件处理
        if (rule.getOperator() != null && !rule.getOperator().trim().isEmpty()) {
            log.warn("LOGIC节点无子规则但有operator，按CONDITION处理：ruleId={}", rule.getId());
            return buildConditionExpression(rule, fieldIdToNameMap);
        }

        // 空逻辑节点
        log.warn("LOGIC节点既无子规则也无operator：ruleId={}", rule.getId());
        return "AND".equals(rule.getLogicOperator()) ? "true" : "false";
    }

    /**
     * 构建条件表达式
     *
     * @param rule 规则定义
     * @param fieldIdToNameMap 字段映射
     * @return 条件表达式字符串
     */
    private String buildConditionExpression(MetadataValidationRuleDefinitionDO rule, Map<Long, String> fieldIdToNameMap) {
        String fieldName = getFieldName(rule, fieldIdToNameMap);
        if (fieldName == null) {
            return "false";
        }

        try {
            OpEnum operator = OpEnum.valueOf(rule.getOperator());
            Object value = rule.getFieldValue();
            Object value2 = rule.getFieldValue2();

            return buildOperatorExpression(fieldName, operator, value, value2);
        } catch (Exception e) {
            log.error("构建条件表达式失败：ruleId={}", rule.getId(), e);
            return "false";
        }
    }

    /**
     * 根据操作符构建表达式
     *
     * @param fieldName 字段名
     * @param operator 操作符
     * @param value 值1
     * @param value2 值2
     * @return 表达式字符串
     */
    private String buildOperatorExpression(String fieldName, OpEnum operator, Object value, Object value2) {
        switch (operator) {
            case EQUALS:
                return String.format("(%s != null && %s == %s)", fieldName, fieldName, formatValue(value));
            case NOT_EQUALS:
                return String.format("(%s != null && %s != %s)", fieldName, fieldName, formatValue(value));
            case GREATER_THAN:
                return String.format("(%s != null && %s > %s)", fieldName, fieldName, formatValue(value));
            case GREATER_EQUALS:
                return String.format("(%s != null && %s >= %s)", fieldName, fieldName, formatValue(value));
            case LESS_THAN:
                return String.format("(%s != null && %s < %s)", fieldName, fieldName, formatValue(value));
            case LESS_EQUALS:
                return String.format("(%s != null && %s <= %s)", fieldName, fieldName, formatValue(value));
            case CONTAINS:
                return String.format("(%s != null && %s.toString().contains(%s))", 
                        fieldName, fieldName, formatValue(value));
            case NOT_CONTAINS:
                return String.format("(%s != null && !%s.toString().contains(%s))", 
                        fieldName, fieldName, formatValue(value));
            case EXISTS_IN:
                return String.format("(%s != null && %s.contains(%s.toString()))", 
                        fieldName, formatValue(value), fieldName);
            case NOT_EXISTS_IN:
                return String.format("(%s != null && !%s.contains(%s.toString()))", 
                        fieldName, formatValue(value), fieldName);
            case RANGE:
                return String.format("(%s != null && %s >= %s && %s <= %s)",
                        fieldName, fieldName, formatValue(value), fieldName, formatValue(value2));
            case IS_EMPTY:
                return String.format("(%s == null || %s.toString().trim().isEmpty())", fieldName, fieldName);
            case IS_NOT_EMPTY:
                return String.format("(%s != null && !%s.toString().trim().isEmpty())", fieldName, fieldName);
            case LATER_THAN:
                return String.format("(%s != null && %s.isAfter(%s))", 
                        fieldName, fieldName, formatDateValue(value));
            case EARLIER_THAN:
                return String.format("(%s != null && %s.isBefore(%s))", 
                        fieldName, fieldName, formatDateValue(value));
            default:
                log.warn("不支持的操作符：{}", operator);
                return "false";
        }
    }

    /**
     * 格式化值为表达式字符串
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Collection) {
            return formatCollectionValue((Collection<?>) value);
        }
        return "\"" + value.toString() + "\"";
    }

    /**
     * 格式化日期值
     */
    private String formatDateValue(Object value) {
        if (value instanceof LocalDate) {
            LocalDate date = (LocalDate) value;
            return String.format("LocalDate.of(%d, %d, %d)",
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;
            return String.format("LocalDateTime.of(%d, %d, %d, %d, %d, %d)",
                    dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
                    dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
        }
        if (value instanceof LocalTime) {
            LocalTime time = (LocalTime) value;
            return String.format("LocalTime.of(%d, %d, %d)",
                    time.getHour(), time.getMinute(), time.getSecond());
        }
        return formatValue(value);
    }

    /**
     * 格式化集合值
     */
    private String formatCollectionValue(Collection<?> collection) {
        String items = collection.stream()
                .map(this::formatValue)
                .collect(Collectors.joining(", "));
        return "[" + items + "]";
    }

    private String getFieldName(MetadataValidationRuleDefinitionDO rule, Map<Long, String> fieldIdToNameMap) {
        String fieldCode = rule.getFieldCode();
        if (fieldCode != null && !fieldCode.trim().isEmpty()) {
            return fieldCode;
        }

        Long fieldId = rule.getFieldId();
        if (fieldId != null && fieldIdToNameMap.containsKey(fieldId)) {
            return fieldIdToNameMap.get(fieldId);
        }

        log.warn("无法确定字段名：ruleId={}, fieldId={}", rule.getId(), fieldId);
        return null;
    }

    private List<MetadataValidationRuleDefinitionDO> findChildRules(Long parentId, 
                                                                      List<MetadataValidationRuleDefinitionDO> allRules) {
        return allRules.stream()
                .filter(rule -> parentId.equals(rule.getParentRuleId()))
                .collect(Collectors.toList());
    }

    private List<MetadataValidationRuleGroupDO> findActiveRuleGroups(Long entityId) {
        QueryWrapper queryWrapper = ruleGroupRepository.query()
                .eq("entity_id", entityId)
                .eq("validation_type", "SELF_DEFINED")
                .eq("rg_status", 1)
                .eq("deleted", 0);
        return ruleGroupRepository.list(queryWrapper);
    }

    private Map<Long, String> buildFieldIdToNameMap(Long entityId) {
        QueryWrapper queryWrapper = entityFieldRepository.query()
                .eq("entity_id", entityId)
                .eq("deleted", 0);

        return entityFieldRepository.list(queryWrapper).stream()
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName));
    }

    /**
     * 构建完整的上下文
     * 确保所有字段都在 context 中，避免 MVEL 解析时报错
     *
     * @param field 当前字段
     * @param value 当前字段值
     * @param data 已有数据
     * @param fieldIdToNameMap 字段映射
     * @return 完整的上下文
     */
    private Map<String, Object> buildCompleteContext(MetadataEntityFieldDO field, 
                                                      Object value, 
                                                      Map<String, Object> data,
                                                      Map<Long, String> fieldIdToNameMap) {
        Map<String, Object> context = new HashMap<>();
        
        // 首先添加所有字段，默认值为 null
        for (String fieldName : fieldIdToNameMap.values()) {
            context.put(fieldName, null);
        }
        
        // 然后覆盖已有的数据
        if (data != null) {
            context.putAll(data);
        }
        
        // 最后设置当前字段的值
        context.put(field.getFieldName(), value);
        
        return context;
    }

    @Override
    public String getValidationType() {
        return "SELF_DEFINED";
    }

    @Override
    public boolean supports(String fieldType) {
        return true;
    }
}

