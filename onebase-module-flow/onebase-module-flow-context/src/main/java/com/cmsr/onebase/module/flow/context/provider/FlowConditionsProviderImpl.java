package com.cmsr.onebase.module.flow.context.provider;


import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.enums.FieldTypeConvertor;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.enums.OperatorTypeEnum;
import com.cmsr.onebase.module.flow.context.express.AndExpression;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.formula.api.formula.FormulaEngineApi;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Author：huangjie
 * @Date：2025/9/28 9:17
 */
@Setter
@Component
public class FlowConditionsProviderImpl implements FlowConditionsProvider {


    @Autowired
    private FormulaEngineApi formulaEngineApi;

    /**
     * 格式化条件，把其规范为表达式能执行的。左值是合法的变量名称，右值是具体的值或者表达式。
     * <p>
     * 注意：
     * 右值是变量，保留表达式变量。
     * 右值是函数，计算出相关的值。
     *
     * @return
     */
    @Override
    public OrExpression formatConditionsForExpression(List<Conditions> conditions, Map<String, Object> vars) {
        OrExpression orExpression = ConditionsSupport.convertToOrExpresses(conditions);
        if (orExpression == null) {
            return null;
        }
        for (AndExpression andExpression : ListUtils.emptyIfNull(orExpression.getAndExpressions())) {
            for (ExpressionItem expressionItem : ListUtils.emptyIfNull(andExpression.getExpressionItems())) {
                formatRuleItemForExpression(expressionItem, vars);
            }
        }
        return orExpression;
    }

    /**
     * 格式化条件，把其规范为值。左值是合法的变量名称，右值是具体的值。
     * 主要工作！如果右值是表达式，提取相关的值。
     *
     * @return
     */
    @Override
    public OrExpression formatConditionsForValue(List<Conditions> conditions, Map<String, Object> vars) {
        OrExpression orExpression = ConditionsSupport.convertToOrExpresses(conditions);
        for (AndExpression andExpression : orExpression.getAndExpressions()) {
            for (ExpressionItem expressionItem : andExpression.getExpressionItems()) {
                formatRuleItemForValue(expressionItem, vars);
            }
        }
        return orExpression;
    }

    @Override
    public List<ExpressionItem> formatConditionItemsForValue(List<ConditionItem> conditionItems, Map<String, Object> vars) {
        List<ExpressionItem> expressionItems = new ArrayList<>();
        for (ConditionItem conditionItem : conditionItems) {
            ExpressionItem expressionItem = ConditionsSupport.convertToExpressesItem(conditionItem);
            formatRuleItemForValue(expressionItem, vars);
            expressionItems.add(expressionItem);
        }
        return expressionItems;
    }

    private void formatRuleItemForExpression(ExpressionItem expressionItem, Map<String, Object> vars) {
        if (expressionItem.getOperatorType() == OperatorTypeEnum.VALUE) {
            formatExpressionItemValue(expressionItem);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            Object value = expressionItem.getFieldValue().toString();
            expressionItem.setFieldValue(value);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.FORMULA) {
            Map valueMap = (Map) expressionItem.getFieldValue();
            String formula = MapUtils.getString(valueMap, "formula");
            Map parameters = MapUtils.getMap(valueMap, "parameters");
            FormulaExecuteReqDTO reqDTO = new FormulaExecuteReqDTO();
            reqDTO.setFormula(formula);
            reqDTO.setParameters(parameters);
            reqDTO.setContextData(vars);
            expressionItem.setFieldValue(callFormula(reqDTO, expressionItem.getFieldTypeEnum()));
        }
    }


    private Object callFormula(FormulaExecuteReqDTO reqDTO, SemanticFieldTypeEnum fieldType) {
        CommonResult<FormulaExecuteRespDTO> respDTO = formulaEngineApi.executeFormula(reqDTO);
        if (respDTO.getData() == null) {
            throw new IllegalCallerException("调用公式错误: " + reqDTO.getFormula() + ", 错误信息: " + respDTO.getMsg());
        }
        Object result = respDTO.getData().getResult();
        if (fieldType != null) {
            return FieldTypeConvertor.convert(fieldType, result);
        } else {
            return result;
        }
    }

    private void formatRuleItemForValue(ExpressionItem expressionItem, Map<String, Object> vars) {
        // 转换值
        if (expressionItem.getOperatorType() == OperatorTypeEnum.VALUE) {
            formatExpressionItemValue(expressionItem);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            String exp = expressionItem.getFieldValue().toString();
            Object value = getVariableByExpression(exp, vars);
            expressionItem.setFieldValue(value);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.FORMULA) {
            Map valueMap = (Map) expressionItem.getFieldValue();
            String formula = MapUtils.getString(valueMap, "formula");
            Map parameters = MapUtils.getMap(valueMap, "parameters");
            FormulaExecuteReqDTO reqDTO = new FormulaExecuteReqDTO();
            reqDTO.setFormula(formula);
            reqDTO.setParameters(parameters);
            reqDTO.setContextData(vars);
            expressionItem.setFieldValue(callFormula(reqDTO, expressionItem.getFieldTypeEnum()));
        }
    }

    private Object getVariableByExpression(String exp, Map<String, Object> vars) {
        if (exp.contains(".")) {
            String key1 = StringUtils.substringBefore(exp, ".");
            String key2 = StringUtils.substringAfter(exp, ".");
            Object value = vars.get(key1);
            if (value == null) {
                return null;
            }
            if (value instanceof Map map) {
                return map.get(key2);
            }
            throw new IllegalArgumentException("变量错误: " + exp);
        } else {
            return vars.get(exp);
        }
    }

    @Override
    public ExpressionItem formatConditionItemForValue(ConditionItem conditionItem, Map<String, Object> dataMap) {
        ExpressionItem expressionItem = ConditionsSupport.convertToExpressesItem(conditionItem);
        if (expressionItem.getOperatorType() == OperatorTypeEnum.VALUE) {
            formatExpressionItemValue(expressionItem);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.VARIABLE) {
            String exp = conditionItem.getValue().toString();
            Object value = getVariableByExpression(exp, dataMap);
            expressionItem.setFieldValue(value);
        } else if (expressionItem.getOperatorType() == OperatorTypeEnum.FORMULA) {
            Map valueMap = (Map) expressionItem.getFieldValue();
            String formula = MapUtils.getString(valueMap, "formula");
            Map parameters = MapUtils.getMap(valueMap, "parameters");
            FormulaExecuteReqDTO reqDTO = new FormulaExecuteReqDTO();
            reqDTO.setFormula(formula);
            reqDTO.setParameters(parameters);
            reqDTO.setContextData(dataMap);
            expressionItem.setFieldValue(callFormula(reqDTO, conditionItem.getFieldTypeEnum()));
        }
        return expressionItem;
    }

    private void formatExpressionItemValue(ExpressionItem expressionItem) {
        if (expressionItem.getOp() == OpEnum.RANGE
                && (expressionItem.getFieldTypeEnum() == SemanticFieldTypeEnum.DATE || expressionItem.getFieldTypeEnum() == SemanticFieldTypeEnum.DATETIME)
                && expressionItem.getFieldValue() instanceof Map) {
            String begin = MapUtils.getString((Map) expressionItem.getFieldValue(), "begin");
            String end = MapUtils.getString((Map) expressionItem.getFieldValue(), "end");
            expressionItem.setFieldValue(List.of(begin, end));
        }
    }

}
