package com.cmsr.onebase.module.system.enums.tenant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Tenant 状态的枚举值
 *
 * @author lingma
 * @date 2025-08-01
 */
@Getter
@AllArgsConstructor
public enum TenantStatusEnum {

    /**
     * 正常
     */
    NORMAL(1),
    /**
     * 停用
     */
    DISABLE(0);

    /**
     * 状态值
     */
    private final Integer status;

}