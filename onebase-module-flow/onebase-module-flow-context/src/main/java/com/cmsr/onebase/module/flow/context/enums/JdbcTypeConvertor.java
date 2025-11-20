package com.cmsr.onebase.module.flow.context.enums;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @Author：huangjie
 * @Date：2025/9/8 17:24
 */
public class JdbcTypeConvertor {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER_NANO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    /**
     * 将数据库类型转换为对应的Java类型
     * 支持的数据库类型包括：BIGINT, BOOLEAN, DATE, DECIMAL, LONGVARCHAR, NUMERIC, TIMESTAMP, VARCHAR
     *
     * <p>转换规则示例：</p>
     * <ul>
     *     <li>输入 jdbcType="TIMESTAMP", value="2025-09-08 17:24:00" → 输出 java.time.LocalDateTime</li>
     *     <li>输入 jdbcType="VARCHAR", value="abc" → 输出 java.lang.String</li>
     *     <li>输入 jdbcType="BIGINT", value="1234567890" → 输出 java.lang.Long</li>
     *     <li>输入 jdbcType="BOOLEAN", value="true" → 输出 java.lang.Boolean</li>
     *     <li>输入 jdbcType="DATE", value="2025-09-08" → 输出 java.time.LocalDate</li>
     *     <li>输入 jdbcType="DECIMAL", value="123.45" → 输出 java.math.BigDecimal</li>
     *     <li>输入 jdbcType="LONGVARCHAR", value="long string" → 输出 java.lang.String</li>
     *     <li>输入 jdbcType="NUMERIC", value="123.45" → 输出 java.math.BigDecimal</li>
     * </ul>
     *
     * @param jdbcType 数据库类型字符串，如"TIMESTAMP"、"VARCHAR"等
     * @param value    待转换的值，通常为字符串形式的数据库值
     * @return 转换后的Java对象，类型根据jdbcType和value内容确定
     * @throws UnsupportedOperationException 当不支持的数据库类型传入时抛出
     */
    public static Object convert(String jdbcType, Object value) {
        if (value == null) {
            return null;
        }

        if (jdbcType == null) {
            throw new IllegalArgumentException("jdbcType 不能为空");
        }

        switch (jdbcType.toUpperCase()) {
            case "TIMESTAMP":
                return convertTimestamp(value);
            case "VARCHAR":
                return convertVarchar(value);
            case "BIGINT":
                return convertBigInt(value);
            case "BOOLEAN":
                return convertBoolean(value);
            case "DATE":
                return convertDate(value);
            case "DECIMAL":
                return convertDecimal(value);
            case "LONGVARCHAR":
                return convertLongVarchar(value);
            case "NUMERIC":
                return convertNumeric(value);
            default:
                return value;
        }
    }

    private static Object convertTimestamp(Object value) {
        if (value instanceof LocalDateTime) {
            return value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
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
        } catch (DateTimeParseException e) {
        }
        try {
            // 尝试解析为带有毫秒的格式
            return LocalDateTime.parse(stringValue, TIMESTAMP_FORMATTER_NANO);
        } catch (DateTimeParseException e) {
        }
        try {
            Instant instant = Instant.parse(stringValue);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException e) {

        }
        throw new IllegalArgumentException("无效的TIMESTAMP格式: " + stringValue);
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
        if (value instanceof java.sql.Date v) {
            return v.toLocalDate();
        }
        if (value instanceof java.util.Date v) {
            return LocalDate.ofInstant(v.toInstant(), ZoneId.systemDefault());
        }
        if (value instanceof LocalDateTime v) {
            return v.toLocalDate();
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
}
