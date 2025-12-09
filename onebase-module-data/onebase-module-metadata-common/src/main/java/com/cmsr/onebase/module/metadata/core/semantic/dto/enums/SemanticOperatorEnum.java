package com.cmsr.onebase.module.metadata.core.semantic.dto.enums;

import org.apache.commons.lang3.StringUtils;

public enum SemanticOperatorEnum {

    /**
     * 等于
     *
     * @Description: 等于操作符
     * SQL 中的 "=" 操作符
     */
    EQUALS("等于"),

    /**
     * 不等于
     *
     * @Description: 不等于操作符
     * SQL 中的 "!=" 或 "<>" 操作符
     */
    NOT_EQUALS("不等于"),

    /**
     * 大于
     *
     * @Description: 大于操作符
     * SQL 中的 ">" 操作符
     */
    GREATER_THAN("大于"),

    /**
     * 大于等于
     *
     * @Description: 大于等于操作符
     * SQL 中的 ">=" 操作符
     */
    GREATER_EQUALS("大于等于"),

    /**
     * 小于
     *
     * @Description: 小于操作符
     * SQL 中的 "<" 操作符
     */
    LESS_THAN("小于"),

    /**
     * 小于等于
     *
     * @Description: 小于等于操作符
     * SQL 中的 "<=" 操作符
     */
    LESS_EQUALS("小于等于"),

    /**
     * 包含/相似
     *
     * @Description: 包含/模糊匹配操作符
     * SQL 中的 "LIKE" 操作符
     */
    CONTAINS("包含"),

    /**
     * 存在于
     *
     * @Description: 存在于操作符
     * SQL 中的 "IN" 操作符
     */
    EXISTS_IN("存在于"),

    /**
     * 不存在于
     *
     * @Description: 不存在于操作符
     * SQL 中的 "NOT IN" 操作符
     */
    NOT_EXISTS_IN("不存在于"),

    /**
     * 不包含/不相似
     *
     * @Description: 不包含/模糊不匹配操作符
     * SQL 中的 "NOT LIKE" 操作符
     */
    NOT_CONTAINS("不包含"),

    /**
     * 为空
     *
     * @Description: 为空操作符
     * 用于判断某个字段是否为空，类似于 SQL 中的 "IS NULL" 或者 = ''
     */
    IS_EMPTY("为空"),

    /**
     * 不为空
     *
     * @Description: 不为空操作符
     * 用于判断某个字段是否不为空，类似于 SQL 中的 "IS NOT NULL" 或者 <> ''
     */
    IS_NOT_EMPTY("不为空");

    private final String description;

    SemanticOperatorEnum(String description) {
        this.description = description;
    }

    public static SemanticOperatorEnum getByName(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (SemanticOperatorEnum value : SemanticOperatorEnum.values()) {
            if (value.name().equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid SemanticOperatorEnum code: " + code);
    }
}
