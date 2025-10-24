package com.cmsr.onebase.module.system.api.applicationauthtenant;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;

import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterprisePageReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseSaveReqVO;
import com.cmsr.onebase.module.system.api.applicationauthtenant.dto.ApplicationAuthEnterpriseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



@Tag(name = "应用授权企业表 API")
@RestController
@RequestMapping("/system/corp-and-app-relation")
public interface CorpAndAppRelationApi {

    @PostMapping("/create")
    @Operation(summary = "创建应用授权企业表")
    CommonResult<Long> createCorpAppRelation(@Valid @RequestBody ApplicationAuthEnterpriseSaveReqVO createReqVO);

    @PutMapping("/update")
    @Operation(summary = "更新应用授权企业表")
    CommonResult<Boolean> updateApplicationAuthEnterprise(@Valid @RequestBody ApplicationAuthEnterpriseSaveReqVO updateReqVO);

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用授权企业表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    CommonResult<Boolean> deleteApplicationAuthEnterprise(@RequestParam("id") Long id);

    @GetMapping("/get")
    @Operation(summary = "获得应用授权企业表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    CommonResult<ApplicationAuthEnterpriseVO> getApplicationAuthEnterprise(@RequestParam("id") Long id);

    @GetMapping("/page")
    @Operation(summary = "获得应用授权企业表分页")
    CommonResult<PageResult<ApplicationAuthEnterpriseVO>> getApplicationAuthEnterprisePage(@Valid ApplicationAuthEnterprisePageReqVO pageReqVO);
}