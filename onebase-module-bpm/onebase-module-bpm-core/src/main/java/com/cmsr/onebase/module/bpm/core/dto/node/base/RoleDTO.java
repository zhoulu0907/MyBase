package com.cmsr.onebase.module.bpm.core.dto.node.base;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
}
