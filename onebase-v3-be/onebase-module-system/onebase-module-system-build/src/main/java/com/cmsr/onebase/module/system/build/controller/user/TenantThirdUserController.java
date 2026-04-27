package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "空间外部用户服务 - 三方用户")
@RestController
@RequestMapping("/system/third-user")
@Validated
public class TenantThirdUserController {

    @Resource
    private UserService userService;

    @PostMapping("/create")
    @Operation(summary = "新增三方用户")
    @PreAuthorize("@ss.hasPermission('tenant:third:create')")
    public CommonResult<Long> createUserAndUserAppRelation(@Valid @RequestBody ThirdUserAppCombinedInsertReqVO reqVO) {
        Long id = userService.thirdUserCreateUserAndUserAppRelation(reqVO);
        // TODO: 添加相关应用权限
        // TODO: 应用用户表添加相关数据
        return success(id);
    }


    @PostMapping("/update")
    @Operation(summary = "编辑三方用户")
    @PreAuthorize("@ss.hasPermission('tenant:third:update')")
    public CommonResult<Long> updateUserAndUserAppRelation(@Valid @RequestBody ThirdUserAppCombinedUpdateReqVO reqVO) {
        Long id = userService.thirdUserUpdateUserAndUserAppRelation(reqVO);
        return success(id);
    }

    @PostMapping("/update-password")
    @Operation(summary = "重置三方用户密码")
    @PreAuthorize("@ss.hasPermission('tenant:third:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody ThirdUserUpdatePasswordReqVO reqVO) {
        userService.thirdUserUpdatePassword(reqVO.getId());
        return success(true);
    }

    @PostMapping("/update-status")
    @Operation(summary = "修改三方用户状态")
    @PreAuthorize("@ss.hasPermission('tenant:third:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除三方用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:third:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @GetMapping("/user-applications-page")
    @Operation(summary = "获得三方用户(包含授权列表)-分页")
    @PreAuthorize("@ss.hasPermission('tenant:third:query')")
    public CommonResult<PageResult<UserApplicationRespVO>> getUserAppRelationPage(@Valid UserAppPageSearchReqVO userAppPageReqVO) {
        PageResult<UserApplicationRespVO> pageResult = userService.getUserAppRelationPage(userAppPageReqVO);
        return success(pageResult);
    }


    @GetMapping("/get")
    @Operation(summary = "获得三方用户详情(包含授权app)")
    @PreAuthorize("@ss.hasPermission('tenant:third:query')")
    public CommonResult<UserApplicationRespVO> getThirdUserAndRelationApp(@RequestParam("id") Long id) {
        UserApplicationRespVO userDetail = userService.getThirdUserAndRelationApp(id);
        return success(userDetail);
    }

}
