package com.cmsr.onebase.plugin.runtime.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.service.PluginContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 插件上下文控制器
 * <p>
 * 提供插件上下文信息的查询接口，包括租户ID、应用ID和插件配置
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-12
 */
@Tag(name = "管理后台 - 插件上下文")
@RestController
@RequestMapping("/plugin/context")
@Validated
public class PluginContextController {

    @Resource
    private PluginContextService pluginContextService;

    @Resource
    private OneBasePluginManager oneBasePluginManager;

    @Resource
    private PluginProperties pluginProperties;

    @GetMapping("/manifest")
    @Operation(summary = "获取所有可用插件的前端资源清单")
    public CommonResult<List<Map<String, Object>>> getPluginManifest() {
        List<Map<String, Object>> manifest = new ArrayList<>();
        List<PluginWrapper> plugins = oneBasePluginManager.getPluginManager().getPlugins();

        String contextPath = pluginProperties.getFrontendContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }

        for (PluginWrapper plugin : plugins) {
            // 只处理已启动的插件
            if (plugin.getPluginState() != PluginState.STARTED) {
                continue;
            }

            String pluginId = plugin.getPluginId();
            String version = plugin.getDescriptor().getVersion().toString();
            String dirName = "frontend-" + pluginId + "-" + version;
            
            // 检查前端目录是否存在
            Path frontendDir = Paths.get(pluginProperties.getPluginsDir(), pluginProperties.getFrontendDir(), dirName);
            if (!Files.exists(frontendDir)) {
                continue;
            }

            Map<String, Object> info = new HashMap<>();
            info.put("pluginId", pluginId);
            info.put("version", version);
            
            // 构建访问基路径
            String baseUrl = contextPath + dirName + "/";
            info.put("baseUrl", baseUrl);

            // 探测入口文件
            if (Files.exists(frontendDir.resolve("remoteEntry.js"))) {
                info.put("entry", "remoteEntry.js");
                info.put("type", "module-federation");
            } else if (Files.exists(frontendDir.resolve("index.html"))) {
                info.put("entry", "index.html");
                info.put("type", "iframe");
            } else {
                info.put("type", "static");
            }

            manifest.add(info);
        }

        return success(manifest);
    }

    @GetMapping("/tenant-id")
    @Operation(summary = "获取当前租户ID")
    public CommonResult<Long> getTenantId() {
        Long tenantId = pluginContextService.getTenantId();
        return success(tenantId);
    }

    @GetMapping("/application-id")
    @Operation(summary = "获取当前应用ID")
    public CommonResult<Long> getApplicationId() {
        Long applicationId = pluginContextService.getApplicationId();
        return success(applicationId);
    }

    @GetMapping("/config")
    @Operation(summary = "获取指定插件的全部配置")
    public CommonResult<Map<String, Object>> getConfig(
            @Parameter(description = "插件ID", required = true) @RequestParam("pluginId") String pluginId,
            @Parameter(description = "插件版本", required = true) @RequestParam("version") String version) {
        Map<String, Object> config = pluginContextService.getConfig(pluginId, version);
        return success(config);
    }
}
