package com.cmsr.onebase.plugin.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.build.service.PluginConfigService;
import com.cmsr.onebase.plugin.build.vo.req.PluginConfigUpdateReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginPackageRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 插件配置管理 Controller
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Tag(name = "管理后台 - 插件配置管理")
@RestController
@RequestMapping("/plugin/config")
@Validated
public class PluginConfigController {

    @Resource
    private PluginConfigService pluginConfigService;

    @GetMapping("/list")
    @Operation(summary = "获取配置列表")
    @Parameters({
            @Parameter(name = "pluginId", description = "插件ID", required = true, example = "1024"),
            @Parameter(name = "pluginVersion", description = "插件版本", required = true, example = "1.0.0")
    })
    @PreAuthorize("@ss.hasPermission('plugin:config:query')")
    public CommonResult<List<PluginConfigRespVO>> getConfigList(
            @RequestParam("pluginId") Long pluginId,
            @RequestParam("pluginVersion") String pluginVersion) {
        return success(pluginConfigService.getConfigList(pluginId, pluginVersion));
    }

    @PostMapping("/update")
    @Operation(summary = "批量更新配置")
    @PreAuthorize("@ss.hasPermission('plugin:config:update')")
    public CommonResult<Boolean> updateConfigs(@Valid @RequestBody List<PluginConfigUpdateReqVO> updateReqVOList) {
        pluginConfigService.updateConfigs(updateReqVOList);
        return success(true);
    }

    @GetMapping("/package/list")
    @Operation(summary = "获取包信息列表")
    @Parameters({
            @Parameter(name = "pluginId", description = "插件ID", required = true, example = "1024"),
            @Parameter(name = "pluginVersion", description = "插件版本", required = true, example = "1.0.0")
    })
    @PreAuthorize("@ss.hasPermission('plugin:config:query')")
    public CommonResult<List<PluginPackageRespVO>> getPackageList(
            @RequestParam("pluginId") Long pluginId,
            @RequestParam("pluginVersion") String pluginVersion) {
        return success(pluginConfigService.getPackageList(pluginId, pluginVersion));
    }

}
