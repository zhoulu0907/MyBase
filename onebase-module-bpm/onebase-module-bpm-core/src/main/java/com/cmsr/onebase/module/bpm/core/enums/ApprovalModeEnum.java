package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批方式枚举
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum ApprovalModeEnum {

    /**
     * 会签
     * 所有人都需要审批
     */
    COUNTER_SIGN("counter_sign", "会签"),

    /**
     * 或签
     * 任意一人审批即可
     */
    ANY_SIGN("any_sign", "或签");

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
    public static ApprovalModeEnum getByCode(String code) {
        for (ApprovalModeEnum mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        return null;
    }
}
