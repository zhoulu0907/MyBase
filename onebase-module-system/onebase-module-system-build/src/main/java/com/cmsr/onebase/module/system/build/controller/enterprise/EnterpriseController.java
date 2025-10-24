package com.cmsr.onebase.module.system.build.controller.enterprise;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.enterprise.dto.*;
import com.cmsr.onebase.module.system.service.enterprise.EnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 企业 Controller
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Tag(name = "管理后台 - 企业")
@RestController
@RequestMapping("/system/enterprise/")
@Validated
public class EnterpriseController {

    @Resource
    private EnterpriseService enterpriseService;

    @PostMapping("create")
    @Operation(summary = "创建企业")
    @PreAuthorize("@ss.hasPermission('system:enterprise:create')")
    public CommonResult<Long> createEnterprise(@RequestBody @Valid EnterpriseSaveReqVO reqVO) {
        return success(enterpriseService.createEnterprise(reqVO));
    }


    @PostMapping("create_user")
    @Operation(summary = "创建企业管理员")
    @PermitAll
    public CommonResult<EnterpriseUserRespVO> createUser(@RequestBody @Valid EnterpriseUserReqVO reqVO) {
        EnterpriseUserRespVO user=enterpriseService.createUser(reqVO);
        return success(user);
    }

    @PostMapping("update")
    @Operation(summary = "更新企业")
    @PermitAll
    //@PreAuthorize("@ss.hasPermission('system:enterprise:update')")
    public CommonResult<Boolean> updateEnterprise(@RequestBody @Valid EnterpriseSaveReqVO reqVO) {
        enterpriseService.updateEnterprise(reqVO);
        return success(true);
    }

    @PostMapping("delete")
    @Operation(summary = "删除企业")
    @PreAuthorize("@ss.hasPermission('system:enterprise:delete')")
    public CommonResult<Boolean> deleteEnterprise(@RequestParam("id") Long id) {
        enterpriseService.deleteEnterprise(id);
        return success(true);
    }

    @PostMapping("update_status")
    @Operation(summary = "企业禁用")
    @PreAuthorize("@ss.hasPermission('system:enterprise:delete')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id,Long status) {
        enterpriseService.updateStatus(id,status);
        return success(true);
    }

    @GetMapping("page")
    @Operation(summary = "获得企业分页")
    @PreAuthorize("@ss.hasPermission('system:enterprise:query')")
    public CommonResult<PageResult<EnterpriseRespVO>> getEnterprisePage(EnterprisePageReqVO pageReqVO) {
        PageResult<EnterpriseRespVO> pageResult = enterpriseService.getEnterprisePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("enterprise_application_page")
    @Operation(summary = "获得企业应用列表")
    @PreAuthorize("@ss.hasPermission('system:enterprise:query')")
    public CommonResult<PageResult<EnterpriseApplicationRespVO>> enterpriseApplicationPage(ApplicationAuthEnterprisePageReqVO pageReqVO) {
        PageResult<EnterpriseApplicationRespVO> pageResult = enterpriseService.enterpriseApplicationPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("get")
    @Operation(summary = "获得企业详情")
    @PreAuthorize("@ss.hasPermission('system:enterprise:query')")
    public CommonResult<EnterpriseRespVO> getEnterprise(@RequestParam("id") Long id) {
        EnterpriseRespVO enterprise = enterpriseService.getEnterprise(id);
        return success(enterprise);
    }

}
