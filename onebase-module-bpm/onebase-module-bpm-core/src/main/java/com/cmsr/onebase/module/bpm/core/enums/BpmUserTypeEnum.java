package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型枚举，基于warmflow的UserType扩展，增加自定义类型
 *
 * @author liyang
 * @date 2025-10-20
 */
@Getter
@AllArgsConstructor
public enum BpmUserTypeEnum {

    /**
     * 待办任务的审批人权限
     */
    APPROVAL("1", "待办任务的审批人权限"),

    /**
     * 待办任务的转办人权限
     */
    TRANSFER("2", "待办任务的转办人权限"),

    /**
     * 待办任务的委托人权限
     */
    DEPUTE("3", "待办任务的委托人权限"),


    // ============== 以下为自定义权限类型，以100开头，避免与后续的更新冲突 ===========

    /**
     * 待办任务的代理人权限
     */
    AGENT("100", "待办任务的代理人权限"),

    /**
     * 待办任务的抄送人权限
     */
    CC("101", "待办任务的抄送人权限"),
    ;

    /**
     * 节点编码
     */
    private final String code;

    /**
     * 节点名称
     */
    private final String name;

    /**
     * 根据编码获取节点类型
     *
     * @param code 节点编码
     * @return BpmNodeTypeEnum
     */
    public static BpmUserTypeEnum getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String lowerCode = code.toLowerCase();

        for (BpmUserTypeEnum nodeType : values()) {
            if (nodeType.getCode().equals(lowerCode)) {
                return nodeType;
            }
        }

        return null;
    }
}
