package com.cmsr.onebase.module.metadata.core.util;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.cmsr.onebase.module.metadata.core.enums.MetadataBooleanLiteralEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataTypeCodeEnum;
import com.cmsr.onebase.module.metadata.core.enums.OpEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;


import lombok.extern.slf4j.Slf4j;

/**
 * 字段值处理工具类
 * 用于处理字段值的类型转换、验证和格式化
 *
 * @author bty418
 * @date 2025-09-24
 */
@Slf4j
public class FieldValueUtil {

    /**
     * 处理条件值
     * 根据操作符和字段类型转换条件值
     *
     * @param operator 操作符
     * @param fieldValues 条件值列表
     * @param field 字段信息
     * @return 处理后的条件值
     */
    public static Object processConditionValue(String operator, List<String> fieldValues, MetadataEntityFieldDO field) {
        if (CollectionUtils.isEmpty(fieldValues)) {
            return null;
        }

        String firstValue = fieldValues.get(0);
        if (!StringUtils.hasText(firstValue)) {
            return null;
        }

        // 解析操作符为枚举
        OpEnum opEnum = OperatorUtil.parseOperator(operator);

        // 根据操作符类型处理条件值
        switch (opEnum) {
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case GREATER_EQUALS:
            case LESS_THAN:
            case LESS_EQUALS:
            case LATER_THAN:
            case EARLIER_THAN:
                // 单值比较操作符，需要进行类型转换
                return convertFieldValue(firstValue, field);

            case CONTAINS:
            case NOT_CONTAINS:
                // 模糊查询操作符，直接使用字符串
                return firstValue;

            case EXISTS_IN:
            case NOT_EXISTS_IN:
                // IN查询操作符，返回值列表
                return fieldValues.stream()
                        .filter(StringUtils::hasText)
                        .map(value -> convertFieldValue(value, field))
                        .collect(Collectors.toList());

            case IS_EMPTY:
            case IS_NOT_EMPTY:
                // 空值检查操作符，不需要条件值
                return null;

            case RANGE:
                // 范围查询操作符，需要两个值
                if (fieldValues.size() >= 2) {
                    Map<String, Object> rangeMap = new HashMap<>();
                    rangeMap.put("start", convertFieldValue(fieldValues.get(0), field));
                    rangeMap.put("end", convertFieldValue(fieldValues.get(1), field));
                    return rangeMap;
                }
                return null;

            case CONTAINS_ALL:
            case NOT_CONTAINS_ALL:
            case CONTAINS_ANY:
            case NOT_CONTAINS_ANY:
                // 数组包含操作符，返回值列表用于多值匹配
                return fieldValues.stream()
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());

            default:
                log.warn("不支持的操作符: {}", operator);
                return firstValue;
        }
    }

    /**
     * 转换字段值类型
     * 使用JdbcTypeConvertor进行业务实体字段到JDBC类型的转换
     *
     * @param value 原始值
     * @param field 字段信息
     * @return 转换后的值
     */
    public static Object convertFieldValue(String value, MetadataEntityFieldDO field) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        // 根据字段类型推断JDBC类型并进行转换
        String jdbcType = inferJdbcType(field.getFieldType());

        // 使用JdbcTypeConvertor进行类型转换
        return JdbcTypeConvertor.convert(jdbcType, value);
    }

    /**
     * 根据字段类型推断JDBC类型
     *
     * @param fieldType 字段类型
     * @return JDBC类型
     */
    public static String inferJdbcType(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return MetadataDataTypeCodeEnum.VARCHAR.getCode();
        }

        MetadataDataTypeCodeEnum typeCode = MetadataDataTypeCodeEnum.fromCode(fieldType);
        if (typeCode == null) {
            return MetadataDataTypeCodeEnum.VARCHAR.getCode();
        }

        switch (typeCode) {
            case STRING:
            case TEXT:
            case LONG_TEXT:
                return MetadataDataTypeCodeEnum.VARCHAR.getCode();
            case INTEGER:
            case INT:
                return MetadataDataTypeCodeEnum.BIGINT.getCode();
            case DECIMAL:
            case DOUBLE:
            case FLOAT:
            case NUMBER:
            case NUMERIC:
                return MetadataDataTypeCodeEnum.DECIMAL.getCode();
            case BOOLEAN:
            case BOOL:
                return MetadataDataTypeCodeEnum.BOOLEAN.getCode();
            case DATE:
                return MetadataDataTypeCodeEnum.DATE.getCode();
            case DATETIME:
            case TIMESTAMP:
                return MetadataDataTypeCodeEnum.TIMESTAMP.getCode();
            case ARRAY:
                return MetadataDataTypeCodeEnum.ARRAY.getCode();
            default:
                return MetadataDataTypeCodeEnum.VARCHAR.getCode();
        }
    }

    /**
     * 格式化字段值用于显示
     * 将Object类型的字段值转换为适合显示的格式
     *
     * @param fieldValue 字段值
     * @param fieldType 字段类型
     * @return 格式化后的值
     */
    public static Object formatFieldValue(Object fieldValue, String fieldType) {
        if (fieldValue == null) {
            return null;
        }

        // 对于Object类型，直接返回原值，让前端处理显示格式
        return fieldValue;
    }

    /**
     * 验证字段值的有效性
     *
     * @param fieldValue 字段值
     * @param field 字段信息
     * @return 是否有效
     */
    public static boolean validateFieldValue(Object fieldValue, MetadataEntityFieldDO field) {
        if (fieldValue == null) {
            return true; // null值总是有效的
        }

        String fieldType = field.getFieldType();
        if (!StringUtils.hasText(fieldType)) {
            return true; // 未知类型，默认有效
        }

        MetadataDataTypeCodeEnum typeCode = MetadataDataTypeCodeEnum.fromCode(fieldType);
        if (typeCode == null) {
            return true;
        }

        // 根据字段类型进行基本验证
        switch (typeCode) {
            case INTEGER:
            case INT:
                return isValidInteger(fieldValue);
            case DECIMAL:
            case DOUBLE:
            case FLOAT:
            case NUMBER:
            case NUMERIC:
                return isValidNumber(fieldValue);
            case BOOLEAN:
            case BOOL:
                return isValidBoolean(fieldValue);
            case DATE:
            case DATETIME:
            case TIMESTAMP:
                return isValidDate(fieldValue);
            default:
                return true; // 其他类型默认有效
        }
    }

    /**
     * 检查是否为有效的整数
     */
    private static boolean isValidInteger(Object value) {
        if (value instanceof Number) {
            return true;
        }
        if (value instanceof String) {
            try {
                Long.parseLong((String) value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 检查是否为有效的数字
     */
    private static boolean isValidNumber(Object value) {
        if (value instanceof Number) {
            return true;
        }
        if (value instanceof String) {
            try {
                Double.parseDouble((String) value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 检查是否为有效的布尔值
     */
    private static boolean isValidBoolean(Object value) {
        if (value instanceof Boolean) {
            return true;
        }
        if (value instanceof String) {
            return MetadataBooleanLiteralEnum.isBooleanLiteral((String) value);
        }
        return false;
    }

    /**
     * 检查是否为有效的日期
     */
    private static boolean isValidDate(Object value) {
        if (value instanceof java.time.LocalDate ||
            value instanceof java.time.LocalDateTime ||
            value instanceof java.util.Date ||
            value instanceof java.sql.Date ||
            value instanceof java.sql.Timestamp) {
            return true;
        }
        if (value instanceof String) {
            // 简单的日期格式检查，实际项目中可以用更严格的日期解析
            String str = (String) value;
            return str.matches("\\d{4}-\\d{2}-\\d{2}.*") ||
                   str.matches("\\d{4}/\\d{2}/\\d{2}.*");
        }
        return false;
    }
}
