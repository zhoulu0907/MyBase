package com.cmsr.onebase.module.flow.context.condition;

import com.cmsr.onebase.module.flow.context.enums.FieldTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.enums.OperatorTypeEnum;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * 脚本节点 : fieldId 为 name, fieldType 为 type
 *
 * @Author：huangjie
 * @Date：2025/9/16 21:08
 */
@Data
public class ConditionItem implements java.io.Serializable {

    @JsonAlias(value = {"field", "name", "fieldUuid"})
    private String fieldUuid;

    private String fieldName;

    /**
     * 操作符
     * {@link OpEnum}
     */
    private String op;

    /**
     * 变量值来源计算方式，固定值，变量，公式等
     * {@link OperatorTypeEnum}
     */
    private String operatorType;

    /**
     * 根据字段id，查询元数据信息，补充的信息。
     * 字段类型
     * {@link JdbcTypeEnum}
     */
    private String jdbcType;

    /**
     * 根据字段id，查询元数据信息，补充的信息。
     * 字段类型
     * {@link FieldTypeEnum}
     */
    @JsonAlias(value = {"fieldType", "type"})
    private String fieldType;

    /**
     * 如果是operatorType是值，value可能是字符串，也可能是数组，也可能是Map。
     * 如果是operatorType是变量，value是变字符串
     */
    private Object value;

}
