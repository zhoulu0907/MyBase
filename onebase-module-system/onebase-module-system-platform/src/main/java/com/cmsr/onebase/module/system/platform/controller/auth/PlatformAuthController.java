package com.cmsr.onebase.module.system.platform.controller.auth;


import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.security.config.SecurityProperties;
import com.cmsr.onebase.framework.security.core.util.SecurityFrameworkUtils;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.service.auth.AdminAuthService;
import com.cmsr.onebase.module.system.vo.auth.RuntimeAuthLoginReqVO;
import com.cmsr.onebase.module.system.vo.auth.AuthLoginRespVO;
import com.cmsr.onebase.module.system.vo.auth.AuthRegisterReqVO;
import com.cmsr.onebase.module.system.vo.auth.AuthResetPasswordReqVO;
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
 * 平台登录认证相关服务
 * 1. 平台用户登录，超级管理员
 *
 * @author matianyu
 * @date 2025-11
 */
@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class PlatformAuthController {

    @Resource
    private AdminAuthService authService;

    @Resource
    private SecurityProperties securityProperties;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid RuntimeAuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }


    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "注册用户")
    public CommonResult<AuthLoginRespVO> register(@RequestBody @Valid AuthRegisterReqVO registerReqVO) {
        return success(authService.register(registerReqVO));
    }

    @PostMapping("/reset-password")
    @PermitAll
    @Operation(summary = "重置密码")
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid AuthResetPasswordReqVO reqVO) {
        authService.resetPassword(reqVO);
        return success(true);
    }
}