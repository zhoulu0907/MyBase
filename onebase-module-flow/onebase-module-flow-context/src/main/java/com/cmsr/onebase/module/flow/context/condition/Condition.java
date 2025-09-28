package com.cmsr.onebase.module.flow.context.condition;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/26 15:07
 */
public class Condition {

    public static List<ConditionItem> createCondition(List<Map<String, Object>> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return new ArrayList<>();
        }
        List<ConditionItem> conditionItems = new ArrayList<>();
        // 遍历外层conditions数组（OR关系）
        for (Map<String, Object> outerCondition : conditions) {
            List<Map<String, Object>> innerConditions = (List<Map<String, Object>>) MapUtils.getObject(outerCondition, "conditions");
            List<RuleItem> ruleItems = createRuleItems(innerConditions);
            if (!ruleItems.isEmpty()) {
                ConditionItem conditionItem = new ConditionItem();
                conditionItem.setRules(ruleItems);
                conditionItems.add(conditionItem);
            }
        }
        return conditionItems;
    }

    private static List<RuleItem> createRuleItems(List<Map<String, Object>> innerConditions) {
        if (CollectionUtils.isEmpty(innerConditions)) {
            return new ArrayList<>();
        }
        List<RuleItem> ruleItems = new ArrayList<>();
        for (Map<String, Object> innerCondition : innerConditions) {
            RuleItem ruleItem = createRuleItem(innerCondition);
            ruleItems.add(ruleItem);
        }
        return ruleItems;
    }

    private static RuleItem createRuleItem(Map<String, Object> innerCondition) {
        RuleItem ruleItem = new RuleItem();
        // 获取字段ID
        String fieldId = MapUtils.getString(innerCondition, "fieldId");
        ruleItem.setFieldId(fieldId);
        // 获取操作符
        ruleItem.setOp(MapUtils.getString(innerCondition, "op"));
        // 获取操作符类型
        ruleItem.setOperatorType(MapUtils.getString(innerCondition, "operatorType"));
        // 获取值列表
        Object value = MapUtils.getObject(innerCondition, "value");
        if (value != null) {
            if (value instanceof List l) {
                List<String> valueList = new ArrayList<>();
                for (Object o : l) {
                    valueList.add(o.toString());
                }
                ruleItem.setValue(valueList);
            } else {
                ruleItem.setValue(value.toString());
            }
        }
        return ruleItem;
    }
}
