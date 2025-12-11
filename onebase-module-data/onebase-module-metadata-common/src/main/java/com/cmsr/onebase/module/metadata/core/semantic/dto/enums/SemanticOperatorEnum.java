package com.cmsr.onebase.module.metadata.core.semantic.dto.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 操作符枚举
 *
 * 与流程上下文中的操作符定义保持一致，涵盖字符串、数值、集合与日期等比较语义。
 */
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
    IS_NOT_EMPTY("不为空"),

    /**
     * 晚于
     *
     * 通常用于日期比较，表示某个日期在另一个日期之后，类似于 ">"
     */
    LATER_THAN("晚于"),

    /**
     * 早于
     *
     * 通常用于日期比较，表示某个日期在另一个日期之前，类似于 "<"
     */
    EARLIER_THAN("早于"),

    /**
     * 范围
     *
     * 表示某个值在两个边界值之间，类似于 SQL 中的 "BETWEEN ... AND ..."
     */
    RANGE("范围"),

    /**
     * 包含全部
     *
     * 判断字段是否包含全部指定的值（如集合包含性），可用 like 组合实现
     */
    CONTAINS_ALL("包含全部"),

    /**
     * 不包含全部
     *
     * 判断字段是否不包含全部指定的值，可用 not like 组合实现
     */
    NOT_CONTAINS_ALL("不包含全部"),

    /**
     * 包含任一
     *
     * 判断字段是否包含任意一个指定的值，可用 like 组合实现
     */
    CONTAINS_ANY("包含任一"),

    /**
     * 不包含任一
     *
     * 判断字段是否不包含任意一个指定的值，可用 not like 组合实现
     */
    NOT_CONTAINS_ANY("不包含任一");

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
