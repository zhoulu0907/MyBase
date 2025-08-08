package com.cmsr.onebase.module.app.controller.admin.auth;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.*;
import com.cmsr.onebase.module.app.service.auth.AppAuthRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 8:57
 */
@Tag(name = "应用管理-角色管理")
@RestController
@RequestMapping("/app/auth-role")
@Validated
public class AppAuthRoleController {

    @Resource
    private AppAuthRoleService authRoleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    public CommonResult<List<AuthRoleListRespVO>> getAuthRoleList(
            @RequestParam(value = "applicationId") Long applicationId) {
        return CommonResult.success(authRoleService.getAuthRoleList(applicationId));
    }

    /**
     * 新增角色
     */
    @PostMapping("/create")
    @Operation(summary = "新增角色")
    public CommonResult<Boolean> createAuthRole(@Valid @RequestBody AuthRoleCreateReqVO reqVO) {
        authRoleService.createRole(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 角色添加成员
     * @param reqVO
     * @return
     */
    @PostMapping("/add-user")
    @Operation(summary = "角色添加成员")
    public CommonResult<Boolean> addUser(@Valid @RequestBody AuthRoleAddMemberReqVO reqVO) {
        authRoleService.addUser(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 角色删除成员
     * @param reqVO
     * @return
     */
    @PostMapping("/delete-user")
    @Operation(summary = "角色删除成员")
    public CommonResult<Boolean> deleteUser(@Valid @RequestBody AuthRoleDeleteMemberReqVO reqVO) {
        authRoleService.deleteUser(reqVO);
        return CommonResult.success(true);
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete")
    @Operation(summary = "删除角色")
    public CommonResult<Boolean> deleteRole(@RequestParam("roleId") Long roleId) {
        authRoleService.deleteRole(roleId);
        return CommonResult.success(true);
    }

}
