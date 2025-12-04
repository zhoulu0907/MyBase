package com.cmsr.onebase.module.infra.runtime.controller.security;

import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigBatchUpdateReqVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigCategoryRespVO;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.service.security.SecurityConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class SecurityConfigController {

    @Resource
    private SecurityConfigService securityConfigService;


    @PostMapping("/check-login-captcha")
    @Operation(summary = "检查登录验证码")
    @PermitAll
    public CommonResult<Boolean> checkLoginCaptcha() {
        return success(securityConfigService.checkLoginCaptcha());
    }
}