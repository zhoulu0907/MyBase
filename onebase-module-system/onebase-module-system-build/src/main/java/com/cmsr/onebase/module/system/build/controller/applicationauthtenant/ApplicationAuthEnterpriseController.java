package com.cmsr.onebase.module.system.build.controller.applicationauthtenant;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import com.cmsr.onebase.module.system.service.applicationauthtenant.ApplicationAuthEnterpriseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;



import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "应用授权企业表")
@RestController
@Validated
@RequestMapping("/system/application-auth-enterprise")
public class ApplicationAuthEnterpriseController   {

    @Resource
    private ApplicationAuthEnterpriseService applicationAuthEnterpriseService;

    @PostMapping("/create")
    @PermitAll
    @Operation(summary = "创建应用授权企业表")
    public CommonResult<Long> createApplicationAuthEnterprise(@Valid @RequestBody ApplicationAuthEnterpriseSaveReqVO createReqVO) {
        return success(applicationAuthEnterpriseService.createApplicationAuthEnterprise(createReqVO));
    }

    @PutMapping("/update")
    @PermitAll
    @Operation(summary = "更新应用授权企业表")
    public CommonResult<Boolean> updateApplicationAuthEnterprise(@Valid @RequestBody ApplicationAuthEnterpriseSaveReqVO updateReqVO) {
        applicationAuthEnterpriseService.updateApplicationAuthEnterprise(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用授权企业表")
    public CommonResult<Boolean> deleteApplicationAuthEnterprise(@RequestParam("id") Long id) {
        applicationAuthEnterpriseService.deleteApplicationAuthEnterprise(id);
        return success(true);
    }

    @GetMapping("/get")
    @PermitAll
    @Operation(summary = "获得应用授权企业表")
    public CommonResult<ApplicationAuthEnterpriseVO> getApplicationAuthEnterprise(@RequestParam("id") Long id) {
        return success(applicationAuthEnterpriseService.getApplicationAuthEnterprise(id));
    }

    @GetMapping("/page")
    @PermitAll
    @Operation(summary = "获得应用授权企业表分页")
    public CommonResult<PageResult<ApplicationAuthEnterpriseVO>> getApplicationAuthEnterprisePage(@Valid ApplicationAuthEnterprisePageReqVO pageReqVO) {
        PageResult<ApplicationAuthEnterpriseVO> pageResult = applicationAuthEnterpriseService.getApplicationAuthEnterprisePage(pageReqVO);
        return success(pageResult);
    }
}