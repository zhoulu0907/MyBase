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
public class AuthPermissionDetailVO {

    @Schema(description = "应用Id")
    private Long applicationId;

    @Schema(description = "应用Code")
    private String applicationCode;

    @Schema(description = "角色Id")
    private Long roleId;

    @Schema(description = "角色Code")
    private String roleCode;

    @Schema(description = "菜单Id")
    private Long menuId;

    @Schema(description = "菜单Code")
    private String menuCode;

    @Schema(description = "应用权限")
    private AuthPermissionVO authPermission;

    @Schema(description = "操作权限")
    private List<AuthOperationVO> authOperations;

    @Schema(description = "实体访问权限")
    private List<AuthEntityVO> authEntities;

    @Schema(description = "数据访问权限")
    private List<AuthDataGroupVO> authDataGroups;

    @Schema(description = "字段权限")
    private List<AuthFieldVO> authFields;

}
