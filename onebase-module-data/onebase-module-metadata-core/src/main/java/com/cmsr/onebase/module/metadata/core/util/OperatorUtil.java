package com.cmsr.onebase.module.metadata.core.util;

import org.springframework.util.StringUtils;

import com.cmsr.onebase.module.metadata.core.enums.OpEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 操作符工具类
 * 用于操作符的解析和处理
 *
 * @author bty418
 * @date 2025-09-24
 */
@Slf4j
public class OperatorUtil {

    /**
     * 解析操作符字符串为枚举
     * 支持多种格式的操作符输入，包括枚举名称、中文描述、符号等
     *
     * @param operator 操作符字符串
     * @return 操作符枚举
     */
    public static OpEnum parseOperator(String operator) {
        if (!StringUtils.hasText(operator)) {
            return OpEnum.EQUALS; // 默认等于
        }

        String op = operator.toLowerCase().trim();

        // 直接匹配枚举名称
        try {
            return OpEnum.valueOf(operator.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 继续尝试其他匹配方式
        }

        // 匹配常见的操作符符号和别名
        switch (op) {
            // 等于
            case "equals":
            case "=":
            case "==":
            case "等于":
                return OpEnum.EQUALS;

            // 不等于
            case "not_equals":
            case "!=":
            case "<>":
            case "不等于":
                return OpEnum.NOT_EQUALS;

            // 包含
            case "contains":
            case "like":
            case "包含":
                return OpEnum.CONTAINS;

            // 不包含
            case "not_contains":
            case "not_like":
            case "不包含":
                return OpEnum.NOT_CONTAINS;

            // 存在于(IN)
            case "exists_in":
            case "in":
            case "存在于":
                return OpEnum.EXISTS_IN;

            // 不存在于(NOT IN)
            case "not_exists_in":
            case "not_in":
            case "不存在于":
                return OpEnum.NOT_EXISTS_IN;

            // 大于
            case "greater_than":
            case ">":
            case "大于":
                return OpEnum.GREATER_THAN;

            // 大于等于
            case "greater_equals":
            case ">=":
            case "大于等于":
                return OpEnum.GREATER_EQUALS;

            // 小于
            case "less_than":
            case "<":
            case "小于":
                return OpEnum.LESS_THAN;

            // 小于等于
            case "less_equals":
            case "<=":
            case "小于等于":
                return OpEnum.LESS_EQUALS;

            // 晚于(日期比较)
            case "later_than":
            case "晚于":
                return OpEnum.LATER_THAN;

            // 早于(日期比较)
            case "earlier_than":
            case "早于":
                return OpEnum.EARLIER_THAN;

            // 范围
            case "range":
            case "between":
            case "范围":
                return OpEnum.RANGE;

            // 为空
            case "is_empty":
            case "null":
            case "is_null":
            case "为空":
                return OpEnum.IS_EMPTY;

            // 不为空
            case "is_not_empty":
            case "not_null":
            case "is_not_null":
            case "不为空":
                return OpEnum.IS_NOT_EMPTY;

            // 包含全部
            case "contains_all":
            case "包含全部":
                return OpEnum.CONTAINS_ALL;

            // 不包含全部
            case "not_contains_all":
            case "不包含全部":
                return OpEnum.NOT_CONTAINS_ALL;

            // 包含任一
            case "contains_any":
            case "包含任一":
                return OpEnum.CONTAINS_ANY;

            // 不包含任一
            case "not_contains_any":
            case "不包含任一":
                return OpEnum.NOT_CONTAINS_ANY;

            default:
                log.warn("未知的操作符: {}, 使用默认EQUALS", operator);
                return OpEnum.EQUALS;
        }
    }

    /**
     * 检查操作符是否需要值
     *
     * @param opEnum 操作符枚举
     * @return 是否需要值
     */
    public static boolean isValueRequired(OpEnum opEnum) {
        switch (opEnum) {
            case IS_EMPTY:
            case IS_NOT_EMPTY:
                return false; // 这些操作符不需要值
            default:
                return true; // 其他操作符都需要值
        }
    }

    /**
     * 检查操作符是否支持多个值
     *
     * @param opEnum 操作符枚举
     * @return 是否支持多个值
     */
    public static boolean isMultiValueSupported(OpEnum opEnum) {
        switch (opEnum) {
            case EXISTS_IN:
            case NOT_EXISTS_IN:
            case CONTAINS_ALL:
            case NOT_CONTAINS_ALL:
            case CONTAINS_ANY:
            case NOT_CONTAINS_ANY:
            case RANGE:
                return true; // 这些操作符支持多个值
            default:
                return false; // 其他操作符只支持单个值
        }
    }

    /**
     * 检查操作符是否为比较操作符
     *
     * @param opEnum 操作符枚举
     * @return 是否为比较操作符
     */
    public static boolean isComparisonOperator(OpEnum opEnum) {
        switch (opEnum) {
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case GREATER_EQUALS:
            case LESS_THAN:
            case LESS_EQUALS:
            case LATER_THAN:
            case EARLIER_THAN:
                return true;
            default:
                return false;
        }
    }

    /**
     * 检查操作符是否为文本操作符
     *
     * @param opEnum 操作符枚举
     * @return 是否为文本操作符
     */
    public static boolean isTextOperator(OpEnum opEnum) {
        switch (opEnum) {
            case CONTAINS:
            case NOT_CONTAINS:
            case CONTAINS_ALL:
            case NOT_CONTAINS_ALL:
            case CONTAINS_ANY:
            case NOT_CONTAINS_ANY:
                return true;
            default:
                return false;
        }
    }

    /**
     * 检查操作符是否为集合操作符
     *
     * @param opEnum 操作符枚举
     * @return 是否为集合操作符
     */
    public static boolean isSetOperator(OpEnum opEnum) {
        switch (opEnum) {
            case EXISTS_IN:
            case NOT_EXISTS_IN:
                return true;
            default:
                return false;
        }
    }

    /**
     * 检查操作符是否为空值操作符
     *
     * @param opEnum 操作符枚举
     * @return 是否为空值操作符
     */
    public static boolean isNullOperator(OpEnum opEnum) {
        switch (opEnum) {
            case IS_EMPTY:
            case IS_NOT_EMPTY:
                return true;
            default:
                return false;
        }
    }
}
