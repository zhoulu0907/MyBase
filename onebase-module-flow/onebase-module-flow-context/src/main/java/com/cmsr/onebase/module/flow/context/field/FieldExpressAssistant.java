package com.cmsr.onebase.module.flow.context.field;

import com.cmsr.onebase.framework.common.express.JdbcTypeConvertor;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.express.AndExpresses;
import com.cmsr.onebase.module.flow.context.express.ExpressItem;
import com.cmsr.onebase.module.flow.context.express.OrExpresses;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/17 13:23
 */
@Slf4j
@Setter
@Component
public class FieldExpressAssistant {

    /**
     * 转换字段数据
     *
     * @param inputParams
     * @return
     */
    public Map<String, Object> convertInputParamsData(Map<Long, String> inputParams, Map<Long, FieldInfo> fieldInfoMap) {
        if (inputParams == null || inputParams.isEmpty()) {
            return new HashMap<>();
        }
        return convertInputParamsToResult(inputParams, fieldInfoMap);
    }


    /**
     * @param conditions
     */
    public OrExpresses convertToExpresses(List<ConditionItem> conditions, Map<Long, FieldInfo> fieldInfoMap) {
        if (conditions == null || conditions.isEmpty()) {
            return new OrExpresses();
        }
        return convertConditionsToOrExpresses(conditions, fieldInfoMap);
    }

    /**
     * 将条件列表转换为OrExpresses
     */
    private OrExpresses convertConditionsToOrExpresses(List<ConditionItem> conditions,
                                                       Map<Long, FieldInfo> fieldInfoMap) {
        OrExpresses orExpresses = new OrExpresses();
        List<AndExpresses> andExpressionsList = new ArrayList<>();

        try {
            for (ConditionItem condition : conditions) {
                if (condition == null || condition.getRules() == null || condition.getRules().isEmpty()) {
                    continue; // 跳过空的条件项
                }

                AndExpresses andExpresses = convertConditionToAndExpresses(condition, fieldInfoMap);
                if (andExpresses != null && andExpresses.getExpressItems() != null && !andExpresses.getExpressItems().isEmpty()) {
                    andExpressionsList.add(andExpresses);
                }
            }

            orExpresses.setExpressesList(andExpressionsList);
            return orExpresses;

        } catch (Exception e) {
            log.error("转换条件表达式时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("转换条件表达式失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将单个条件项转换为AndExpresses
     */
    private AndExpresses convertConditionToAndExpresses(ConditionItem condition,
                                                        Map<Long, FieldInfo> fieldInfoMap) {
        AndExpresses andExpresses = new AndExpresses();
        List<ExpressItem> expressItems = new ArrayList<>();

        for (RuleItem rule : condition.getRules()) {
            if (rule == null) {
                continue; // 跳过空的规则项
            }

            ExpressItem expressItem = convertRuleToExpressItem(rule, fieldInfoMap);
            if (expressItem != null) {
                expressItems.add(expressItem);
            }
        }

        andExpresses.setExpressItems(expressItems);
        return andExpresses;
    }

    /**
     * 将RuleItem转换为ExpressItem
     */
    private ExpressItem convertRuleToExpressItem(RuleItem rule, Map<Long, FieldInfo> fieldInfoMap) {
        try {
            // 参数校验
            if (rule.getFieldId() == null || rule.getFieldId().trim().isEmpty()) {
                log.warn("规则项的字段ID为空，跳过处理");
                return null;
            }

            Long fieldId = NumberUtils.toLong(rule.getFieldId());
            if (fieldId == null) {
                log.warn("无法解析字段ID: {}", rule.getFieldId());
                return null;
            }

            FieldInfo fieldInfo = fieldInfoMap.get(fieldId);
            if (fieldInfo == null) {
                log.warn("找不到字段ID为 {} 的字段信息", fieldId);
                return null;
            }

            // 字段名校验
            if (fieldInfo.getFieldName() == null || fieldInfo.getFieldName().trim().isEmpty()) {
                log.warn("字段ID {} 对应的字段名为空", fieldId);
                return null;
            }

            ExpressItem expressItem = new ExpressItem();
            expressItem.setKey(fieldInfo.getFieldName());
            expressItem.setOperatorType(rule.getOp());

            // 转换值
            Object convertedValue = convertRuleValue(rule, fieldInfo);
            expressItem.setValue(convertedValue);

            return expressItem;

        } catch (Exception e) {
            log.error("转换规则项时发生错误，字段ID: {}, 错误: {}", rule.getFieldId(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转换规则值
     */
    private Object convertRuleValue(RuleItem rule, FieldInfo fieldInfo) {
        if (rule.getValue() == null || rule.getValue().isEmpty()) {
            return null;
        }

        try {
            if (rule.getValue().size() == 1) {
                // 单个值
                String value = rule.getValue().get(0);
                if (value == null) {
                    return null;
                }
                return JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), value);
            } else {
                // 多个值，转换为数组
                Object[] convertedValues = new Object[rule.getValue().size()];
                for (int i = 0; i < rule.getValue().size(); i++) {
                    String value = rule.getValue().get(i);
                    if (value != null) {
                        convertedValues[i] = JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), value);
                    }
                }
                return convertedValues;
            }
        } catch (Exception e) {
            log.warn("转换规则值时发生错误，字段: {}, JDBC类型: {}, 值: {}, 错误: {}",
                    fieldInfo.getFieldName(), fieldInfo.getJdbcType(), rule.getValue(), e.getMessage());
            // 转换失败时返回原始值
            return rule.getValue().size() == 1 ? rule.getValue().get(0) : rule.getValue().toArray();
        }
    }

    /**
     * 提取字段ID列表
     */
    public List<Long> extractFieldIds(Map<Long, String> inputParams) {
        return inputParams.keySet().stream().collect(Collectors.toList());
    }

    /**
     * 从条件列表中收集所有字段ID
     */
    public List<Long> extractFieldIds(List<ConditionItem> conditions) {
        return conditions.stream()
                .flatMap(condition -> condition.getRules().stream())
                .map(ruleItem -> NumberUtils.toLong(ruleItem.getFieldId()))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 转换输入参数为结果映射
     */
    private Map<String, Object> convertInputParamsToResult(Map<Long, String> inputParams,
                                                           Map<Long, FieldInfo> fieldInfoMap) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<Long, String> entry : inputParams.entrySet()) {
            Long fieldId = entry.getKey();
            String inputValue = entry.getValue();

            FieldInfo fieldInfo = fieldInfoMap.get(fieldId);
            if (fieldInfo == null) {
                throw new IllegalArgumentException("找不到字段ID为 " + fieldId + " 的字段信息");
            }

            Object convertedValue = convertFieldValue(fieldId, fieldInfo, inputValue);
            result.put(fieldInfo.getFieldName(), convertedValue);
        }

        return result;
    }


    /**
     * 转换字段值
     */
    private Object convertFieldValue(Long fieldId, FieldInfo fieldInfo, String inputValue) {
        if (inputValue == null || fieldInfo.getJdbcType() == null) {
            return inputValue;
        }

        try {
            return JdbcTypeConvertor.convert(fieldInfo.getJdbcType(), inputValue);
        } catch (Exception e) {
            log.warn("字段数据转换失败，字段ID: {}, 字段名: {}, JDBC类型: {}, 输入值: {}, 错误: {}",
                    fieldId, fieldInfo.getFieldName(), fieldInfo.getJdbcType(), inputValue, e.getMessage());
            return inputValue; // 转换失败时保留原始字符串值
        }
    }


}
