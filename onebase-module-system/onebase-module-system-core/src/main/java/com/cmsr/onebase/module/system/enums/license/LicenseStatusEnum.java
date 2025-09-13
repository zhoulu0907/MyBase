package com.cmsr.onebase.module.system.enums.license;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * License 状态的枚举值
 *
 * @author lingma
 * @date 2025-08-01
 */
@Getter
@AllArgsConstructor
public enum LicenseStatusEnum {

    /**
     * 启用
     */
    ENABLE("enable"),
    /**
     * 禁用
     */
    DISABLE("disable");

    /**
     * 状态值
     */
    private final String status;

}