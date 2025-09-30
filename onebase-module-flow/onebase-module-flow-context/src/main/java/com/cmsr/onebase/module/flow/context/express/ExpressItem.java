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

    private Object key;

    private OpEnum op;

    private Object value;

    private OperatorTypeEnum operatorType;

    private FieldTypeEnum fieldType;

    private JdbcTypeEnum jdbcType;
}
