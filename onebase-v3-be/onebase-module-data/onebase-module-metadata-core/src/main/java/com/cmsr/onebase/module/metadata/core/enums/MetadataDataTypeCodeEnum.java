package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 元数据系统中常见的数据类型编码（字段类型/JDBC类型/数据库类型）。
 */
@Getter
@AllArgsConstructor
public enum MetadataDataTypeCodeEnum {

    STRING("STRING"),
    TEXT("TEXT"),
    LONG_TEXT("LONG_TEXT"),
    VARCHAR("VARCHAR"),
    LONGVARCHAR("LONGVARCHAR"),
    CHAR("CHAR"),

    INTEGER("INTEGER"),
    INT("INT"),
    SMALLINT("SMALLINT"),
    TINYINT("TINYINT"),
    BIGINT("BIGINT"),
    LONG("LONG"),
    NUMBER("NUMBER"),
    NUMERIC("NUMERIC"),
    DECIMAL("DECIMAL"),
    DOUBLE("DOUBLE"),
    FLOAT("FLOAT"),

    BOOLEAN("BOOLEAN"),
    BOOL("BOOL"),

    DATE("DATE"),
    DATETIME("DATETIME"),
    TIMESTAMP("TIMESTAMP"),
    TIME("TIME"),

    JSON("JSON"),
    JSONB("JSONB"),
    ARRAY("ARRAY"),

    SINGLE_SELECT("SINGLE_SELECT"),
    MULTI_SELECT("MULTI_SELECT"),
    AUTO_NUMBER("AUTO_NUMBER");

    private final String code;

    public boolean matches(String value) {
        return value != null && code.equalsIgnoreCase(value);
    }

    public boolean containsIn(String value) {
        return value != null && value.toUpperCase().contains(code);
    }

    public static MetadataDataTypeCodeEnum fromCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (MetadataDataTypeCodeEnum type : values()) {
            if (type.matches(value)) {
                return type;
            }
        }
        return null;
    }

    public static boolean matchesAny(String value, MetadataDataTypeCodeEnum... candidates) {
        if (value == null || candidates == null || candidates.length == 0) {
            return false;
        }
        for (MetadataDataTypeCodeEnum candidate : candidates) {
            if (candidate != null && candidate.matches(value)) {
                return true;
            }
        }
        return false;
    }
}
