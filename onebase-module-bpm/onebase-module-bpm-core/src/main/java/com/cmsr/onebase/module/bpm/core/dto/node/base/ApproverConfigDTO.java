package com.cmsr.onebase.module.bpm.core.dto.node.base;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 审批人配置
 *
 * 用于审批人节点内部的审批人配置
 * 包含审批人的具体配置信息
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class ApproverConfigDTO {

    /**
     * 审批人类型
     * user: 指定成员
     * role: 指定角色
     */
    @NotBlank(message = "审批人类型不能为空")
    private String approverType;

    /**
     * 指定成员列表（当审批人类型为user时使用）
     */
    private List<UserDTO> users;

    /**
     * 指定角色列表（当审批人类型为role时使用）
     */
    private List<RoleDTO> roles;

    /**
     * 审批方式
     * : 会签（所有人都需要审批）
     * any_sign: 或签（任意一人审批即可）
     */
    @NotBlank(message = "审批方式不能为空")
    private String approvalMode;
}
