package com.cmsr.onebase.module.app.build.controller.auth;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.auth.AppAuthPermissionService;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：huangjie
 * @Date：2025/8/7 8:57
 */
@Tag(name = "应用管理-权限管理")
@Setter
@RestController
@RequestMapping("/app/auth-permission")
@Validated
public class AppAuthPermissionController {

    @Autowired
    private AppAuthPermissionService authPermissionService;


    /**
     * 获取角色-菜单的功能权限
     */
    @GetMapping("/get-function")
    @Operation(summary = "获取角色-菜单的功能权限")
    public CommonResult<AuthDetailFunctionPermissionVO> getFunctionPermission(@Valid AuthPermissionReq reqVO) {
        return CommonResult.success(authPermissionService.getFunctionPermission(reqVO));
    }

    /**
     * 获取角色-菜单的数据权限
     */
    @GetMapping("/get-data")
    @Operation(summary = "获取角色-菜单的数据权限")
    public CommonResult<AuthDetailDataPermissionVO> getDataPermission(@Valid AuthPermissionReq reqVO) {
        return CommonResult.success(authPermissionService.getDataPermission(reqVO));
    }

    /**
     * 获取角色-字段的权限
     */
    @GetMapping("/get-field")
    @Operation(summary = "获取角色-字段的权限")
    public CommonResult<AuthDetailFieldPermissionVO> getFieldPermission(@Valid AuthPermissionReq reqVO) {
        return CommonResult.success(authPermissionService.getFieldPermission(reqVO));
    }

    /**
     * 更新页面权限
     */
    @PostMapping("/update-page-allowed")
    @Operation(summary = "更新页面权限")
    public CommonResult<Boolean> updatePageAllowed(@Valid @RequestBody AuthUpdatePageAllowedReqVO reqVO) {
        authPermissionService.updatePageAllowed(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 更新操作权限
     */
    @PostMapping("/update-operation")
    @Operation(summary = "更新操作权限")
    public CommonResult<Boolean> updateOperation(@Valid @RequestBody AuthUpdateOperationReqVO reqVO) {
        authPermissionService.updateOperation(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 更新字段权限
     */
    @PostMapping("/update-view")
    @Operation(summary = "更新视图权限")
    public CommonResult<Boolean> updateView(@Valid @RequestBody AuthUpdateViewReqVO reqVO) {
        authPermissionService.updateView(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 更新数据组权限
     */
    @PostMapping("/update-data-group")
    @Operation(summary = "更新数据组权限")
    public CommonResult<Boolean> updateDataGroup(@Valid @RequestBody AuthUpdateDataGroupReqVO reqVO) {
        authPermissionService.updateDataGroup(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 删除数据组权限
     */
    @PostMapping("/delete-data-group")
    @Operation(summary = "删除数据组权限")
    public CommonResult<Boolean> deleteDataGroup(@RequestParam("id") Long id) {
        authPermissionService.deleteDataGroup(id);
        return CommonResult.success(true);
    }

    /**
     * 更新字段权限
     */
    @PostMapping("/update-field")
    @Operation(summary = "更新字段权限")
    public CommonResult<Boolean> updateField(@Valid @RequestBody AuthUpdateFieldReqVO reqVO) {
        authPermissionService.updateField(reqVO);
        return CommonResult.success(true);
    }


}
