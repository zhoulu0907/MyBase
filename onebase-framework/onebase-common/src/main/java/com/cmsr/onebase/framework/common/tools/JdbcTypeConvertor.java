package com.cmsr.onebase.framework.common.tools;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

/**
 * @Author：huangjie
 * @Date：2025/9/8 17:24
 */
public class JdbcTypeConvertor {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 将数据库类型转换为对应的Java类型
     * 支持的数据库类型包括：ARRAY, BIGINT, BOOLEAN, DATE, DECIMAL, LONGVARCHAR, NUMERIC, TIMESTAMP, VARCHAR
     *
     * <p>转换规则示例：</p>
     * <ul>
     *     <li>输入 jdbcType=Types.TIMESTAMP, value="2025-09-08 17:24:00" → 输出 java.time.LocalDateTime</li>
     *     <li>输入 jdbcType=Types.VARCHAR, value="abc" → 输出 java.lang.String</li>
     *     <li>输入 jdbcType=Types.BIGINT, value="1234567890" → 输出 java.lang.Long</li>
     *     <li>输入 jdbcType=Types.BOOLEAN, value="true" → 输出 java.lang.Boolean</li>
     *     <li>输入 jdbcType=Types.DATE, value="2025-09-08" → 输出 java.time.LocalDate</li>
     *     <li>输入 jdbcType=Types.DECIMAL, value="123.45" → 输出 java.math.BigDecimal</li>
     *     <li>输入 jdbcType=Types.LONGVARCHAR, value="long string" → 输出 java.lang.String</li>
     *     <li>输入 jdbcType=Types.NUMERIC, value="123.45" → 输出 java.math.BigDecimal</li>
     *     <li>输入 jdbcType=Types.ARRAY, value=[1,2,3] → 输出 java.sql.Array 或转换后的Java数组</li>
     * </ul>
     *
     * @param jdbcType 数据库类型常量，对应java.sql.Types中的常量值
     * @param value    待转换的值，通常为字符串形式的数据库值
     * @return 转换后的Java对象，类型根据jdbcType和value内容确定
     * @throws UnsupportedOperationException 当不支持的数据库类型传入时抛出
     */
    public static Object convert(int jdbcType, Object value) {
        if (value == null) {
            return null;
        }

        switch (jdbcType) {
            case Types.TIMESTAMP:
                return convertTimestamp(value);
            case Types.VARCHAR:
                return convertVarchar(value);
            case Types.BIGINT:
                return convertBigInt(value);
            case Types.BOOLEAN:
                return convertBoolean(value);
            case Types.DATE:
                return convertDate(value);
            case Types.DECIMAL:
                return convertDecimal(value);
            case Types.LONGVARCHAR:
                return convertLongVarchar(value);
            case Types.NUMERIC:
                return convertNumeric(value);
            case Types.ARRAY:
                return convertArray(value);
            default:
                throw new UnsupportedOperationException("不支持的数据库类型: " + jdbcType);
        }
    }

    private static Object convertTimestamp(Object value) {
        if (value instanceof LocalDateTime) {
            return value;
        }
        String stringValue = value.toString().trim();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        if (StringUtils.isNumeric(stringValue)) {
            long timestamp = Long.parseLong(stringValue);
            // Unix时间戳转LocalDateTime
            return Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        try {
            // 尝试解析为标准时间格式
            return LocalDateTime.parse(stringValue, TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e1) {
            throw new IllegalArgumentException("无效的TIMESTAMP格式: " + stringValue, e1);
        }
    }


    /**
     * 转换VARCHAR类型的值
     * 根据注释要求，直接返回字符串类型，不进行其他类型的转换
     *
     * @param value 待转换的值
     * @return 转换后的字符串
     */
    private static Object convertVarchar(Object value) {
        return value.toString();
    }

    private static Object convertBigInt(Object value) {
        if (value instanceof Long) {
            return value;
        }
        return Long.parseLong(value.toString());
    }

    private static Object convertBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return value;
        }
        String stringValue = value.toString().trim().toLowerCase();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return BooleanUtils.toBooleanObject(stringValue);
    }

    private static Object convertDate(Object value) {
        if (value instanceof LocalDate) {
            return value;
        }
        String stringValue = value.toString();
        try {
            return LocalDate.parse(stringValue, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("无效的DATE格式: " + stringValue, e);
        }
    }

    private static Object convertDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return value;
        }
        return new BigDecimal(value.toString());
    }

    private static Object convertLongVarchar(Object value) {
        return value.toString();
    }

    private static Object convertNumeric(Object value) {
        // NUMERIC和DECIMAL处理方式相同
        return convertDecimal(value);
    }

    private static Object convertArray(Object value) {
        if (value instanceof Array) {
            try {
                return ((Array) value).getArray();
            } catch (SQLException e) {
                throw new IllegalArgumentException("无法转换ARRAY类型", e);
            }
        } else if (value instanceof String) {
            // 处理字符串形式的数组，如"[1,2,3]"
            String stringValue = (String) value;
            // 移除首尾的括号
            stringValue = stringValue.replaceAll("^\\[|\\]$", "");
            // 分割元素
            String[] elements = stringValue.split(",");
            // 尝试转换为整数数组
            try {
                int[] intArray = Arrays.stream(elements)
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .toArray();
                return intArray;
            } catch (NumberFormatException e) {
                // 如果转换整数失败，返回字符串数组
                return Arrays.stream(elements)
                        .map(String::trim)
                        .toArray(String[]::new);
            }
        }
        throw new IllegalArgumentException("无效的ARRAY类型: " + value.getClass().getName());
    }
}
