package com.cmsr.onebase.module.bpm.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局配置流程发起人终止权限枚举
 */
@Getter
@AllArgsConstructor
public enum GlobalInitiTermiFlowPermEnum {
    /**
     * 仅可在发起节点终止
     */
    INITIATOR_NODE_ONLY("initiator_node_only","仅可在发起节点终止"),
    /**
     * 可在任意环节终止
     */
    ANY_NODE("any_node","可在任意环节终止"),
    /**
     * 无终止权限
     */
    NO_PERMISSION("no_permission","发起人无终止权限");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;
    public static GlobalInitiTermiFlowPermEnum getByCode(String code) {
        for (GlobalInitiTermiFlowPermEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
