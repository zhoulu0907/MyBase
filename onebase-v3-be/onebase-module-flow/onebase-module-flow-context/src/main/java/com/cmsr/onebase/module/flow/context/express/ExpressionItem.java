package com.cmsr.onebase.module.flow.context.express;


import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.enums.OperatorTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/26 16:19
 */
@Data
public class ExpressionItem implements Serializable {

    public static ExpressionItem copy(ExpressionItem item) {
        ExpressionItem newItem = new ExpressionItem();
        newItem.setFieldKey(item.getFieldKey());
        newItem.setOp(item.getOp());
        newItem.setFieldValue(item.getFieldValue());
        newItem.setOperatorType(item.getOperatorType());
        newItem.setFieldTypeEnum(item.getFieldTypeEnum());
        return newItem;
    }

    private String fieldKey;

    private OpEnum op;

    private Object fieldValue;

    private OperatorTypeEnum operatorType;

    private SemanticFieldTypeEnum fieldTypeEnum;

}
