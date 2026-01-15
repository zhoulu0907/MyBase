package com.cmsr.onebase.module.formula.dal.dataflexdo;

import com.mybatisflex.annotation.Table;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Table(value = FormulaDO.TABLE_NAME)
public class FormulaDO {

    public static final String TABLE_NAME = "formula_function";

    // 字段名常量
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EXPRESSION = "expression";
    public static final String FIELD_SUMMARY = "summary";
    public static final String FIELD_USAGE = "usage";
    public static final String FIELD_EXAMPLE = "example";
    public static final String FIELD_RETURN_TYPE = "return_type";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_STATUS = "status";

    /**
     * 函数类型
     * @see com.cmsr.onebase.framework.common.enums.FunctionTypeEnum
     */
    @Column(name = FIELD_TYPE, comment = "函数类型")
    private String type;

    /**
     * 函数名称
     */
    @Column(name = FIELD_NAME, comment = "函数名称")
    private String name;

    /**
     * 函数表达式
     */
    @Column(name = FIELD_EXPRESSION, comment = "函数表达式")
    private String expression;

    /**
     * 函数简介
     */
    @Column(name = FIELD_SUMMARY, comment = "函数简介")
    private String summary;

    /**
     * 函数用法（md格式）
     */
    @Column(name = FIELD_USAGE, comment = "函数用法")
    private String usage;

    /**
     * 函数示例（md格式）
     */
    @Column(name = FIELD_EXAMPLE, comment = "函数示例")
    private String example;

    /**
     * 返回值类型（枚举Code）
     * @see com.cmsr.onebase.framework.common.enums.ReturnTypeEnum
     */
    @Column(name = FIELD_RETURN_TYPE, comment = "返回值类型")
    private String returnType;

    /**
     * 函数版本
     */
    @Column(name = FIELD_VERSION, comment = "函数版本")
    private String version;

    /**
     * 函数状态
     */
    @Column(name = FIELD_STATUS, comment = "函数状态")
    private Integer status;
}
