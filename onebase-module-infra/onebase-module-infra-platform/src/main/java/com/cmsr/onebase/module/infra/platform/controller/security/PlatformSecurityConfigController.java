package com.cmsr.onebase.module.infra.platform.controller.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.service.security.SecurityConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 安全配置控制器
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Tag(name = "管理后台 - 安全配置")
@RestController
@RequestMapping("/infra/security-config")
@Validated
public class PlatformSecurityConfigController {

    @Resource
    private SecurityConfigService securityConfigService;


    @PostMapping("/check-login-captcha")
    @Operation(summary = "检查登录验证码", description = "返回值true 需要验证码,false 不需要")
    @PermitAll
    public CommonResult<Boolean> checkLoginCaptcha() {
        return success(securityConfigService.checkLoginCaptcha());
    }
}