package com.cmsr.onebase.module.app.build.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.build.service.resource.PageSetService;
import com.cmsr.onebase.module.app.core.dto.appresource.CopyPageSetDTO;
import com.cmsr.onebase.module.app.core.dto.appresource.CreatePageSetDTO;
import com.cmsr.onebase.module.app.core.vo.resource.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "应用资源管理-页面集管理")
@RestController
@RequestMapping("/app/resource/page_set")
@Validated
public class PageSetController {

    @Resource
    private PageSetService pageSetService;

    @GetMapping("/id")
    @Operation(summary = "获取页面集id")
    public CommonResult<Long> getPageSetCode(@RequestParam Long menuId) {

        Long pageSetId = pageSetService.getPageSetId(menuId);

        return CommonResult.success(pageSetId);
    }

    @GetMapping("/app_id")
    @Operation(summary = "获取页面集应用ID")
    public CommonResult<Long> getAppId(@RequestParam Long pageSetId) {
        Long appId = pageSetService.getAppId(pageSetId);
        return CommonResult.success(appId);
    }

    @GetMapping("/main_metadata")
    public CommonResult<String> getMainMetadata(@RequestParam Long pageSetId) {
        String mainMetadata = pageSetService.getMainMetadata(pageSetId);
        return CommonResult.success(mainMetadata);
    }

    @PostMapping("/create")
    @Operation(summary = "创建页面集")
    public CommonResult<String> createPageSet(@RequestBody CreatePageSetDTO createPageSetDTO) {

        String pageSetCode = pageSetService.createPageSet(createPageSetDTO);

        return CommonResult.success(pageSetCode);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制页面集")
    public CommonResult<String> copyPageSet(@RequestBody CopyPageSetDTO copyPageSetDTO) {

        String pageSetCode = pageSetService.copyPageSet(copyPageSetDTO);

        return CommonResult.success(pageSetCode);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除页面集")
    public CommonResult<Boolean> deletePageSet(@RequestBody DeletePageSetReqVO deletePageSetReqVO) {
        pageSetService.deletePageSet(deletePageSetReqVO.getMenuId());
        return CommonResult.success(true);
    }

    @PostMapping("/save")
    @Operation(summary = "保存页面集")
    public CommonResult<Boolean> savePageSet(@RequestBody SavePageSetReqVO savePageSetReqVO) {

        pageSetService.savePageSet(savePageSetReqVO);

        return CommonResult.success(true);
    }

    @PostMapping("/load")
    @Operation(summary = "载入页面集")
    public CommonResult<LoadPageSetRespVO> loadPageSet(@RequestBody LoadPageSetReqVO loadPageSetReqVO) {

        LoadPageSetRespVO loadPageSetRespVO = pageSetService.loadPageSet(loadPageSetReqVO);

        return CommonResult.success(loadPageSetRespVO);
    }

    @GetMapping("/list")
    @Operation(summary = "查询页面集列表")
    public CommonResult<ListPageSetRespVO> listPageSet(@Valid ListPageSetReqVO listPageSetReqVO) {

        ListPageSetRespVO listPageSetRespVO = pageSetService.listPageSet(listPageSetReqVO);

        return CommonResult.success(listPageSetRespVO);
    }
}
