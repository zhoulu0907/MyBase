package com.cmsr.onebase.module.app.core.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 17:35
 */
@Schema(description = "应用管理 - 权限 Request VO")
@Data
public class AuthPermissionReq {

    @Schema(description = "应用Id")
    private Long applicationId;

    //TODO 等前端切换，再删除
    @Schema(description = "角色Id")
    private Long roleId;

    @Schema(description = "角色uuid")
    private String roleUuid;

    //TODO 等前端切换，再删除
    @Schema(description = "菜单Id")
    private Long menuId;

    @Schema(description = "菜单uuid")
    private String menuUuid;

}
