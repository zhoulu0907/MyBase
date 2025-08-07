package com.cmsr.onebase.module.app.controller.admin.auth;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRolePermissionReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRolePermissionRespVO;
import com.cmsr.onebase.module.app.service.auth.AppAuthPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：huangjie
 * @Date：2025/8/7 8:57
 */
@Tag(name = "应用管理-权限管理")
@RestController
@RequestMapping("/app/auth-permission")
@Validated
public class AppAuthPermissionController {

    @Resource
    private AppAuthPermissionService authPermissionService;

    /**
     * 获取角色-菜单的权限
     */
    @GetMapping("/get")
    @Operation(summary = "获取角色-菜单的权限")
    public CommonResult<AuthRolePermissionRespVO> getRolePermission(
            @RequestParam(value = "roleId") Long roleId,
            @RequestParam(value = "menuId") Long menuId) {
        return CommonResult.success(authPermissionService.getRolePermission(roleId, menuId));
    }

    /**
     * 更新角色-菜单的权限
     */
    @GetMapping("/update")
    @Operation(summary = "更新角色-菜单的权限")
    public CommonResult<Boolean> updateRolePermission(@Valid @RequestBody AuthRolePermissionReqVO reqVO) {
        return CommonResult.success(authPermissionService.updateRolePermission(reqVO));
    }


}
