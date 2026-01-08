package com.cmsr.onebase.plugin.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.build.service.PluginConfigService;
import com.cmsr.onebase.plugin.build.vo.req.PluginConfigSaveReqVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigDetailRespVO;
import com.cmsr.onebase.plugin.build.vo.resp.PluginConfigTemplateRespVO;
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

    @GetMapping("/template")
    @Operation(summary = "获取配置项（配置模板信息，供前端渲染配置表单）")
    @Parameters({
            @Parameter(name = "pluginId", description = "插件ID", required = true, example = "test-plugin"),
            @Parameter(name = "pluginVersion", description = "插件版本", required = true, example = "1.0.0")
    })
    @PreAuthorize("@ss.hasPermission('plugin:config:query')")
    public CommonResult<PluginConfigTemplateRespVO> getConfigTemplate(
            @RequestParam("pluginId") String pluginId,
            @RequestParam("pluginVersion") String pluginVersion) {
        return success(pluginConfigService.getConfigTemplate(pluginId, pluginVersion));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取配置项详情（Map结构，key为configKey，value为配置值对象）")
    @Parameters({
            @Parameter(name = "pluginId", description = "插件ID", required = true, example = "test-plugin"),
            @Parameter(name = "pluginVersion", description = "插件版本", required = true, example = "1.0.0")
    })
    @PreAuthorize("@ss.hasPermission('plugin:config:query')")
    public CommonResult<PluginConfigDetailRespVO> getConfigDetail(
            @RequestParam("pluginId") String pluginId,
            @RequestParam("pluginVersion") String pluginVersion) {
        return success(pluginConfigService.getConfigDetail(pluginId, pluginVersion));
    }

    @PostMapping("/save")
    @Operation(summary = "创建/更新配置（先删除旧配置再插入新配置）")
    @PreAuthorize("@ss.hasPermission('plugin:config:update')")
    public CommonResult<Boolean> saveConfigs(@Valid @RequestBody PluginConfigSaveReqVO saveReqVO) {
        pluginConfigService.saveConfigs(saveReqVO);
        return success(true);
    }

    @GetMapping("/package/list")
    @Operation(summary = "获取包信息列表")
    @Parameters({
            @Parameter(name = "pluginId", description = "插件ID", required = true, example = "test-plugin"),
            @Parameter(name = "pluginVersion", description = "插件版本", required = true, example = "1.0.0")
    })
    @PreAuthorize("@ss.hasPermission('plugin:config:query')")
    public CommonResult<List<PluginPackageRespVO>> getPackageList(
            @RequestParam("pluginId") String pluginId,
            @RequestParam("pluginVersion") String pluginVersion) {
        return success(pluginConfigService.getPackageList(pluginId, pluginVersion));
    }

}
