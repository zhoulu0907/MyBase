package com.cmsr.onebase.module.bpm.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalConfigEnum {
    // ==================== 审批人为空处理 ====================
    /*
    * 暂停：流程暂停
     */
    PAUSE("pause","流程暂停"),
    /*
    * 自动跳过：自动跳过当前节点
     */
    SKIP("skip","自动跳过节点"),
    /*
    * 转交给应用管理员：转交给应用管理员处理
     */
    TRANSFER_ADMIN("transfer_admin","转交给应用管理员"),
    /*
    * 转交给指定成员：转交给指定成员处理
     */
    TRANSFER_MEMBER("transfer_member","转交给指定成员"),

    // ==================== 流程撤回规则 ====================
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
    UNREAD("unread","未读"),

    // ==================== 流程退回规则 ====================
    /**
     * 按顺序重新审批
     */
    SEQUENTIAL("sequential","按流程顺序重新审批"),
    /**
     * 直达当前节点
     */
    DIRECT("direct","直达当前节点"),
    // ==================== 流程发起人终止权限 ====================
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
    public static GlobalConfigEnum getByCode(String code) {
        for (GlobalConfigEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
