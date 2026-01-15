package com.cmsr.onebase.module.flow.context.enums;

import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
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
public class FieldTypeConvertor {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER_NANO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    /**
     *
     */
    public static Object convert(SemanticFieldTypeEnum fieldType, Object value) {
        if (value == null) {
            return null;
        }
        if (fieldType == null) {
            return value;
        }

        Class<?> rawJavaType = fieldType.getRawJavaType();

        if (rawJavaType == String.class) {
            return convertVarchar(value);
        } else if (rawJavaType == Long.class) {
            return convertBigInt(value);
        } else if (rawJavaType == Boolean.class) {
            return convertBoolean(value);
        } else if (rawJavaType == LocalDate.class) {
            return convertDate(value);
        } else if (rawJavaType == LocalDateTime.class) {
            return convertTimestamp(value);
        } else if (rawJavaType == BigDecimal.class) {
            return convertDecimal(value);
        }
        return value;
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
        String stringValue = value.toString().trim().toLowerCase();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return Long.parseLong(stringValue);
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
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
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
        String stringValue = value.toString();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return new BigDecimal(stringValue);
    }


}
