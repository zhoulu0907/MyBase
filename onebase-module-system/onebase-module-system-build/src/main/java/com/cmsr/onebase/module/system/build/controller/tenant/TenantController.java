package com.cmsr.onebase.module.system.build.controller.tenant;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.convert.tenant.TenantConvert;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.vo.tenant.TenantRespVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantSimpleRespVO;
import com.cmsr.onebase.module.system.vo.tenant.TenantUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - 租户")
@RestController
@RequestMapping("/system/tenant")
@Component("oldTenantController")
public class TenantController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/get-id-by-name")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用租户名，获得租户编号", description = "登录界面，根据用户的租户名，获得租户编号")
    @Parameter(name = "name", description = "租户名", required = true, example = "1024")
    public CommonResult<Long> getTenantIdByName(@RequestParam("name") String name) {
        TenantDO tenant = tenantService.getTenantByName(name);
        return success(tenant != null ? tenant.getId() : null);
    }

    @GetMapping({"simple-list"})
    @PermitAll
    @TenantIgnore
    @Operation(summary = "获取租户精简信息列表", description = "只包含被开启的租户，用于【首页】功能的选择租户选项")
    public CommonResult<List<TenantRespVO>> getTenantSimpleList() {
        List<TenantDO> list = tenantService.getTenantListByStatus(CommonStatusEnum.ENABLE.getStatus());
        return success(convertList(list, tenantDO ->
                new TenantRespVO().setId(tenantDO.getId()).setName(tenantDO.getName())));
    }

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用域名，获得租户信息", description = "登录界面，根据用户的域名，获得租户信息")
    @Parameter(name = "website", description = "域名", required = true, example = "onebase")
    public CommonResult<TenantSimpleRespVO> getTenantByWebsite(@RequestParam("website") String website) {
        TenantDO tenant = tenantService.getTenantByWebsite(website);
        if (tenant == null || CommonStatusEnum.isDisable(tenant.getStatus())) {
            return success(null);
        }
        return success(TenantConvert.INSTANCE.convertToSimpleRespVO(tenant));
    }

    @PostMapping("/update")
    @Operation(summary = "更新租户")
    @PreAuthorize("@ss.hasPermission('tenant:info:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantUpdateReqVO updateReqVO) {
        tenantService.updateTenant(updateReqVO);
        return success(true);
    }


    @GetMapping("/get")
    @Operation(summary = "获得租户(安全考虑仅获取用户所属租户)")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:info:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        return success(tenantService.getTenantWithAppCount(id));
    }

    @GetMapping("/get-simple-tenant-by-id")
    @Operation(summary = "获得租户(免登录)")
    @PermitAll
    @TenantIgnore
    public CommonResult<TenantSimpleRespVO> getSimpleTenantById(@RequestParam(value = "id") Long id) {
        return success( BeanUtils.toBean(tenantService.getTenant(id), TenantSimpleRespVO.class));
    }

}

