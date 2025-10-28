package com.cmsr.onebase.module.app.api.auth.dto;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 14:01
 */
@Data
public class RoleDTO {

    /**
     * 角色编号
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;
}