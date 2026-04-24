package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 元数据支持的数据源类型编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataDatasourceTypeEnum {

    POSTGRESQL("POSTGRESQL"),
    KINGBASE("KINGBASE");

    private final String code;

    public boolean matches(String value) {
        return value != null && code.equalsIgnoreCase(value);
    }

    public static boolean isPostgresFamily(String value) {
        return POSTGRESQL.matches(value) || KINGBASE.matches(value);
    }
}
