package com.cmsr.onebase.module.system.build.controller.user;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "空间外部用户服务 - 用户")
@RestController
@RequestMapping("/system/third")
@Validated
public class TenantThirdUserController {

    @Resource
    private UserService userService;

    @GetMapping("/user-applications-page")
    @Operation(summary = "获得用户授权应用列表-分页")
    @PreAuthorize("@ss.hasPermission('tenant:user:query')")
    public CommonResult<PageResult<UserApplicationRespVO>> getUserAppRelationPage(@Valid UserAppPageReqVO userAppPageReqVO) {
        PageResult<UserApplicationRespVO> pageResult = userService.getUserAppRelationPage(userAppPageReqVO);
        return success(pageResult);
    }


    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:create')")
    public CommonResult<Long> createUserAndUserAppRelation(@Valid @RequestBody ThirdUserAppCombinedInsertReqVO reqVO) {
        Long id = userService.createUserAndUserAppRelation(reqVO);
        return success(id);
    }


    @PostMapping("/update")
    @Operation(summary = "编辑用户")
    @PreAuthorize("@ss.hasPermission('tenant:user:create')")
    public CommonResult<Long> updateUserAndUserAppRelation(@Valid @RequestBody ThirdUserAppCombinedUpdateReqVO reqVO) {
        Long id = userService.updateUserAndUserAppRelation(reqVO);
        return success(id);
    }


    @PostMapping("/forget-password")
    @Operation(summary = "忘记密码")
    @PreAuthorize("@ss.hasPermission('tenant:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserForgetPasswordReqVO reqVO) {
        userService.forgetPassword(reqVO);
        return success(true);
    }





}
