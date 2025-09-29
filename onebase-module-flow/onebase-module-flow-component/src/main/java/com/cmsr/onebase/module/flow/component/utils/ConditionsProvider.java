package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.framework.common.express.OperatorTypeEnum;
import com.cmsr.onebase.module.flow.context.InLoopDepth;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.yomahub.liteflow.core.NodeComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

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
    public List<ConditionItem> formatForExpression(NodeComponent nodeComponent, List<ConditionItem> conditions, InLoopDepth inLoopDepth) {
        for (ConditionItem condition : conditions) {
            formatRuleItemsForExpression(nodeComponent, condition.getRules(), inLoopDepth);
        }
        return conditions;
    }

    public List<RuleItem> formatRuleItemsForExpression(NodeComponent nodeComponent, List<RuleItem> ruleItems, InLoopDepth inLoopDepth) {
        for (RuleItem ruleItem : ruleItems) {
            formatRuleItemForExpression(nodeComponent, inLoopDepth, ruleItem);
        }
        return ruleItems;
    }

    public RuleItem formatRuleItemForExpression(NodeComponent nodeComponent, InLoopDepth inLoopDepth, RuleItem ruleItem) {
        String fieldId = ruleItem.getFieldId();
        ruleItem.setFieldId(formatFieldIdForExpression(nodeComponent, inLoopDepth, fieldId));
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(ruleItem.getOperatorType());
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            // do nothing
        } else if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            String valueExp = formatValueForExpression(nodeComponent, inLoopDepth, ruleItem.getValue().toString());
            ruleItem.setValue(valueExp);
        }
        return ruleItem;
    }

    public RuleItem formatRuleItemForExpression(int index, RuleItem ruleItem) {
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(ruleItem.getOperatorType());
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            // do nothing
        } else if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            String valueExp = formatValueForExpression(ruleItem.getValue().toString(), index);
            ruleItem.setValue(valueExp);
        }
        return ruleItem;
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

    public String formatValueForExpression(String exp, int index) {
        String[] split = StringUtils.split(exp, '.');
        return split[0] + "[" + index + "]." + split[1];
    }

    /**
     * 格式化条件，把其规范为值。左值是合法的变量名称，右值是具体的值。
     * 主要工作！如果右值是表达式，提取相关的值。
     *
     * @param conditions
     * @param variableContext
     * @return
     */
    public List<ConditionItem> formatForValue(List<ConditionItem> conditions, VariableContext variableContext) {
        for (ConditionItem conditionItem : conditions) {
            formatRuleItemsForValue(conditionItem.getRules(), variableContext);
        }
        return conditions;
    }

    public List<RuleItem> formatRuleItemsForValue(List<RuleItem> ruleItems, VariableContext variableContext) {
        for (RuleItem ruleItem : ruleItems) {
            formatRuleItemForValue(ruleItem, variableContext);
        }
        return ruleItems;
    }

    public RuleItem formatRuleItemForValue(RuleItem ruleItem, VariableContext variableContext) {
        Object value = convertValue(ruleItem.getOperatorType(), ruleItem.getValue(), variableContext);
        ruleItem.setValue(value);
        return ruleItem;
    }

    private Object convertValue(String operatorType, Object value, VariableContext variableContext) {
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(operatorType);
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            return value;
        } else if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            return variableContext.getVariableByExpression(value.toString());
        } else {
            return value;
        }
    }

}
