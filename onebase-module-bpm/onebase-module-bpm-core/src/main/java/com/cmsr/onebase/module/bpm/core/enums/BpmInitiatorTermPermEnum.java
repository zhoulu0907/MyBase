package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程发起人终止权限枚举
 *
 * @author liyang
 * @date 2025-11-04
 */
@Getter
@AllArgsConstructor
public enum BpmInitiatorTermPermEnum {
    /**
     * 仅可在发起节点终止
     */
    INITIATION_NODE("initiation_node", "仅可在发起节点终止"),

    /**
     * 可在任意环节终止
     */
    ANY("any","可在任意环节终止"),

    /**
     * 无终止权限
     */
    NONE("none","发起人无终止权限");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;
    public static BpmInitiatorTermPermEnum getByCode(String code) {
        for (BpmInitiatorTermPermEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
