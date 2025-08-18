package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 9:36
 */
@Data
@Schema(description = "应用管理 - 角色权限 Response VO")
public class AuthDetailPermissionVO {

    @Schema(description = "应用Id")
    private Long applicationId;

    @Schema(description = "角色Id")
    private Long roleId;

    @Schema(description = "菜单Id")
    private Long menuId;

    @Schema(description = "页面是否可访问")
    private Boolean pageAllowed = Boolean.TRUE;

    @Schema(description = "操作权限")
    private List<AuthOperationVO> authOperations;

    @Schema(description = "实体访问权限")
    private AuthDetailEntityVO authEntity;

    @Schema(description = "数据访问权限")
    private AuthDetailDataGroupVO authData;

    @Schema(description = "字段权限")
    private AuthDetailFieldVO authField;

}
