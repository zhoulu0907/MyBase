package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段校验规则类型编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataValidationRuleTypeEnum {

    REQUIRED("REQUIRED", "必填校验"),
    UNIQUE("UNIQUE", "唯一校验"),
    LENGTH("LENGTH", "长度校验"),
    LENGTH_RANGE("LENGTH_RANGE", "长度校验"),
    RANGE("RANGE", "范围校验"),
    FORMAT("FORMAT", "格式校验"),
    REGEX("REGEX", "格式校验"),
    CHILD_NOT_EMPTY("CHILD_NOT_EMPTY", "子表空行校验"),
    SELF_DEFINED("SELF_DEFINED", "自定义校验"),
    CUSTOM("CUSTOM", "自定义校验");

    private final String code;
    private final String displayName;

    public static MetadataValidationRuleTypeEnum getByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (MetadataValidationRuleTypeEnum type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
