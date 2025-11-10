package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程代理状态枚举
 *
 * @author liyang
 * @date 2025-11-10
 */
@Getter
@AllArgsConstructor
public enum BpmDelegationStatus {
    /**
     * 待生效
     */
    INACTIVE("inactive", "待生效"),

    /**
     * 代理中
     */
    ACTIVE("active", "代理中"),

    /**
     * 已失效
     */
    EXPIRED("expired", "已失效"),

    /**
     * 已撤销
     */
    REVOKED("revoked", "已撤销"),
    ;

    private final String code;
    private final String name;


    public static BpmDelegationStatus getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String lowerCode = code.toLowerCase();

        for (BpmDelegationStatus delegationStatus : values()) {
            if (delegationStatus.getCode().equals(lowerCode)) {
                return delegationStatus;
            }
        }

        return null;
    }
}
