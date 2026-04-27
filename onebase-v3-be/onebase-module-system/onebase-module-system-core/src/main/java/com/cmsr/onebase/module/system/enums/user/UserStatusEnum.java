package com.cmsr.onebase.module.system.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User 状态的枚举值
 *
 * @author lingma
 * @date 2025-08-01
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

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