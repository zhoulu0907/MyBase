package com.cmsr.onebase.plugin.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.plugin.build.service.PluginInfoService;
import com.cmsr.onebase.plugin.build.vo.req.PluginInfoPageReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginInfoUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.req.PluginUploadReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginInfoDetailRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginInfoRespVO;
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
 * 插件信息管理 Controller
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Tag(name = "管理后台 - 插件管理")
@RestController
@RequestMapping("/plugin/info")
@Validated
public class PluginInfoController {

    @Resource
    private PluginInfoService pluginInfoService;

    @PostMapping("/upload")
    @Operation(summary = "上传插件（首次上传）")
    @PreAuthorize("@ss.hasPermission('plugin:info:create')")
    public CommonResult<String> uploadPlugin(@Valid PluginUploadReqVO uploadReqVO) {
        return success(pluginInfoService.uploadPlugin(uploadReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取插件详情（含版本列表）")
    @Parameter(name = "id", description = "版本记录ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('plugin:info:query')")
    public CommonResult<PluginInfoDetailRespVO> getPluginDetail(@RequestParam("id") Long id) {
        return success(pluginInfoService.getPluginDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询插件列表")
    @PreAuthorize("@ss.hasPermission('plugin:info:query')")
    public CommonResult<PageResult<PluginInfoRespVO>> getPluginPage(@Valid PluginInfoPageReqVO pageReqVO) {
        return success(pluginInfoService.getPluginPage(pageReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新插件基础信息（对所有版本生效）")
    @PreAuthorize("@ss.hasPermission('plugin:info:update')")
    public CommonResult<Boolean> updatePluginInfo(@Valid @RequestBody PluginInfoUpdateReqVO updateReqVO) {
        pluginInfoService.updatePluginInfo(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除插件及其所有版本")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "test-plugin")
    @PreAuthorize("@ss.hasPermission('plugin:info:delete')")
    public CommonResult<Boolean> deletePlugin(@RequestParam("pluginId") String pluginId) {
        pluginInfoService.deletePlugin(pluginId);
        return success(true);
    }

    @PostMapping("/enable")
    @Operation(summary = "启用插件版本")
    @Parameter(name = "id", description = "版本记录ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('plugin:info:update')")
    public CommonResult<Boolean> enablePlugin(@RequestParam("id") Long id) {
        pluginInfoService.enablePlugin(id);
        return success(true);
    }

    @PostMapping("/disable")
    @Operation(summary = "禁用插件版本")
    @Parameter(name = "id", description = "版本记录ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('plugin:info:update')")
    public CommonResult<Boolean> disablePlugin(@RequestParam("id") Long id) {
        pluginInfoService.disablePlugin(id);
        return success(true);
    }

}
