package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点审批状态 (审批记录用)
 *
 * @author liyang
 * @date 2025-11-09
 */
@Getter
@AllArgsConstructor
public enum BpmNodeApproveStatusEnum {

    // ================== 阶段1: 节点环节前 (pre) ==================
    /**
     * 待审批
     */
    PRE_APPROVAL("pre_approval", "待审批"),

    /**
     * 待执行
     */
    PRE_EXEC("pre_exec", "待执行"),

    /**
     * 自动抄送
     */
    PRE_AUTO_CC("pre_auto_cc", "自动抄送"),


    // ================== 阶段2: 节点环节中 (curr) ==================

    /**
     * 待提交
     */
    CURR_PENDING_SUBMIT("curr_pending_submit", "待提交"),

    /**
     * 审批中（蓝色字体）
     */
    CURR_IN_APPROVAL("curr_in_approval", "审批中"),

    /**
     * 执行中
     */
    CURR_IN_EXEC("curr_in_exec", "执行中"),


    // ================== 阶段3: 节点环节后 (post) ==================
    /**
     * 已提交（绿色字体）
     */
    POST_SUBMITTED("post_submitted", "已提交"),

    /**
     * 已同意（绿色字体）
     */
    POST_APPROVED("post_approved", "已同意"),

    /**
     * 已拒绝（红色字体）
     */
    POST_REJECTED("post_rejected", "已拒绝"),

    /**
     * 已转交
     */
    POST_TRANSFERRED("post_transferred", "已转交"),

    /**
     * 已加签
     */
    POST_ADD_SIGNER("post_add_signer", "已加签"),

    /**
     * 已退回（红色字体）
     */
    POST_RETURNED("post_returned", "已退回"),

    /**
     * 已弃权
     */
    POST_ABSTAINED("post_abstained", "已弃权"),

    /**
     * 已撤回（红色字体）
     */
    POST_WITHDRAWN("post_withdrawn", "已撤回"),

    /**
     * 自动通过（绿色字体）
     */
    POST_AUTO_APPROVED("post_auto_approved", "自动通过"),

    /**
     * 自动拒绝（红色字体）
     */
    POST_AUTO_REJECTED("post_auto_rejected", "自动拒绝"),

    /**
     * 自动转交
     */
    POST_AUTO_TRANSFERRED("post_auto_transferred", "自动转交"),

    /**
     * 自动跳过
     */
    POST_AUTO_SKIPPED("post_auto_skipped", "自动跳过"),

    /**
     * 自动抄送
     */
    POST_AUTO_CC("post_auto_cc", "自动抄送"),
    ;

    /**
     * 状态编码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据编码获取节点审批状态
     *
     * @param code 状态编码
     * @return 节点审批状态枚举
     */
    public static BpmNodeApproveStatusEnum getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String lowerCode = code.toLowerCase();

        for (BpmNodeApproveStatusEnum status : values()) {
            if (status.getCode().equals(lowerCode)) {
                return status;
            }
        }

        return null;
    }
}
