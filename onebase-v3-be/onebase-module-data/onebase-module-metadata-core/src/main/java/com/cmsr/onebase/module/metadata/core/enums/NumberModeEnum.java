package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自动编号模式枚举
 *
 * @author bty418
 * @date 2025-09-17
 */
@Getter
@AllArgsConstructor
public enum NumberModeEnum {

    /**
     * 自然数编号
     */
    NATURAL("NATURAL", "自然数编号"),

    /**
     * 指定位数编号
     */
    FIXED_DIGIT("FIXED_DIGIT", "指定位数编号"),
    
    /**
     * 指定位数编号（兼容旧数据）
     */
    FIXED_DIGITS("FIXED_DIGITS", "指定位数编号");

    /**
     * 模式编码
     */
    private final String code;

    /**
     * 模式描述
     */
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static NumberModeEnum fromCode(String code) {
        for (NumberModeEnum mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown number mode: " + code);
    }
}
