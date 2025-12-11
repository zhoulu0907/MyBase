package com.cmsr.onebase.module.infra.runtime.controller.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.vo.security.*;
import com.cmsr.onebase.module.infra.service.security.SecurityConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
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
public class RuntimeSecurityConfigController {

    @Resource
    private SecurityConfigService securityConfigService;

    @PostMapping("/get-tenant-items")
    @Operation(summary = "根据分类IDS获取获取租户的安全配置项（优先通过AppId获取租户ID）")
    @PermitAll
    @TenantIgnore
    public CommonResult<List<SecurityConfigCategoryGroupRespVO>> getTenantConfigItems(@RequestBody SecurityConfigGetReqVO getReqVO) {
        List<SecurityConfigCategoryGroupRespVO> items = securityConfigService.getTenantConfigItemsByCategoryCodes(getReqVO);
        return success(items);
    }
}