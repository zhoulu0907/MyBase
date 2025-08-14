package com.cmsr.onebase.module.app.controller.admin.appresource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.api.appresource.dto.CopyPageSetDTO;
import com.cmsr.onebase.module.app.api.appresource.dto.CreatePageSetDTO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.DeletePageSetReqVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetReqVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.LoadPageSetRespVO;
import com.cmsr.onebase.module.app.controller.admin.appresource.vo.SavePageSetReqVO;
import com.cmsr.onebase.module.app.service.appresource.PageSetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

@Tag(name = "应用资源管理-页面集管理")
@RestController
@RequestMapping("/app/resource/page_set")
@Validated
public class PageSetController {

    @Resource
    private PageSetService pageSetService;

    @GetMapping("/code")
    @Operation(summary = "获取页面集编码")
    public CommonResult<String> getPageSetCode(@RequestParam String menuCode) {

        String pageSetCode = pageSetService.getPageSetCode(menuCode);

        return CommonResult.success(pageSetCode);
    }

    @GetMapping("/app_id")
    @Operation(summary = "获取页面集应用ID")
    public CommonResult<Long> getAppId(@RequestParam String code) {
        Long appId = pageSetService.getAppId(code);
        return CommonResult.success(appId);
    }

    @GetMapping("/main_metadata")
    public CommonResult<String> getMainMetadata(@RequestParam String code) {
        String mainMetadata = pageSetService.getMainMetadata(code);
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
        pageSetService.deletePageSet(deletePageSetReqVO.getMenuCode());
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
}
