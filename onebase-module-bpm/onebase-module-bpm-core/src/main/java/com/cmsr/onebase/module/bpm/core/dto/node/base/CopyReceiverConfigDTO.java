package com.cmsr.onebase.module.bpm.core.dto.node.base;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 抄送人配置
 *
 * 用于抄送人节点内部的抄送人配置
 * 包含抄送人的具体配置信息
 *
 */
@Data
public class CopyReceiverConfigDTO {
    /**
     * 抄送人类型
     * user: 指定成员
     * role: 指定角色
     */
    @NotBlank(message = "抄送人类型不能为空")
    private String copyReceiverType;

    /**
     * 指定成员列表（当抄送人类型为user时使用）
     */
    private List<UserDTO> users;

    /**
     * 指定角色列表（当抄送人类型为role时使用）
     */
    private List<RoleDTO> roles;
}
