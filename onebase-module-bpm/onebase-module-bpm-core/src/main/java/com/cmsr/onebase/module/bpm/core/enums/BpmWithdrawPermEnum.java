package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程撤回权限枚举
 *
 * @author liyang
 * @date 2025-11-04
 */
@Getter
@AllArgsConstructor
public enum BpmWithdrawPermEnum {
    /**
     * 不允许撤回
     */
    NONE("none", "不允许撤回"),

    /**
     * 仅允许发起节点撤回
     */
    INITIATION_NODE("initiation_node", "仅允许发起节点撤回"),

    /**
     * 允许所有节点撤回
     */
    ALL("all", "允许所有节点撤回");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static BpmWithdrawPermEnum getByCode(String code) {
        for (BpmWithdrawPermEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
