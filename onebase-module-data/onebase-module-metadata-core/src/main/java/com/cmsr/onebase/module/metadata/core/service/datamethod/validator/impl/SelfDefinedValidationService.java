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

/**
 * 自定义规则校验服务
 * 
 * 直接评估规则定义实现复杂的自定义校验逻辑
 * 从 MetadataValidationRuleGroupDO 和 MetadataValidationRuleDefinitionDO 读取规则配置
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
        // 查询该实体下所有 SELF_DEFINED 类型的规则组
        List<MetadataValidationRuleGroupDO> ruleGroups = findSelfDefinedRuleGroups(entityId);
        
        if (ruleGroups.isEmpty()) {
            return; // 没有自定义规则组，跳过校验
        }

        // 准备上下文数据（包含 fieldName 和 fieldId 作为 key）
        Map<String, Object> context = prepareContext(field, value, data);
        
        // 构建 fieldId 到 fieldName 的映射
        Map<Long, String> fieldIdToNameMap = buildFieldIdToNameMap(entityId);

        // 遍历所有规则组并执行校验
        for (MetadataValidationRuleGroupDO ruleGroup : ruleGroups) {
            // 检查规则组是否启用
            if (ruleGroup.getRgStatus() == null || ruleGroup.getRgStatus() != 1) {
                continue; // 跳过未启用的规则组
            }

            try {
                // 查询规则组下的所有规则定义
                List<MetadataValidationRuleDefinitionDO> ruleDefinitions = 
                        ruleDefinitionRepository.selectByGroupId(ruleGroup.getId());

                if (ruleDefinitions.isEmpty()) {
                    log.warn("规则组无规则定义，跳过：groupId={}, groupName={}", 
                            ruleGroup.getId(), ruleGroup.getRgName());
                    continue;
                }

                // 直接评估规则定义
                boolean result = evaluateRules(ruleDefinitions, context, fieldIdToNameMap);
                
                // 如果评估结果为true，表示不满足条件，抛出异常
                if (result) {
                    String errorMessage = ruleGroup.getPopPrompt() != null && !ruleGroup.getPopPrompt().trim().isEmpty()
                            ? ruleGroup.getPopPrompt()
                            : "字段[" + field.getDisplayName() + "]不满足自定义校验规则：" + ruleGroup.getRgName();
                    
                    log.info("自定义规则校验失败：fieldName={}, groupId={}, groupName={}, message={}", 
                            field.getFieldName(), ruleGroup.getId(), ruleGroup.getRgName(), errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }

            } catch (IllegalArgumentException e) {
                // 重新抛出业务异常
                throw e;
            } catch (Exception e) {
                log.error("自定义规则校验执行失败：groupId={}, groupName={}, fieldName={}", 
                        ruleGroup.getId(), ruleGroup.getRgName(), field.getFieldName(), e);
                throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]自定义规则校验执行失败：" + e.getMessage());
            }
        }
    }

    /**
     * 查询实体下所有 SELF_DEFINED 类型的规则组
     *
     * @param entityId 实体ID
     * @return 规则组列表
     */
    private List<MetadataValidationRuleGroupDO> findSelfDefinedRuleGroups(Long entityId) {
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(MetadataValidationRuleGroupDO.ENTITY_ID, entityId);
        cs.and(MetadataValidationRuleGroupDO.VALIDATION_TYPE, "SELF_DEFINED");
        cs.and("deleted", 0);
        return ruleGroupRepository.findAllByConfig(cs);
    }

    /**
     * 构建 fieldId 到 fieldName 的映射
     *
     * @param entityId 实体ID
     * @return fieldId 到 fieldName 的映射
     */
    private Map<Long, String> buildFieldIdToNameMap(Long entityId) {
        Map<Long, String> fieldIdToNameMap = new HashMap<>();
        try {
            // 查询实体下的所有字段
            DefaultConfigStore cs = new DefaultConfigStore();
            cs.and("entity_id", entityId);
            cs.and("deleted", 0);
            List<MetadataEntityFieldDO> fields = entityFieldRepository.findAllByConfig(cs);
            
            for (MetadataEntityFieldDO fieldDO : fields) {
                fieldIdToNameMap.put(fieldDO.getId(), fieldDO.getFieldName());
            }
            
            log.debug("构建字段映射：entityId={}, 字段数量={}", entityId, fieldIdToNameMap.size());
        } catch (Exception e) {
            log.error("构建字段映射失败：entityId={}", entityId, e);
        }
        return fieldIdToNameMap;
    }

    /**
     * 评估规则定义列表
     * 顶级规则（parentRuleId为null）之间是 OR 关系
     *
     * @param ruleDefinitions 规则定义列表
     * @param context 上下文数据
     * @param fieldIdToNameMap fieldId到fieldName的映射
     * @return 评估结果，true表示不满足条件
     */
    private boolean evaluateRules(List<MetadataValidationRuleDefinitionDO> ruleDefinitions, 
                                   Map<String, Object> context,
                                   Map<Long, String> fieldIdToNameMap) {
        // 获取所有顶级规则
        List<MetadataValidationRuleDefinitionDO> topLevelRules = ruleDefinitions.stream()
                .filter(rule -> rule.getParentRuleId() == null)
                .toList();

        if (topLevelRules.isEmpty()) {
            return false;
        }

        // 顶级规则之间是 OR 关系：任一满足即返回true
        for (MetadataValidationRuleDefinitionDO topRule : topLevelRules) {
            boolean result = evaluateRule(topRule, ruleDefinitions, context, fieldIdToNameMap);
            if (result) {
                return true; // 只要有一个顶级规则满足，就返回true
            }
        }

        return false;
    }

    /**
     * 评估单个规则
     *
     * @param rule 规则定义
     * @param allRules 所有规则列表
     * @param context 上下文数据
     * @param fieldIdToNameMap fieldId到fieldName的映射
     * @return 评估结果
     */
    private boolean evaluateRule(MetadataValidationRuleDefinitionDO rule, 
                                  List<MetadataValidationRuleDefinitionDO> allRules, 
                                  Map<String, Object> context,
                                  Map<Long, String> fieldIdToNameMap) {
        if ("CONDITION".equals(rule.getLogicType())) {
            // 条件节点：直接评估
            return evaluateCondition(rule, context, fieldIdToNameMap);
        } else if ("LOGIC".equals(rule.getLogicType())) {
            // 逻辑节点：递归评估子规则
            List<MetadataValidationRuleDefinitionDO> childRules = findChildRules(rule.getId(), allRules);
            
            if (childRules.isEmpty()) {
                // LOGIC 节点没有子规则，检查是否实际上是一个条件节点
                // 如果有 operator，说明应该按条件来评估
                if (rule.getOperator() != null && !rule.getOperator().trim().isEmpty()) {
                    log.warn("LOGIC节点没有子规则但有operator，按CONDITION处理：ruleId={}, fieldId={}, operator={}", 
                            rule.getId(), rule.getFieldId(), rule.getOperator());
                    return evaluateCondition(rule, context, fieldIdToNameMap);
                }
                
                // 既没有子规则，也没有条件字段，按照逻辑学原则处理：
                // - AND：空集的全称命题为真（vacuous truth）
                // - OR：空集的存在命题为假
                log.warn("LOGIC节点既没有子规则也没有operator：ruleId={}, logicOperator={}", 
                        rule.getId(), rule.getLogicOperator());
                return "AND".equals(rule.getLogicOperator());
            }

            if ("AND".equals(rule.getLogicOperator())) {
                // AND 逻辑：所有子规则都必须满足
                for (MetadataValidationRuleDefinitionDO childRule : childRules) {
                    if (!evaluateRule(childRule, allRules, context, fieldIdToNameMap)) {
                        return false;
                    }
                }
                return true;
            } else if ("OR".equals(rule.getLogicOperator())) {
                // OR 逻辑：任一子规则满足即可
                for (MetadataValidationRuleDefinitionDO childRule : childRules) {
                    if (evaluateRule(childRule, allRules, context, fieldIdToNameMap)) {
                        return true;
                    }
                }
                return false;
            }
        }

        // 未知的 logicType，记录警告并返回 false
        log.warn("未知的规则类型：ruleId={}, logicType={}", rule.getId(), rule.getLogicType());
        return false;
    }

    /**
     * 评估单个条件
     *
     * @param rule 规则定义
     * @param context 上下文数据
     * @param fieldIdToNameMap fieldId到fieldName的映射
     * @return 评估结果
     */
    private boolean evaluateCondition(MetadataValidationRuleDefinitionDO rule, 
                                       Map<String, Object> context,
                                       Map<Long, String> fieldIdToNameMap) {
        // 获取字段名：优先使用 fieldCode，如果为空则通过 fieldId 查找
        String fieldCode = rule.getFieldCode();
        if (fieldCode == null || fieldCode.trim().isEmpty()) {
            // fieldCode 为空，尝试通过 fieldId 获取字段名
            Long fieldId = rule.getFieldId();
            if (fieldId != null && fieldIdToNameMap.containsKey(fieldId)) {
                fieldCode = fieldIdToNameMap.get(fieldId);
                log.debug("通过fieldId获取字段名：fieldId={}, fieldName={}", fieldId, fieldCode);
            } else {
                log.warn("无法确定字段名：ruleId={}, fieldId={}, fieldCode={}", 
                        rule.getId(), fieldId, rule.getFieldCode());
                return false;
            }
        }
        
        Object fieldValue = context.get(fieldCode);
        Object compareValue = rule.getFieldValue();
        Object compareValue2 = rule.getFieldValue2();
        
        log.debug("评估条件：fieldCode={}, fieldValue={}, operator={}, compareValue={}", 
                fieldCode, fieldValue, rule.getOperator(), compareValue);
        
        try {
            OpEnum operator = OpEnum.valueOf(rule.getOperator());
            
            switch (operator) {
                case EQUALS:
                    // 对于 EQUALS：只有两个值都不为null且相等时才返回true
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return Objects.equals(fieldValue, compareValue);
                    
                case NOT_EQUALS:
                    // 对于 NOT_EQUALS：只有两个值都不为null且不相等时才返回true
                    // 如果任一值为null，返回false（不触发规则）
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return !Objects.equals(fieldValue, compareValue);
                    
                case GREATER_THAN:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareNumbers(fieldValue, compareValue) > 0;
                    
                case GREATER_EQUALS:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareNumbers(fieldValue, compareValue) >= 0;
                    
                case LESS_THAN:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareNumbers(fieldValue, compareValue) < 0;
                    
                case LESS_EQUALS:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareNumbers(fieldValue, compareValue) <= 0;
                    
                case CONTAINS:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return fieldValue.toString().contains(compareValue.toString());
                    
                case NOT_CONTAINS:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return !fieldValue.toString().contains(compareValue.toString());
                    
                case EXISTS_IN:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareValue.toString().contains(fieldValue.toString());
                    
                case NOT_EXISTS_IN:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return !compareValue.toString().contains(fieldValue.toString());
                    
                case RANGE:
                    if (fieldValue == null || compareValue == null || compareValue2 == null) {
                        return false;
                    }
                    int cmp1 = compareNumbers(fieldValue, compareValue);
                    int cmp2 = compareNumbers(fieldValue, compareValue2);
                    return cmp1 >= 0 && cmp2 <= 0;
                    
                case IS_EMPTY:
                    // IS_EMPTY 本身就是检查 null 或空，所以这里不需要额外的 null 检查
                    return fieldValue == null || fieldValue.toString().trim().isEmpty();
                    
                case IS_NOT_EMPTY:
                    // IS_NOT_EMPTY 检查不为空，null 应该返回 false
                    return fieldValue != null && !fieldValue.toString().trim().isEmpty();
                    
                case LATER_THAN:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareDates(fieldValue, compareValue) > 0;
                    
                case EARLIER_THAN:
                    if (fieldValue == null || compareValue == null) {
                        return false;
                    }
                    return compareDates(fieldValue, compareValue) < 0;
                    
                default:
                    log.warn("不支持的操作符：{}", operator);
                    return false;
            }
        } catch (Exception e) {
            log.error("条件评估失败：fieldCode={}, operator={}", fieldCode, rule.getOperator(), e);
            return false;
        }
    }

    /**
     * 比较数值
     * 注意：调用此方法前必须确保 value1 和 value2 都不为 null
     *
     * @param value1 值1（不能为null）
     * @param value2 值2（不能为null）
     * @return 比较结果：-1表示小于，0表示等于，1表示大于
     */
    private int compareNumbers(Object value1, Object value2) {
        BigDecimal num1 = toBigDecimal(value1);
        BigDecimal num2 = toBigDecimal(value2);
        
        return num1.compareTo(num2);
    }

    /**
     * 转换为 BigDecimal
     *
     * @param value 值
     * @return BigDecimal
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        } else {
            return new BigDecimal(value.toString());
        }
    }

    /**
     * 比较日期
     * 注意：调用此方法前必须确保 value1 和 value2 都不为 null
     *
     * @param value1 值1（不能为null）
     * @param value2 值2（不能为null）
     * @return 比较结果
     */
    private int compareDates(Object value1, Object value2) {
        if (value1 instanceof LocalDateTime && value2 instanceof LocalDateTime) {
            return ((LocalDateTime) value1).compareTo((LocalDateTime) value2);
        } else if (value1 instanceof LocalDate && value2 instanceof LocalDate) {
            return ((LocalDate) value1).compareTo((LocalDate) value2);
        } else {
            // 尝试字符串比较
            return value1.toString().compareTo(value2.toString());
        }
    }

    /**
     * 查找子规则
     *
     * @param parentId 父规则ID
     * @param allRules 所有规则列表
     * @return 子规则列表
     */
    private List<MetadataValidationRuleDefinitionDO> findChildRules(Long parentId, 
                                                                      List<MetadataValidationRuleDefinitionDO> allRules) {
        return allRules.stream()
                .filter(rule -> parentId.equals(rule.getParentRuleId()))
                .toList();
    }

    /**
     * 准备表达式评估的上下文数据
     *
     * @param field 字段信息
     * @param value 字段值
     * @param data 完整数据对象
     * @return 上下文数据
     */
    private Map<String, Object> prepareContext(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        Map<String, Object> context = new HashMap<>();
        
        // 将完整数据对象作为上下文
        if (data != null) {
            context.putAll(data);
        }
        
        // 添加当前字段的值
        context.put(field.getFieldName(), value != null ? value : "");
        
        return context;
    }

    @Override
    public String getValidationType() {
        return "SELF_DEFINED";
    }

    @Override
    public boolean supports(String fieldType) {
        // 自定义规则支持所有字段类型
        return true;
    }
}

