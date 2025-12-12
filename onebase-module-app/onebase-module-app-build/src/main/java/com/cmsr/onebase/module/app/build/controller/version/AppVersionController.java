package com.cmsr.onebase.module.app.build.controller.version;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.service.version.AppVersionService;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:32
 */
@Tag(name = "应用管理-版本管理")
@RestController
@RequestMapping("/app/version")
@Validated
public class AppVersionController {

    @Resource
    private AppVersionService appVersionService;

    @GetMapping("/page")
    @Operation(summary = "应用版本列表")
    public CommonResult<PageResult<VersionPageRespVO>> pageApplicationVersion(@Validated VersionPageReqVo reqVo) {
        return CommonResult.success(appVersionService.getApplicationVersionPage(reqVo));
    }

    @PostMapping("/online")
    @Operation(summary = "发布应用")
    public CommonResult<Boolean> onlineApplicationVersion(@RequestBody VersionOnlineReq createReqVO) {
        appVersionService.onlineApplication(createReqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/offline")
    @Operation(summary = "下架应用")
    public CommonResult<Boolean> offlineApplicationVersion(@RequestBody JsonNode offlineVO) {
        appVersionService.offlineApplication();
        return CommonResult.success(true);
    }

    @PostMapping("/restore")
    @Operation(summary = "应用版本启用")
    @Parameter(name = "applicationId", description = "应用id", required = true)
    public CommonResult<Boolean> restoreApplicationVersion(@RequestParam("versionId") Long versionId) {
        appVersionService.restoreApplicationVersion(versionId);
        return CommonResult.success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用版本")
    @Parameter(name = "applicationId", description = "应用id", required = true)
    public CommonResult<Boolean> deleteApplicationVersion(@RequestParam("versionId") Long versionId) {
        appVersionService.deleteApplicationVersion(versionId);
        return CommonResult.success(true);
    }

}
