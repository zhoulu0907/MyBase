package com.cmsr.onebase.module.bpm.core.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 操作符枚举
 *
 * @Author：huangjie
 * @Date：2025/9/16 21:29
 */
public enum OpEnum {

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
     * 包含
     *
     * @Description: 包含操作符
     * SQL 中的 "LIKE" 操作符
     */
    CONTAINS("包含"),

    /**
     * 不包含
     *
     * @Description: 不包含操作符
     * SQL 中的 "NOT LIKE" 操作符
     */
    NOT_CONTAINS("不包含"),

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
     * 晚于
     *
     * @Description: 晚于操作符
     * 通常用于日期比较，表示某个日期在另一个日期之后，类似于 ">"
     */
    LATER_THAN("晚于"),

    /**
     * 早于
     *
     * @Description: 早于操作符
     * 通常用于日期比较，表示某个日期在另一个日期之前，类似于 "<"
     */
    EARLIER_THAN("早于"),

    /**
     * 范围
     *
     * @Description: 范围操作符
     * 通常用于表示某个值在两个边界值之间，类似于 SQL 中的 "BETWEEN ... AND ..."
     */
    RANGE("范围"),

    /**
     * 为空
     *
     * @Description: 为空操作符
     * 用于判断某个字段是否为空，类似于 SQL 中的 "IS NULL"或者 = ''
     */
    IS_EMPTY("为空"),

    /**
     * 不为空
     *
     * @Description: 不为空操作符
     * 用于判断某个字段是否不为空，类似于 SQL 中的 "IS NOT NULL"或者 <> ''
     */
    IS_NOT_EMPTY("不为空"),

    /**
     * 包含全部
     *
     * @Description: 包含全部操作符
     * 用于判断某个字段是否包含全部指定的值，比如字段值是 [1,2,3,4,5]，判断条件是 [1,2,3]，则返回 true，用 like 来实现
     */
    CONTAINS_ALL("包含全部"),

    /**
     * 不包含全部
     *
     * @Description: 不包含全部操作符
     * 用于判断某个字段是否不包含全部指定的值，比如字段值是 [1,2,3]，判断条件是 [1,2,3,4]，则返回 true，用 not like 来实现
     */
    NOT_CONTAINS_ALL("不包含全部"),

    /**
     * 包含任一
     *
     * @Description: 包含任一操作符
     * 用于判断某个字段是否包含任意一个指定的值，比如字段值是 [1,2,3]，判断条件是 [3,4,5]， 则返回 true，用 like 来实现
     */
    CONTAINS_ANY("包含任一"),

    /**
     * 不包含任一
     *
     * @Description: 不包含任一操作符
     * 用于判断某个字段是否不包含任意一个指定的值，比如字段值是 [1,2,3]，判断条件是 [3,4,5]， 则返回 true，用 not like 来实现
     */
    NOT_CONTAINS_ANY("不包含任一");

    private final String description;

    OpEnum(String description) {
        this.description = description;
    }

    public static OpEnum getByName(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (OpEnum value : OpEnum.values()) {
            if (value.name().equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid OpEnum code: " + code);
    }
}
