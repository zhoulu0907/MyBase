package com.cmsr.onebase.module.app.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 14:01
 */
@Data
public class RoleDTO {

    @Schema(description = "角色编号", example = "1024")
    private Long id;

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;
}
