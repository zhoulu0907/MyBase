package com.cmsr.onebase.module.system.runtime.controller.corp.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.system.service.corpapprelation.CorpAppRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

@Tag(name = "企业服务 - 企业应用关系")
@RestController
@Validated
@RequestMapping("/corp/corp-app-relation")
public class CorpAppRelationController {

    @Resource
    private CorpAppRelationService corpAppRelationService;


    // @GetMapping("/corp-applications-page")
    // @Operation(summary = "获得企业授权应用列表-分页")
    // @PreAuthorize("@ss.hasPermission('corp:app-auth:query')")
    // public CommonResult<PageResult<CorpApplicationRespVO>> getCorpAppRelationPage(@Valid CorpAppPageReqVO corpAppPageReqVO) {
    //     PageResult<CorpApplicationRespVO> pageResult = corpAppRelationService.getCorpAppRelationPage(corpAppPageReqVO);
    //     return success(pageResult);
    // }


    @PostMapping("/update-status")
    @Operation(summary = "企业启用/禁用")
    @PreAuthorize("@ss.hasPermission('corp:app-auth:enable')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Long status) {
        corpAppRelationService.updateStatus(id, status);
        return success(true);
    }

}