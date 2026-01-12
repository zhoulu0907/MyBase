package com.cmsr.onebase.plugin.ocr.enums;

import lombok.Getter;

/**
 * 港澳台通行证类型枚举
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Getter
public enum ExitentrypermitType {

    HK_MC_PASSPORT_FRONT("hk_mc_passport_front", "港澳通行证正面"),
    HK_MC_PASSPORT_BACK("hk_mc_passport_back", "港澳通行证反面"),
    TW_PASSPORT_FRONT("tw_passport_front", "台湾通行证正面"),
    TW_PASSPORT_BACK("tw_passport_back", "台湾通行证反面"),
    TW_RETURN_PASSPORT_FRONT("tw_return_passport_front", "台湾居民来往大陆通行证正面"),
    TW_RETURN_PASSPORT_BACK("tw_return_passport_back", "台湾居民来往大陆通行证反面"),
    HK_MC_RETURN_PASSPORT_FRONT("hk_mc_return_passport_front", "港澳居民来往内地通行证正面"),
    HK_MC_RETURN_PASSPORT_BACK("hk_mc_return_passport_back", "港澳居民来往内地通行证反面");

    private final String value;
    private final String description;

    ExitentrypermitType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ExitentrypermitType getByValue(String value) {
        for (ExitentrypermitType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
