package com.cmsr.onebase.module.metadata.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 校验类型枚举
 *
 * @author bty418
 * @date 2025-01-25
 */
@Getter
public enum ValidationTypeEnum {

    REQUIRED_VALIDATION("REQUIRED_VALIDATION", "必填验证", "验证字段是否为空",
                        Arrays.asList("IS_NOT_NULL", "IS_NOT_EMPTY")),

    UNIQUE_VALIDATION("UNIQUE_VALIDATION", "唯一验证", "验证字段值的唯一性",
                      Arrays.asList("NOT_EXISTS")),

    LENGTH_VALIDATION("LENGTH_VALIDATION", "长度验证", "验证字段长度范围",
                      Arrays.asList("LENGTH_BETWEEN", "MIN_LENGTH", "MAX_LENGTH")),

    RANGE_VALIDATION("RANGE_VALIDATION", "范围验证", "验证数值或日期范围",
                     Arrays.asList("BETWEEN", "GREATER_THAN", "LESS_THAN", "GREATER_EQUAL", "LESS_EQUAL")),

    FORMAT_VALIDATION("FORMAT_VALIDATION", "格式验证", "验证字段格式（正则表达式）",
                      Arrays.asList("REGEX_MATCH", "EMAIL_FORMAT", "PHONE_FORMAT", "ID_CARD_FORMAT")),

    SUBTABLE_VALIDATION("SUBTABLE_VALIDATION", "子表空行验证", "验证子表是否有数据",
                        Arrays.asList("HAS_DATA", "IS_EMPTY"));

    /**
     * 校验类型编码
     */
    private final String validationType;

    /**
     * 显示名称
     */
    private final String displayName;

    /**
     * 描述信息
     */
    private final String description;

    /**
     * 支持的条件列表
     */
    private final List<String> supportedConditions;

    /**
     * 构造函数
     *
     * @param validationType 校验类型编码
     * @param displayName 显示名称
     * @param description 描述信息
     * @param supportedConditions 支持的条件列表
     */
    ValidationTypeEnum(String validationType, String displayName, String description,
                       List<String> supportedConditions) {
        this.validationType = validationType;
        this.displayName = displayName;
        this.description = description;
        this.supportedConditions = supportedConditions;
    }

    /**
     * 根据编码获取枚举
     *
     * @param validationType 校验类型编码
     * @return 校验类型枚举
     */
    public static ValidationTypeEnum getByType(String validationType) {
        for (ValidationTypeEnum type : values()) {
            if (type.getValidationType().equals(validationType)) {
                return type;
            }
        }
        return null;
    }
}
