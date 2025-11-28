package com.cmsr.onebase.module.system.runtime.controller.auth;


import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.runtime.service.auth.RuntimeAuthService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.vo.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 运行态登录认证相关服务
 * 1. AppLogin 应用登录-内部用户登录（内部模式，账密登录）
 * 2. AppLogin 应用登录-外部用户登录（SaaS模式，手机号登录）
 *
 * @author matianyu
 * @date 2025-11
 */
@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class RuntimeAuthController {

    @Resource
    private RuntimeAuthService runtimeAuthService;

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private PermissionService permissionService;

    @PostMapping("/app-login")
    @PermitAll
    @Operation(summary = "内部用户登录（Inner模式，账密登录）")
    public CommonResult<AuthLoginRespVO> appUsernameLogin(@RequestBody @Valid AppUserNameLoginReqVO reqVO) {
        return success(runtimeAuthService.appUsernameLogin(reqVO));
    }


    @PostMapping("/app-login-mobile")
    @PermitAll
    @Operation(summary = "外部用户登录（SaaS模式，手机号登录）")
    public CommonResult<AuthLoginRespVO> appMobileLogin(@RequestBody @Valid AppMobileLoginReqVO reqVO) {
        return success(runtimeAuthService.appMobileLogin(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            runtimeAuthService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(runtimeAuthService.refreshToken(refreshToken));
    }


    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "注册用户")
    public CommonResult<AuthLoginRespVO> register(@RequestBody @Valid AuthRegisterReqVO registerReqVO) {
        return success(runtimeAuthService.register(registerReqVO));
    }

    @PostMapping("/reset-password")
    @PermitAll
    @Operation(summary = "重置密码")
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid AuthResetPasswordReqVO reqVO) {
        runtimeAuthService.resetPassword(reqVO);
        return success(true);
    }

    @GetMapping("/get-permission-info")
    @Operation(summary = "获取登录用户的权限信息")
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo(@RequestParam(value = "code", required = false) String code) {
        return success(permissionService.getPermissionInfo(code));
    }
}