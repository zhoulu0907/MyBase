package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 *
 * 审批人为空处理枚举
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum BpmEmptyApproverEnum {
    /* 暂停：流程暂停
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
    TRANSFER_MEMBER("transfer_member","转交给指定成员");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    public static BpmEmptyApproverEnum getByCode(String code) {
        for (BpmEmptyApproverEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
