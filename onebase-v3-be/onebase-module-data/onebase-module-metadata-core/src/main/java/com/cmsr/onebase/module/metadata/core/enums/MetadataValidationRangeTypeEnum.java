package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 范围校验类型编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataValidationRangeTypeEnum {

    NUMBER("NUMBER"),
    DECIMAL("DECIMAL"),
    DATE("DATE"),
    DATETIME("DATETIME");

    private final String code;

    public boolean matches(String value) {
        return value != null && code.equalsIgnoreCase(value);
    }

    public static boolean isNumericType(String value) {
        return NUMBER.matches(value) || DECIMAL.matches(value);
    }

    public static boolean isDateType(String value) {
        return DATE.matches(value) || DATETIME.matches(value);
    }
}
