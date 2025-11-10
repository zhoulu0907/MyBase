package com.cmsr.onebase.module.bpm.core.dto.node.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
     * 按钮类型
     */
    @NotBlank(message = "按钮类型不能为空")
    private String buttonType;

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
    @NotNull(message = "审批意见必填不能为空")
    private Boolean approvalCommentRequired;

    /**
     * 是否启用按钮
     */
    @NotNull(message = "是否启用按钮不能为空")
    private Boolean enabled;
}
