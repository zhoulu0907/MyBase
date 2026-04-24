package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 格式校验编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataValidationFormatCodeEnum {

    EMAIL("EMAIL"),
    MOBILE("MOBILE"),
    ID_CARD("ID_CARD"),
    URL("URL"),
    IP("IP"),
    TEXT("TEXT"),
    REGEX("REGEX");

    private final String code;

    public static MetadataValidationFormatCodeEnum getByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (MetadataValidationFormatCodeEnum item : values()) {
            if (item.code.equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }
}
