package com.cmsr.onebase.module.bpm.api.dto.node.base;

import lombok.Data;

/**
 * 角色信息
 *
 * @author liyang
 * @date 2025-10-21
 */
@Data
public class RoleDTO {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;
}
