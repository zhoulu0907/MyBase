package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文本类字段在存储层的类型编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataTextStorageTypeEnum {

    VARCHAR("VARCHAR"),
    TEXT("TEXT"),
    CHAR("CHAR");

    private final String code;

    public boolean matches(String fieldType) {
        return fieldType != null && code.equalsIgnoreCase(fieldType);
    }

    public static boolean isTextType(String fieldType) {
        for (MetadataTextStorageTypeEnum type : values()) {
            if (type.matches(fieldType)) {
                return true;
            }
        }
        return false;
    }
}
