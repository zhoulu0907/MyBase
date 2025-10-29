package com.cmsr.onebase.module.system.build.controller.corp;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.api.corpapprelation.dto.CorpAppRelationPageReqVO;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.vo.corp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
@RequestMapping("/system/corp/")
@Validated
public class CorpController {

    @Resource
    private CorpService corpService;

    @PostMapping("create")
    @Operation(summary = "创建企业")
    @PreAuthorize("@ss.hasPermission('system:corp:create')")
    public CommonResult<CorpUserRespVO> createCorpCombined(@RequestBody @Valid CorpCombinedVo reqVO) {
        return success(corpService.createCorpCombined(reqVO));
    }


    @PostMapping("update")
    @Operation(summary = "更新企业")
    @PreAuthorize("@ss.hasPermission('system:corp:update')")
    public CommonResult<Boolean> updateCorp(@RequestBody @Valid CorpUpdateReqVO reqVO) {
        corpService.updateCorp(reqVO);
        return success(true);
    }

    @PostMapping("delete")
    @Operation(summary = "删除企业")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:corp:delete')")
    public CommonResult<Boolean> deleteCorp(@RequestParam("id") Long id) {
        corpService.deleteCorp(id);
        return success(true);
    }

    @PostMapping("update_status")
    @Operation(summary = "企业禁用")
    @PreAuthorize("@ss.hasPermission('system:corp:delete')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id,Long status) {
        corpService.updateStatus(id,status);
        return success(true);
    }

    @GetMapping("page")
    @Operation(summary = "获得企业分页")
    @PreAuthorize("@ss.hasPermission('system:corp:query')")
    public CommonResult<PageResult<CorpRespVO>> getCorpPage(CorpPageReqVO pageReqVO) {
        PageResult<CorpRespVO> pageResult = corpService.getCorpPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("corp_application_page")
    @Operation(summary = "获得企业应用列表")
    @PreAuthorize("@ss.hasPermission('system:corp:query')")
    public CommonResult<PageResult<CorpApplicationRespVO>> corpApplicationPage(CorpAppRelationPageReqVO pageReqVO) {
        PageResult<CorpApplicationRespVO> pageResult = corpService.selectCorpAppRelationPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("get")
    @Operation(summary = "获得企业详情")
    @PreAuthorize("@ss.hasPermission('system:corp:query')")
    public CommonResult<CorpRespVO> getCorp(@RequestParam("id") Long id) {
        CorpRespVO corp = corpService.getCorp(id);
        return success(corp);
    }

}
