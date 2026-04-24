package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 默认值表达式常见关键字。
 */
@Getter
@AllArgsConstructor
public enum MetadataDefaultValueKeywordEnum {

    CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
    NOW_CALL("NOW()"),
    CURRENT_DATE("CURRENT_DATE"),
    CURRENT_TIME("CURRENT_TIME"),
    CURRENT_PREFIX("CURRENT_"),
    NOW_PREFIX("NOW("),
    UUID("UUID"),
    NULL("NULL");

    private final String keyword;

    public boolean equalsTo(String value) {
        return value != null && keyword.equalsIgnoreCase(value.trim());
    }

    public boolean containsIn(String value) {
        return value != null && value.toUpperCase().contains(keyword);
    }
}
