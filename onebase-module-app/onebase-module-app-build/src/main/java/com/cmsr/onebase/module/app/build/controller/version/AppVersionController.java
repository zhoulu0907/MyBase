package com.cmsr.onebase.module.app.build.controller.version;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.build.service.version.AppVersionService;
import com.cmsr.onebase.module.app.build.vo.version.ExportPageReqVO;
import com.cmsr.onebase.module.app.build.vo.version.ExportPageRespVO;
import com.cmsr.onebase.module.app.build.vo.version.VersionImportReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionOnlineReq;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageReqVo;
import com.cmsr.onebase.module.app.build.vo.version.VersionPageRespVO;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Author：huangjie
 *                  @Date：2025/7/22 16:32
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "版本导入")
    public CommonResult<Boolean> importApplicationVersion(@Validated VersionImportReq versionImportReq) {
        appVersionService.importApplicationVersion(versionImportReq);
        return CommonResult.success(true);
    }

    @GetMapping("/export")
    @Operation(summary = "导出应用")
    @PreAuthorize("@ss.hasPermission('tenant:app:query')")
    public CommonResult<Long> exportApplication(@RequestParam("versionId") Long versionId,
            @RequestParam("applicationId") Long applicationId) {
        Long exportId = appVersionService.exportApplicationVersion(versionId, applicationId);
        return CommonResult.success(exportId);
    }

    @PostMapping("/export/retry")
    @Operation(summary = "重试导出应用")
    @PreAuthorize("@ss.hasPermission('tenant:app:query')")
    public CommonResult<Long> retryExportApplication(@RequestParam("exportId") Long exportId,
            @RequestParam("applicationId") Long applicationId) {
        Long retryExportId = appVersionService.retryExportApplication(exportId, applicationId);
        return CommonResult.success(retryExportId);
    }

    @GetMapping("/export/status")
    @Operation(summary = "获取导出应用状态")
    @PreAuthorize("@ss.hasPermission('tenant:app:query')")
    public CommonResult<Integer> getExportApplicationStatus(@RequestParam("exportId") Long id) {
        Integer status = appVersionService.getExportStatus(id);
        return CommonResult.success(status);
    }

    @GetMapping("/export/file")
    @Operation(summary = "获取导出应用文件")
    @PreAuthorize("@ss.hasPermission('tenant:app:query')")
    public void getExportApplicationResource(@RequestParam("exportId") Long id,
            HttpServletRequest request, HttpServletResponse response) {
        appVersionService.getExportApplicationResource(id, request, response);
    }

    @GetMapping("/export/page")
    @Operation(summary = "分页查询导出记录")
    @PreAuthorize("@ss.hasPermission('tenant:app:query')")
    public CommonResult<PageResult<ExportPageRespVO>> getExportPage(@Validated ExportPageReqVO pageReqVO) {
        return CommonResult.success(appVersionService.getExportPage(pageReqVO));
    }

    @PostMapping("/export/delete")
    @Operation(summary = "删除导出记录")
    @PreAuthorize("@ss.hasPermission('tenant:app:delete')")
    public CommonResult<Boolean> deleteExportRecord(@RequestParam("exportId") Long exportId) {
        appVersionService.deleteExportRecord(exportId);
        return CommonResult.success(true);
    }

}
