package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.framework.common.express.OperatorTypeEnum;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.condition.ConditionsSupport;
import com.cmsr.onebase.module.flow.context.express.AndExpression;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.yomahub.liteflow.core.NodeComponent;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author：huangjie
 * @Date：2025/9/28 9:17
 */
@Component
public class ConditionsProvider {


    /**
     * 格式化条件，把其规范为表达式能执行的。左值是合法的变量名称，右值是具体的值或者表达式。
     * <p>
     * 注意：
     * 右值是变量，保留表达式变量。
     * 右值是函数，计算出相关的值。
     *
     * @param nodeComponent
     * @param conditions
     * @param inLoopDepth
     * @return
     */
    public OrExpression formatConditionsForExpression(NodeComponent nodeComponent, VariableContext variableContext, InLoopDepth inLoopDepth, List<Conditions> conditions) {
        OrExpression orExpression = ConditionsSupport.convertToOrExpresses(conditions);
        if (orExpression == null) {
            return null;
        }
        for (AndExpression andExpression : ListUtils.emptyIfNull(orExpression.getAndExpressions())) {
            for (ExpressionItem expressionItem : ListUtils.emptyIfNull(andExpression.getExpressionItems())) {
                formatRuleItemForExpression(nodeComponent, variableContext, inLoopDepth, expressionItem);
            }
        }
        return orExpression;
    }

    /**
     * 格式化条件，把其规范为值。左值是合法的变量名称，右值是具体的值。
     * 主要工作！如果右值是表达式，提取相关的值。
     *
     * @param conditions
     * @param variableContext
     * @return
     */
    public OrExpression formatConditionsForValue(NodeComponent nodeComponent, VariableContext variableContext, InLoopDepth inLoopDepth, List<Conditions> conditions) {
        OrExpression orExpression = ConditionsSupport.convertToOrExpresses(conditions);
        for (AndExpression andExpression : orExpression.getAndExpressions()) {
            for (ExpressionItem expressionItem : andExpression.getExpressionItems()) {
                formatRuleItemForValue(nodeComponent, variableContext, inLoopDepth, expressionItem);
            }
        }
        return orExpression;
    }

    public List<ExpressionItem> formatConditionItemsForValue(NodeComponent nodeComponent, VariableContext variableContext, InLoopDepth inLoopDepth, List<ConditionItem> conditionItems) {
        List<ExpressionItem> expressionItems = new ArrayList<>();
        for (ConditionItem conditionItem : conditionItems) {
            ExpressionItem expressionItem = ConditionsSupport.convertToExpressesItem(conditionItem);
            formatRuleItemForValue(nodeComponent, variableContext, inLoopDepth, expressionItem);
            expressionItems.add(expressionItem);
        }
        return expressionItems;
    }

    private void formatRuleItemForExpression(NodeComponent nodeComponent, VariableContext variableContext, InLoopDepth inLoopDepth, ExpressionItem expressionItem) {
        String fieldId = formatFieldIdForExpression(nodeComponent, inLoopDepth, expressionItem.getKey().toString());
        expressionItem.setKey(fieldId);

        if (expressionItem.getOperatorType() == OperatorTypeEnum.VALUE) {
            // do nothing;
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            Object value = formatValueForExpression(nodeComponent, inLoopDepth, expressionItem.getValue().toString());
            expressionItem.setValue(value);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.FORMULA) {
            //TODO 公式
        }
    }


    private void formatRuleItemForValue(NodeComponent nodeComponent, VariableContext variableContext, InLoopDepth inLoopDepth, ExpressionItem expressionItem) {
        // 转换值
        if (expressionItem.getOperatorType() == OperatorTypeEnum.VALUE) {
            // do nothing;
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            String valueExp = formatValueForExpression(nodeComponent, inLoopDepth, expressionItem.getValue().toString());
            Object value = variableContext.getVariableByExpression(valueExp);
            expressionItem.setValue(value);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.FORMULA) {
            //TODO 公式
        }
    }


    public ExpressionItem formatConditionItemForValue(int index, VariableContext variableContext, ConditionItem conditionItem) {
        ExpressionItem expressionItem = ConditionsSupport.convertToExpressesItem(conditionItem);
        if (expressionItem.getOperatorType() == OperatorTypeEnum.VALUE) {
            // do nothing
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            String valueExp = formatValueForExpression(conditionItem.getValue().toString(), index);
            Object value = variableContext.getVariableByExpression(valueExp);
            expressionItem.setValue(value);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.FORMULA) {
            //TODO 公式
        }
        return expressionItem;
    }


    private String formatFieldIdForExpression(NodeComponent nodeComponent, InLoopDepth inLoopDepth, String exp) {
        if (NumberUtils.isParsable(exp)) {
            return exp;
        }
        int loopDepthValue = inLoopDepth.getLoopDepthValue(exp);
        if (loopDepthValue >= 0) {
            Integer loopIndex = nodeComponent.getPreNLoopIndex(loopDepthValue);
            exp = formatValueForExpression(exp, loopIndex);
        }
        return exp;
    }

    private String formatValueForExpression(NodeComponent nodeComponent, InLoopDepth inLoopDepth, String exp) {
        int loopDepthValue = inLoopDepth.getLoopDepthValue(exp);
        if (loopDepthValue >= 0) {
            Integer loopIndex = nodeComponent.getPreNLoopIndex(loopDepthValue);
            exp = formatValueForExpression(exp, loopIndex);
        }
        return exp;
    }

    private String formatValueForExpression(String exp, int index) {
        String[] split = StringUtils.split(exp, '.');
        return split[0] + "[" + index + "]." + split[1];
    }


}
