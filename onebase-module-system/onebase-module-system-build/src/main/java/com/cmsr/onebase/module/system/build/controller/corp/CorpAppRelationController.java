package com.cmsr.onebase.module.system.build.controller.corp;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import com.cmsr.onebase.module.system.vo.corp.CorpApplicationRespVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppPageReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationInertReqVO;
import com.cmsr.onebase.module.system.vo.corpapprelation.CorpAppRelationUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "平台服务-企业应用关系")
@RestController
@Validated
@RequestMapping("/system/corp-app-relation")
public class CorpAppRelationController {

    @Resource
    private CorpAppRelationService corpAppRelationService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('tenant:corp:create')")
    @Operation(summary = "新增企业应用关联")
    public CommonResult<Boolean> createCorpAppRelation(@Valid @RequestBody CorpAppRelationInertReqVO corpAppRelationInertReqVO) {
        corpAppRelationService.createCorpAppRelation(corpAppRelationInertReqVO);
        return success(true);
    }

    @PostMapping("/update")
    @PreAuthorize("@ss.hasPermission('tenant:corp:update')")
    @Operation(summary = "更新企业应用关联")
    public CommonResult<Boolean> updateCorpAppRelation(@Valid @RequestBody CorpAppRelationUpdateReqVO updateReqVO) {
        corpAppRelationService.updateCorpAppRelation(updateReqVO);
        return success(true);
    }

    @PostMapping("/update-status")
    @Operation(summary = "企业启用/禁用")
    @PreAuthorize("@ss.hasPermission('tenant:corp:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Long status) {
        corpAppRelationService.updateStatus(id, status);
        return success(true);
    }

    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission('tenant:corp:delete')")
    @Operation(summary = "删除应用授权企业")
    public CommonResult<Boolean> deleteCorpAppRelation(@RequestParam("id") Long id) {
        corpAppRelationService.deleteCorpAppRelation(id);
        return success(true);
    }

    @GetMapping("/corp-applications-page")
    @Operation(summary = "获得企业授权应用列表-分页")
    @PreAuthorize("@ss.hasPermission('tenant:corp:query')")
    public CommonResult<PageResult<CorpApplicationRespVO>> getCorpAppRelationPage(@Valid CorpAppPageReqVO corpAppPageReqVO) {
        PageResult<CorpApplicationRespVO> pageResult = corpAppRelationService.getCorpAppRelationPage(corpAppPageReqVO);
        return success(pageResult);
    }
}