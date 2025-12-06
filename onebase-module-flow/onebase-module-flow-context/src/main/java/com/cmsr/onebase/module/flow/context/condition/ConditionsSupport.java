package com.cmsr.onebase.module.flow.context.condition;

import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.enums.OperatorTypeEnum;
import com.cmsr.onebase.module.flow.context.express.AndExpression;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 19:20
 */
public class ConditionsSupport {

    public static OrExpression convertToOrExpresses(List<Conditions> conditions) {
        List<AndExpression> andExpressions = new ArrayList<>();
        if (conditions != null) {
            for (Conditions condition : conditions) {
                AndExpression andExpression = convertToAndExpresses(condition.getConditions());
                andExpressions.add(andExpression);
            }
        }
        OrExpression orExpression = new OrExpression();
        orExpression.setAndExpressions(andExpressions);
        return orExpression;
    }

    private static AndExpression convertToAndExpresses(List<ConditionItem> conditions) {
        List<ExpressionItem> expressionItems = new ArrayList<>();
        if (conditions != null) {
            for (ConditionItem conditionItem : conditions) {
                ExpressionItem expressionItem = convertToExpressesItem(conditionItem);
                expressionItems.add(expressionItem);
            }
        }
        AndExpression andExpression = new AndExpression();
        andExpression.setExpressionItems(expressionItems);
        return andExpression;
    }

    public static ExpressionItem convertToExpressesItem(ConditionItem conditionItem) {
        ExpressionItem expressionItem = new ExpressionItem();
        expressionItem.setFieldKey(conditionItem.getFieldKey());
        expressionItem.setOp(OpEnum.getByName(conditionItem.getOp()));
        expressionItem.setOperatorType(OperatorTypeEnum.getByCode(conditionItem.getOperatorType()));
        expressionItem.setFieldTypeEnum(conditionItem.getFieldTypeEnum());
        expressionItem.setFieldValue(conditionItem.getValue());
        return expressionItem;
    }
}
