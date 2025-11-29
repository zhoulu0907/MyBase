package com.cmsr.onebase.module.app.runtime.controller.resource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.ListPageSetRespVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.core.vo.resource.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.runtime.service.resource.PageSetService;
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
