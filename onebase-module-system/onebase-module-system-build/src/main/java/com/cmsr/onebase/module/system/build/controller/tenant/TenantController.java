package com.cmsr.onebase.module.system.build.controller.tenant;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.excel.core.util.ExcelUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.convert.tenant.TenantConvert;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.vo.tenant.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @PostMapping("/create")
    @Operation(summary = "创建租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<Long> createTenant(@Valid @RequestBody TenantInsertReqVO createReqVO) {
        return success(tenantService.createTenant(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantUpdateReqVO updateReqVO) {
        tenantService.updateTenant(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantService.deleteTenant(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户(安全考虑仅获取用户所属租户)")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        // if(null == id ){
        //     id= TenantContextHolder.getTenantId();
        // }
        return success(tenantService.getTenantWithAppCount(id));
    }

    @GetMapping("/get-allocatable-count")
    @Operation(summary = "获得租户可分配数量")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<Long> getAllocatableCount() {
        Long accountCount = tenantService.getAvailableAccountCount();
        return success(accountCount);
    }

    @GetMapping("/get-other-exist-user-count")
    @Operation(summary = "获得其他已有的用户数量和")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<Long> getOtherTenantUserCount(@RequestParam(required = false) Long id) {
        Long accountCount = tenantService.getOtherTenantUserLimitCount(id);
        return success(accountCount);
    }

    @GetMapping("/get-tenant-exist-user-count")
    @Operation(summary = "获得当前已有的用户数量和")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<Long> getTenantExistUserCount(@RequestParam Long id) {
        Long userCount = tenantService.getTenantExistUserCount(id);
        return success(userCount);
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户分页")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<PageResult<TenantRespVO>> getTenantPage(@Valid TenantPageReqVO pageVO) {
        PageResult<TenantRespVO> pageResult = tenantService.getTenantPage(pageVO);
        return success(BeanUtils.toBean(pageResult, TenantRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租户 Excel")
    @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    public void exportTenantExcel(@Valid TenantPageReqVO exportReqVO, HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TenantRespVO> list = tenantService.getTenantPage(exportReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "租户.xls", "数据", TenantRespVO.class,
                BeanUtils.toBean(list, TenantRespVO.class));
    }

}

