package com.cmsr.onebase.module.system.build.controller.tenant;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.vo.tenant.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 租户")
@RestController
@RequestMapping("/system/tenant")
@Component("oldTenantController")
public class TenantController {

    @Resource
    private TenantService tenantService;

    @GetMapping("/get")
    @Operation(summary = "获得租户(安全考虑仅获取用户所属租户)")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('tenant:space:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        return success(tenantService.getTenantWithAppCount(id));
    }

}

