package com.cmsr.onebase.module.app.build.controller.auth;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.service.auth.AppAuthRoleService;
import com.cmsr.onebase.module.app.build.vo.auth.*;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersRespDTO;
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
    public CommonResult<List<AuthRoleListRespVO>> getRoleList(
            @RequestParam(value = "applicationId") Long applicationId) {
        return CommonResult.success(authRoleService.getRoleList(applicationId));
    }

//    @GetMapping("/page-role-users")
//    @Operation(summary = "获取角色用户列表")
//    public CommonResult<PageResult<AuthRoleUsersPageRespVO>> pageRoleUsers(@Validated AuthRoleUsersPageReqVO reqVO) {
//        return CommonResult.success(authRoleService.pageRoleUsers(reqVO));
//    }

    @GetMapping("/page-role-members")
    @Operation(summary = "获取角色成员列表")
    public CommonResult<PageResult<AuthRoleMembersPageRespVO>> pageRoleUsers(@Validated AuthRoleMembersPageReqVO reqVO) {
        return CommonResult.success(authRoleService.pageRoleMembers(reqVO));
    }


    @GetMapping("/list-dept-users")
    @Operation(summary = "获取部门用户列表")
    public CommonResult<DeptAndUsersRespDTO> listDeptUsers(@Validated AuthRoleDeptAndUsersReqVO reqVO) {
        return CommonResult.success(authRoleService.listDeptUsers(reqVO));
    }

    /**
     * 新增角色
     */
    @PostMapping("/create")
    @Operation(summary = "新增角色")
    public CommonResult<AuthRoleCreateRespVO> createRole(@Valid @RequestBody AuthRoleCreateReqVO reqVO) {
        return CommonResult.success(authRoleService.createRole(reqVO));
    }

    /**
     * 重命名角色
     */
    @PostMapping("/rename")
    @Operation(summary = "重命名角色")
    public CommonResult<Boolean> renameRole(@RequestParam("id") Long roleId,
                                            @RequestParam("name") String name) {
        authRoleService.renameRole(roleId, name);
        return CommonResult.success(true);
    }

    /**
     * 角色添加成员
     *
     * @param reqVO
     * @return
     */
    @PostMapping("/add-user")
    @Operation(summary = "角色添加用户")
    public CommonResult<Boolean> addRoleUser(@Valid @RequestBody AuthRoleAddUserReqVO reqVO) {
        authRoleService.addRoleUser(reqVO);
        return CommonResult.success(true);
    }

//    /**
//     * 角色删除成员
//     *
//     * @param reqVO
//     * @return
//     */
//    @PostMapping("/delete-user")
//    @Operation(summary = "角色删除用户")
//    public CommonResult<Boolean> deleteRoleUser(@Valid @RequestBody AuthRoleDeleteUserReqVO reqVO) {
//        authRoleService.deleteRoleUser(reqVO);
//        return CommonResult.success(true);
//    }


    /**
     * 角色添加成员
     *
     * @param reqVO
     * @return
     */
    @PostMapping("/add-dept")
    @Operation(summary = "角色添加部门")
    public CommonResult<Boolean> addRoleDept(@Valid @RequestBody AuthRoleAddDeptReqVO reqVO) {
        authRoleService.addRoleDept(reqVO);
        return CommonResult.success(true);
    }

//    /**
//     * 角色删除成员
//     *
//     * @param reqVO
//     * @return
//     */
//    @PostMapping("/delete-dept")
//    @Operation(summary = "角色删除部门")
//    public CommonResult<Boolean> deleteRoleDept(@Valid @RequestBody AuthRoleDeleteDeptReqVO reqVO) {
//        authRoleService.deleteRoleDept(reqVO);
//        return CommonResult.success(true);
//    }


    @PostMapping("/delete-member")
    @Operation(summary = "角色删除成员")
    public CommonResult<Boolean> deleteRoleDept(@Valid @RequestBody AuthRoleDeleteMemberReqVO reqVO) {
        authRoleService.deleteRoleMember(reqVO);
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
