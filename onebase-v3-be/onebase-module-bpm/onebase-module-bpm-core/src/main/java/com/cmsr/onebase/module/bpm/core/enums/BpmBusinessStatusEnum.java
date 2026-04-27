package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态枚举
 *
 * @author may
 */
@Getter
@AllArgsConstructor
public enum BpmBusinessStatusEnum {

    /**
     * 草稿：尚未提交，仅保存在草稿箱
     */
    DRAFT("draft", "草稿"),

    /**
     * 审批中：流程已启动，正在流转中
     */
    IN_APPROVAL("in_approval", "审批中"),

    /**
     * 已通过：流程正常结束，所有审批节点同意
     */
    APPROVED("approved", "已通过"),

    /**
     * 已拒绝：任一审批人拒绝，流程终止
     */
    REJECTED("rejected", "已拒绝"),

    /**
     * 已撤回：发起人在流程结束前主动撤回申请
     */
    WITHDRAWN("withdrawn", "已撤回"),

    /**
     * 已终止：由发起人或管理员手动强制终止（非撤回、非拒绝）
     */
    TERMINATED("terminated", "已终止"),
    ;

    /**
     * 状态
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;


    /**
     * 根据状态获取枚举
     *
     * @param code 状态
     * @return 枚举
     */
    public static BpmBusinessStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }

        for (BpmBusinessStatusEnum button : values()) {
            if (button.code.equals(code)) {
                return button;
            }
        }
        return null;
    }

}
