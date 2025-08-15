package com.cmsr.onebase.module.app.controller.admin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 17:35
 */
@Schema(description = "应用管理 - 权限")
@Data
public class AuthPermissionDTO {

    @Schema(description = "应用Id")
    private Long applicationId;

    @Schema(description = "角色Id")
    private Long roleId;

    @Schema(description = "菜单Id")
    private Long menuId;

}
