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
@RequestMapping("/corp/corp-app-relation")
public class CorpAppRelationCorpController {

    @Resource
    private CorpAppRelationService corpAppRelationService;


    @GetMapping("/corp-applications-page")
    @Operation(summary = "获得企业授权应用列表-分页")
    @PreAuthorize("@ss.hasPermission('corp:app-auth:query')")
    public CommonResult<PageResult<CorpApplicationRespVO>> getCorpAppRelationPage(@Valid CorpAppPageReqVO corpAppPageReqVO) {
        PageResult<CorpApplicationRespVO> pageResult = corpAppRelationService.getCorpAppRelationPage(corpAppPageReqVO);
        return success(pageResult);
    }
}