package com.cmsr.onebase.module.bpm.api.dto.node.base;

import lombok.Data;

/**
 * 审批人节点按钮配置信息
 *
 * @author liyang
 * @date 2025-10-22
 */
@Data
public class BaseNodeBtnCfgDTO {
    /**
     * 按钮名称
     */
    private String buttonName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 默认审批意见
     */
    private String defaultApprovalComment;

    /**
     * 审批意见必填
     */
    private Boolean approvalCommentRequired;

    /**
     * 是否启用按钮
     */
    private Boolean enabled;
}
