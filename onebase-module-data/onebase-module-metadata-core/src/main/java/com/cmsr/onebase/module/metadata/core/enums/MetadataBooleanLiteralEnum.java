package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 布尔语义常见字面值。
 */
@Getter
@AllArgsConstructor
public enum MetadataBooleanLiteralEnum {

    TRUE("true"),
    FALSE("false"),
    ONE("1"),
    ZERO("0"),
    YES("yes"),
    NO("no");

    private final String literal;

    public boolean matches(String value) {
        return value != null && literal.equalsIgnoreCase(value.trim());
    }

    public static boolean isBooleanLiteral(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (MetadataBooleanLiteralEnum literal : values()) {
            if (literal.matches(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSqlBooleanLiteral(String value) {
        return TRUE.matches(value) || FALSE.matches(value);
    }
}
