package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 页面视图模式枚举
 *
 * @author liyang
 * @date 2025-11-15
 */
@Getter
@AllArgsConstructor
public enum PageViewModeEnum {
    /**
     * 编辑模式
     */
    EDIT("edit", "编辑模式"),

    /**
     * 详情模式
     */
    DETAIL("detail", "详情模式");

    /**
     * 方式编码
     */
    private final String code;

    /**
     * 方式名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static PageViewModeEnum getByCode(String code) {
        for (PageViewModeEnum mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        return null;
    }
}
