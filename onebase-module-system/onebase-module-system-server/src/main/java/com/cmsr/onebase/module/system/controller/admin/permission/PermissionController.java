package com.cmsr.onebase.module.system.controller.admin.permission;

import cn.hutool.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleUsersReqVO;
import com.cmsr.onebase.module.system.controller.admin.permission.vo.permission.PermissionDeleteRoleUsersReqVO;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Set;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 权限 Controller，提供赋予用户、角色的权限的 API 接口
 *
 */
@Tag(name = "管理后台 - 权限")
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;
    @Resource
    private TenantService tenantService;

    @Operation(summary = "获得角色拥有的菜单编号")
    @Parameter(name = "roleId", description = "角色编号", required = true)
    @GetMapping("/list-role-menus")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Set<Long>> getRoleMenuList(Long roleId) {
        return success(permissionService.getRoleMenuListByRoleId(roleId));
    }

    @PostMapping("/assign-role-menu")
    @Operation(summary = "赋予角色菜单")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> assignRoleMenu(@Validated @RequestBody PermissionAssignRoleMenuReqVO reqVO) {
        // 开启多租户的情况下，需要过滤掉未开通的菜单
        tenantService.handleTenantMenu(menuIds -> reqVO.getMenuIds().removeIf(menuId -> !CollUtil.contains(menuIds, menuId)));

        // 执行菜单的分配
        permissionService.assignRoleMenu(reqVO.getRoleId(), reqVO.getMenuIds());
        return success(true);
    }

    @PostMapping("/assign-role-data-scope")
    @Operation(summary = "赋予角色数据权限")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-data-scope')")
    public CommonResult<Boolean> assignRoleDataScope(@Valid @RequestBody PermissionAssignRoleDataScopeReqVO reqVO) {
        permissionService.assignRoleDataScope(reqVO.getRoleId(), reqVO.getDataScope(), reqVO.getDataScopeDeptIds());
        return success(true);
    }

    @Operation(summary = "获得用户拥有的角色编号列表")
    @Parameter(name = "userId", description = "用户编号", required = true)
    @GetMapping("/list-user-roles")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Set<Long>> listAdminRoles(@RequestParam("userId") Long userId) {
        return success(permissionService.getUserRoleIdListByUserId(userId));
    }

    @Operation(summary = "赋予用户角色")
    @PostMapping("/assign-user-roles")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> assignUserRoles(@Validated @RequestBody PermissionAssignUserRoleReqVO reqVO) {
        permissionService.assignUserRoles(reqVO.getUserId(), reqVO.getRoleIds());
        return success(true);
    }

    @Operation(summary = "为角色分配用户")
    @PostMapping("/assign-role-users")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-user')")
    public CommonResult<Boolean> assignRoleUsers(@Validated @RequestBody PermissionAssignRoleUsersReqVO reqVO) {
        permissionService.addRoleUsers(reqVO.getRoleId(), reqVO.getUserIds());
        return success(true);
    }

    @Operation(summary = "从角色中移除用户")
    @PostMapping("/delete-role-users")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-user')")
    public CommonResult<Boolean> deleteRoleUsers(@Validated @RequestBody PermissionDeleteRoleUsersReqVO reqVO) {
        permissionService.deleteRoleUsers(reqVO.getRoleId(), reqVO.getUserIds());
        return success(true);
    }

}
