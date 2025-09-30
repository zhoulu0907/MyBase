package com.cmsr.onebase.module.flow.context.express;

import com.cmsr.onebase.framework.common.express.FieldTypeEnum;
import com.cmsr.onebase.framework.common.express.JdbcTypeEnum;
import com.cmsr.onebase.framework.common.express.OpEnum;
import com.cmsr.onebase.framework.common.express.OperatorTypeEnum;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/26 16:19
 */
@Data
public class ExpressItem {

    public static ExpressItem copy(ExpressItem item) {
        ExpressItem newItem = new ExpressItem();
        newItem.setKey(item.getKey());
        newItem.setOp(item.getOp());
        newItem.setValue(item.getValue());
        newItem.setOperatorType(item.getOperatorType());
        newItem.setFieldType(item.getFieldType());
        newItem.setJdbcType(item.getJdbcType());
        return newItem;
    }


    private Object key;

    private OpEnum op;

    private Object value;

    private OperatorTypeEnum operatorType;

    private FieldTypeEnum fieldType;

    private JdbcTypeEnum jdbcType;
}
