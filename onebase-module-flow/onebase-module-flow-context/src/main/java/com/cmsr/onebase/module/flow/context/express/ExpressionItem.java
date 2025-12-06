package com.cmsr.onebase.module.flow.context.express;


import com.cmsr.onebase.module.flow.context.enums.FieldTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
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
        newItem.setKey(item.getKey());
        newItem.setOp(item.getOp());
        newItem.setValue(item.getValue());
        newItem.setOperatorType(item.getOperatorType());
        newItem.setFieldType(item.getFieldType());
//        newItem.setJdbcType(item.getJdbcType());
        return newItem;
    }


    private String key;

    private OpEnum op;

    private Object value;

    private OperatorTypeEnum operatorType;

    private SemanticFieldTypeEnum fieldType;

//    private JdbcTypeEnum jdbcType;
}
