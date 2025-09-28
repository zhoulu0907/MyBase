package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.framework.common.express.OperatorTypeEnum;
import com.cmsr.onebase.module.flow.context.VariableContext;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/28 9:17
 */
@Component
public class ValueProvider {

    public Object convertValue(String operatorType, Object value, VariableContext variableContext) {
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(operatorType);
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            return value;
        }
        if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            return variableContext.getVariableByExpression(value.toString());
        }
        return value;
    }

    public Object convertValue(int i, String operatorType, Object value, VariableContext variableContext) {
        OperatorTypeEnum operatorTypeEnum = OperatorTypeEnum.getByCode(operatorType);
        if (operatorTypeEnum == OperatorTypeEnum.VALUE) {
            return value;
        }
        if (operatorTypeEnum == OperatorTypeEnum.VARIABLE) {
            String expression = value.toString();
            return variableContext.getVariableByExpression(i, expression);
        }
        return value;
    }

}
