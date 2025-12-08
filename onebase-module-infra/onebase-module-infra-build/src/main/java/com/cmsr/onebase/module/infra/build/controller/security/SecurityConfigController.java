package com.cmsr.onebase.module.infra.build.controller.security;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.vo.security.*;
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

    @Resource
    private SecurityConfigApi securityConfigApi;

    @GetMapping("/categories")
    @Operation(summary = "获取所有安全配置分类")
    @PreAuthorize("@ss.hasPermission('tenant:security:query')")
    public CommonResult<List<SecurityConfigCategoryRespVO>> getAllCategories() {
        List<SecurityConfigCategoryRespVO> categories = securityConfigService.getAllCategories();
        return success(categories);
    }

    @GetMapping("/items")
    @Operation(summary = "根据分类ID获取获取当前租户的安全配置项")
    @Parameter(name = "categoryId", description = "分类ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('tenant:security:query')")
    public CommonResult<List<SecurityConfigItemRespVO>> getTenantConfigItems(@RequestParam("categoryId") Long categoryId) {
        Long tenantId = TenantContextHolder.getTenantId();
        List<SecurityConfigItemRespVO> items = securityConfigService.getTenantConfigItems(tenantId, categoryId);
        return success(items);
    }


    @PostMapping("/get-tenant-items")
    @Operation(summary = "根据分类IDS获取获取租户的安全配置项（优先通过AppId获取租户ID）")
    @PermitAll
    @TenantIgnore
    public CommonResult<List<SecurityConfigCategoryGroupRespVO>> getTenantConfigItems( @RequestBody SecurityConfigGetReqVO getReqVO) {
         List<SecurityConfigCategoryGroupRespVO> items = securityConfigService.getTenantConfigItemsByCategoryCodes(getReqVO);
        return success(items);
    }

    // @GetMapping("/tenant-items")
    // @Operation(summary = "获取当前租户的所有安全配置项")
    // @PreAuthorize("@ss.hasPermission('tenant:security:query')")
    // public CommonResult<List<SecurityConfigItemRespVO>> getSecurityConfigsByTenant() {
    //     Long tenantId = TenantContextHolder.getTenantId();
    //     List<SecurityConfigItemRespVO> items = securityConfigService.getSecurityConfigsByTenant(tenantId);
    //     return success(items);
    // }

    @PostMapping("/batch-update")
    @Operation(summary = "批量更新租户安全配置")
    @PreAuthorize("@ss.hasPermission('tenant:security:update')")
    public CommonResult<Boolean> batchUpdateConfig(@Valid @RequestBody SecurityConfigBatchUpdateReqVO batchUpdateReqVO) {
        Long tenantId = TenantContextHolder.getTenantId();
        securityConfigService.batchUpdateConfig(tenantId, batchUpdateReqVO.getConfigs());
        return success(true);
    }

    @PostMapping("/weak-password/check")
    @Operation(summary = "弱密码校验")
    @PreAuthorize("@ss.hasPermission('tenant:security:query')")
    public CommonResult<Boolean> checkWeakPassword(@RequestParam("password") String password) {
        securityConfigApi.validatePassword(password);
        return success(true);
    }
}