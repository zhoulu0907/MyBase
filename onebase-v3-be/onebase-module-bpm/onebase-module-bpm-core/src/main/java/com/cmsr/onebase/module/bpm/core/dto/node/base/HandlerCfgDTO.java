package com.cmsr.onebase.module.bpm.core.dto.node.base;

import lombok.Data;

import java.util.List;

/**
 *
 * 处理人配置
 *
 * @author liyang
 * @date 2025-11-17
 */
@Data
public class HandlerCfgDTO {
    /**
     * 处理人类型
     */
    private String handlerType;

    /**
     * 指定成员列表（当审批人类型为user时使用）
     */
    private List<UserDTO> users;

    /**
     * 指定角色列表（当审批人类型为role时使用）
     */
    private List<RoleDTO> roles;
}
