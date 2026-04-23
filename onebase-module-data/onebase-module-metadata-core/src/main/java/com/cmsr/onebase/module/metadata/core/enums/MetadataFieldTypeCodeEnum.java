package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 元数据字段类型编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataFieldTypeCodeEnum {

    SELECT("SELECT"),
    SINGLE_SELECT("SINGLE_SELECT"),
    MULTI_SELECT("MULTI_SELECT"),
    PICKLIST("PICKLIST"),
    USER("USER"),
    DATA_SELECTION("DATA_SELECTION"),
    MULTI_USER("MULTI_USER"),
    MULTI_DEPARTMENT("MULTI_DEPARTMENT"),
    MULTI_DATA_SELECTION("MULTI_DATA_SELECTION");

    private final String code;

    public boolean matches(String fieldType) {
        return code.equalsIgnoreCase(fieldType);
    }

    public static boolean isDataSelection(String fieldType) {
        return DATA_SELECTION.matches(fieldType) || MULTI_DATA_SELECTION.matches(fieldType);
    }

    public static boolean isOptionLike(String fieldType) {
        return SELECT.matches(fieldType)
                || SINGLE_SELECT.matches(fieldType)
                || MULTI_SELECT.matches(fieldType)
                || PICKLIST.matches(fieldType)
                || DATA_SELECTION.matches(fieldType)
                || MULTI_USER.matches(fieldType)
                || MULTI_DEPARTMENT.matches(fieldType)
                || MULTI_DATA_SELECTION.matches(fieldType);
    }
}
