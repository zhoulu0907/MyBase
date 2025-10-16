package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自动编号重置周期枚举
 *
 * @author bty418
 * @date 2025-09-17
 */
@Getter
@AllArgsConstructor
public enum ResetCycleEnum {

    /**
     * 不重置
     */
    NEVER("NEVER", "不重置"),

    /**
     * 不重置（兼容旧数据）
     */
    NONE("NONE", "不重置"),

    /**
     * 每日重置
     */
    DAILY("DAILY", "每日重置"),

    /**
     * 每月重置
     */
    MONTHLY("MONTHLY", "每月重置"),

    /**
     * 每年重置
     */
    YEARLY("YEARLY", "每年重置");

    /**
     * 周期编码
     */
    private final String code;

    /**
     * 周期描述
     */
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static ResetCycleEnum fromCode(String code) {
        for (ResetCycleEnum cycle : values()) {
            if (cycle.getCode().equals(code)) {
                return cycle;
            }
        }
        throw new IllegalArgumentException("Unknown reset cycle: " + code);
    }
}
