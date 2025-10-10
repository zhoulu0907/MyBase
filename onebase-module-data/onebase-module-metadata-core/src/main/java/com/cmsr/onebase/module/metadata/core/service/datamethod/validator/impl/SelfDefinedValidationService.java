package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.framework.common.express.OpEnum;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleDefinitionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleDefinitionRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义规则校验服务
 * 
 * 直接评估规则定义实现复杂的自定义校验逻辑
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

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        List<MetadataValidationRuleGroupDO> ruleGroups = findActiveRuleGroups(entityId);
        if (ruleGroups.isEmpty()) {
            return;
        }

        Map<String, Object> context = buildContext(field, value, data);
        Map<Long, String> fieldIdToNameMap = buildFieldIdToNameMap(entityId);

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

            if (evaluateTopLevelRules(rules, context, fieldIdToNameMap)) {
                String message = Optional.ofNullable(ruleGroup.getPopPrompt())
                        .filter(s -> !s.trim().isEmpty())
                        .orElse("字段[" + field.getDisplayName() + "]不满足自定义校验规则：" + ruleGroup.getRgName());
                
                log.info("校验失败：field={}, group={}", field.getFieldName(), ruleGroup.getRgName());
                throw new IllegalArgumentException(message);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("规则组执行失败：groupId={}", ruleGroup.getId(), e);
            throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]校验执行失败：" + e.getMessage());
        }
    }

    private boolean evaluateTopLevelRules(List<MetadataValidationRuleDefinitionDO> allRules,
                                           Map<String, Object> context,
                                           Map<Long, String> fieldIdToNameMap) {
        // 顶级规则之间是 OR 关系
        return allRules.stream()
                .filter(rule -> rule.getParentRuleId() == null)
                .anyMatch(rule -> evaluateRule(rule, allRules, context, fieldIdToNameMap));
    }

    private boolean evaluateRule(MetadataValidationRuleDefinitionDO rule,
                                  List<MetadataValidationRuleDefinitionDO> allRules,
                                  Map<String, Object> context,
                                  Map<Long, String> fieldIdToNameMap) {
        if ("CONDITION".equals(rule.getLogicType())) {
            return evaluateCondition(rule, context, fieldIdToNameMap);
        }
        
        if ("LOGIC".equals(rule.getLogicType())) {
            return evaluateLogicNode(rule, allRules, context, fieldIdToNameMap);
        }
        
        log.warn("未知规则类型：ruleId={}, type={}", rule.getId(), rule.getLogicType());
        return false;
    }

    private boolean evaluateLogicNode(MetadataValidationRuleDefinitionDO rule,
                                       List<MetadataValidationRuleDefinitionDO> allRules,
                                       Map<String, Object> context,
                                       Map<Long, String> fieldIdToNameMap) {
        List<MetadataValidationRuleDefinitionDO> children = findChildRules(rule.getId(), allRules);
        
        // 无子规则时的处理
        if (children.isEmpty()) {
            return handleEmptyLogicNode(rule, context, fieldIdToNameMap);
        }

        // 有子规则时按 AND/OR 逻辑处理
        if ("AND".equals(rule.getLogicOperator())) {
            return children.stream()
                    .allMatch(child -> evaluateRule(child, allRules, context, fieldIdToNameMap));
        } else {
            return children.stream()
                    .anyMatch(child -> evaluateRule(child, allRules, context, fieldIdToNameMap));
        }
    }

    private boolean handleEmptyLogicNode(MetadataValidationRuleDefinitionDO rule,
                                          Map<String, Object> context,
                                          Map<Long, String> fieldIdToNameMap) {
        // 如果有 operator，按条件处理
        if (rule.getOperator() != null && !rule.getOperator().trim().isEmpty()) {
            log.warn("LOGIC节点无子规则但有operator，按CONDITION处理：ruleId={}", rule.getId());
            return evaluateCondition(rule, context, fieldIdToNameMap);
        }
        
        // 空逻辑节点：AND返回true，OR返回false
        log.warn("LOGIC节点既无子规则也无operator：ruleId={}", rule.getId());
        return "AND".equals(rule.getLogicOperator());
    }

    private boolean evaluateCondition(MetadataValidationRuleDefinitionDO rule,
                                       Map<String, Object> context,
                                       Map<Long, String> fieldIdToNameMap) {
        String fieldName = getFieldName(rule, fieldIdToNameMap);
        if (fieldName == null) {
            return false;
        }

        Object fieldValue = context.get(fieldName);
        Object compareValue = rule.getFieldValue();
        Object compareValue2 = rule.getFieldValue2();

        try {
            OpEnum operator = OpEnum.valueOf(rule.getOperator());
            return executeOperator(operator, fieldValue, compareValue, compareValue2);
        } catch (Exception e) {
            log.error("条件评估失败：field={}, operator={}", fieldName, rule.getOperator(), e);
            return false;
        }
    }

    private boolean executeOperator(OpEnum operator, Object fieldValue, Object compareValue, Object compareValue2) {
        // 特殊处理：IS_EMPTY 和 IS_NOT_EMPTY
        if (operator == OpEnum.IS_EMPTY) {
            return fieldValue == null || fieldValue.toString().trim().isEmpty();
        }
        if (operator == OpEnum.IS_NOT_EMPTY) {
            return fieldValue != null && !fieldValue.toString().trim().isEmpty();
        }

        // 其他操作符需要非null值
        if (!isValidForComparison(fieldValue, compareValue)) {
            return false;
        }

        switch (operator) {
            case EQUALS:
                return Objects.equals(fieldValue, compareValue);
            case NOT_EQUALS:
                return !Objects.equals(fieldValue, compareValue);
            case GREATER_THAN:
                return compareNumbers(fieldValue, compareValue) > 0;
            case GREATER_EQUALS:
                return compareNumbers(fieldValue, compareValue) >= 0;
            case LESS_THAN:
                return compareNumbers(fieldValue, compareValue) < 0;
            case LESS_EQUALS:
                return compareNumbers(fieldValue, compareValue) <= 0;
            case CONTAINS:
                return fieldValue.toString().contains(compareValue.toString());
            case NOT_CONTAINS:
                return !fieldValue.toString().contains(compareValue.toString());
            case EXISTS_IN:
                return compareValue.toString().contains(fieldValue.toString());
            case NOT_EXISTS_IN:
                return !compareValue.toString().contains(fieldValue.toString());
            case RANGE:
                return isValidForComparison(compareValue2, null) &&
                       compareNumbers(fieldValue, compareValue) >= 0 &&
                       compareNumbers(fieldValue, compareValue2) <= 0;
            case LATER_THAN:
                return compareDates(fieldValue, compareValue) > 0;
            case EARLIER_THAN:
                return compareDates(fieldValue, compareValue) < 0;
            default:
                log.warn("不支持的操作符：{}", operator);
                return false;
        }
    }

    private boolean isValidForComparison(Object value1, Object value2) {
        if (value2 == null) {
            return value1 != null;
        }
        return value1 != null && value2 != null;
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

    private int compareNumbers(Object value1, Object value2) {
        return toBigDecimal(value1).compareTo(toBigDecimal(value2));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private int compareDates(Object value1, Object value2) {
        if (value1 instanceof LocalDateTime && value2 instanceof LocalDateTime) {
            return ((LocalDateTime) value1).compareTo((LocalDateTime) value2);
        }
        if (value1 instanceof LocalDate && value2 instanceof LocalDate) {
            return ((LocalDate) value1).compareTo((LocalDate) value2);
        }
        return value1.toString().compareTo(value2.toString());
    }

    private List<MetadataValidationRuleDefinitionDO> findChildRules(Long parentId, List<MetadataValidationRuleDefinitionDO> allRules) {
        return allRules.stream()
                .filter(rule -> parentId.equals(rule.getParentRuleId()))
                .collect(Collectors.toList());
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
        
        return entityFieldRepository.findAllByConfig(cs).stream()
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName));
    }

    private Map<String, Object> buildContext(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        Map<String, Object> context = new HashMap<>(data != null ? data : Collections.emptyMap());
        context.put(field.getFieldName(), value != null ? value : "");
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
