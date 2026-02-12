package com.cmsr.onebase.plugin.ocr.enums;

import lombok.Getter;

/**
 * 身份证正反面枚举
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Getter
public enum IdCardSideEnum {

    FRONT("front", "身份证正面"),
    BACK("back", "身份证反面");

    private final String value;
    private final String description;

    IdCardSideEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public String getValue() {
        return value;
    }

    public static IdCardSideEnum getByValue(String value) {
        for (IdCardSideEnum side : values()) {
            if (side.getValue().equals(value)) {
                return side;
            }
        }
        return null;
    }
}
