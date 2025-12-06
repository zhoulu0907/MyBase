package com.cmsr.onebase.module.infra.platform.controller.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigReqVO;
import com.cmsr.onebase.module.infra.service.security.SecurityConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/check-scenarios-captcha")
    @Operation(summary = "检查不同场景是否启用验证码", description = "返回值true 需要验证码,false 不需要")
    @PermitAll
    @TenantIgnore
    public CommonResult<Boolean> checkScenariosCaptcha(SecurityConfigReqVO configReqVO) {
        return success(securityConfigService.checkScenariosCaptcha(configReqVO));
    }
}