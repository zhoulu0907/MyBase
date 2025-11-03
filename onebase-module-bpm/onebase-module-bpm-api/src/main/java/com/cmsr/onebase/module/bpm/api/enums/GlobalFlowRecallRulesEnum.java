package com.cmsr.onebase.module.bpm.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局配置流程撤回枚举
 */
@Getter
@AllArgsConstructor
public enum GlobalFlowRecallRulesEnum {
    /**
     * 不允许撤回
     */
    DISALLOWED("disallowed","不允许撤回"),
    /**
     * 仅允许发起节点撤回
     */
    START_NODE_ONLY("start_node_only","仅允许发起节点撤回"),
    /**
     * 允许所有节点撤回
     */
    ALL_NODES("all_nodes","允许所有节点撤回"),
    /**
     * 未操作
     */
    UNPROCESSED("unprocessed","未操作"),
    /**
     * 未读
     */
    UNREAD("unread","未读");
    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    public static GlobalFlowRecallRulesEnum getByCode(String code) {
        for (GlobalFlowRecallRulesEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
