package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 看板卡片同比/环比对比方向。
 */
@Getter
@AllArgsConstructor
public enum MetadataCardCompareTypeEnum {

    UP("up"),
    DOWN("down"),
    EQUAL("equal");

    private final String code;

    public static MetadataCardCompareTypeEnum fromSignum(int signum) {
        if (signum > 0) {
            return UP;
        }
        if (signum < 0) {
            return DOWN;
        }
        return EQUAL;
    }
}
