package com.cmsr.onebase.plugin.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.build.service.PluginVersionService;
import com.cmsr.onebase.plugin.build.vo.req.PluginVersionUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginVersionUploadReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginVersionRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 插件版本管理 Controller
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Tag(name = "管理后台 - 插件版本管理")
@RestController
@RequestMapping("/plugin/version")
@Validated
public class PluginVersionController {

    @Resource
    private PluginVersionService pluginVersionService;

    @PostMapping("/upload")
    @Operation(summary = "上传新版本")
    @PreAuthorize("@ss.hasPermission('plugin:version:create')")
    public CommonResult<Long> uploadVersion(@Valid PluginVersionUploadReqVO uploadReqVO) {
        return success(pluginVersionService.uploadVersion(uploadReqVO));
    }

    @GetMapping("/list")
    @Operation(summary = "获取版本列表")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "test-plugin")
    @PreAuthorize("@ss.hasPermission('plugin:version:query')")
    public CommonResult<List<PluginVersionRespVO>> getVersionList(@RequestParam("pluginId") String pluginId) {
        return success(pluginVersionService.getVersionList(pluginId));
    }

    @PostMapping("/update")
    @Operation(summary = "更新版本信息（仅停用状态可更新）")
    @PreAuthorize("@ss.hasPermission('plugin:version:update')")
    public CommonResult<Boolean> updateVersion(@Valid PluginVersionUpdateReqVO updateReqVO) {
        pluginVersionService.updateVersion(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除版本（不可删除启用版本和唯一版本）")
    @Parameter(name = "id", description = "版本记录ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('plugin:version:delete')")
    public CommonResult<Boolean> deleteVersion(@RequestParam("id") Long id) {
        pluginVersionService.deleteVersion(id);
        return success(true);
    }

}
