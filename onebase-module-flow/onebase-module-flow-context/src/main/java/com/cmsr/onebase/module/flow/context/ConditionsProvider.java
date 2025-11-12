package com.cmsr.onebase.module.flow.context;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/11/4 12:13
 */
public interface ConditionsProvider {

    OrExpression formatConditionsForExpression(List<Conditions> conditions, Map<String, Object> vars);

    OrExpression formatConditionsForValue(List<Conditions> conditions, Map<String, Object> vars);

    List<ExpressionItem> formatConditionItemsForValue(List<ConditionItem> conditionItems, Map<String, Object> vars);

    ExpressionItem formatConditionItemForValue(ConditionItem conditionItem, Map<String, Object> dataMap);
}
