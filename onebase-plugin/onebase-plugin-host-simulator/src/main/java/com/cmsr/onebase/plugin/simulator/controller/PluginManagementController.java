package com.cmsr.onebase.plugin.simulator.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.simulator.constant.PluginErrorMessages;
import com.cmsr.onebase.plugin.simulator.controller.plugin.vo.*;
import com.cmsr.onebase.plugin.simulator.convert.PluginConvert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.pf4j.PluginState;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 插件管理 REST API
 * <p>
 * 提供插件生命周期管理的接口。
 * </p>
 *
 * <h3>模式支持说明</h3>
 * <ul>
 *     <li><b>PROD模式</b>：完整支持所有接口，获取准确的插件信息（从ZIP包）</li>
 *     <li><b>STAGING模式</b>：支持ZIP包加载，完整生命周期管理</li>
 *     <li><b>DEV模式</b>：仅支持HTTP路由调用，插件列表/详情接口获取信息不准确
 *         （dev模式使用虚拟插件，不是真实的PF4J插件包装器）</li>
 * </ul>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Tag(name = "插件管理")
@RestController
@RequestMapping("/api/plugin")
public class PluginManagementController {

    @Resource(name = "oneBasePluginManager")
    private OneBasePluginManager pluginManager;

    @Resource
    private PluginConvert pluginConvert;

    @GetMapping("/list")
    @Operation(summary = "获取所有已加载的插件列表")
    public CommonResult<List<PluginRespVO>> listPlugins() {
        List<OneBasePluginManager.PluginInfo> plugins = pluginManager.getLoadedPlugins();
        return success(pluginConvert.convertList(plugins));
    }
    @GetMapping("/{pluginId}/info")
    @Operation(summary = "获取指定插件的详细信息")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "hello-plugin")
    public CommonResult<PluginRespVO> getPluginInfo(@PathVariable("pluginId") String pluginId) {
        // 尝试从getPlugin获取PluginWrapper
        return pluginManager.getPlugin(pluginId)
                .map(wrapper -> success(pluginConvert.convert(wrapper)))
                // 如果getPlugin返回empty（如DEV模式），尝试从getLoadedPlugins获取PluginInfo
                .orElseGet(() -> pluginManager.getLoadedPlugins().stream()
                        .filter(info -> info.getPluginId().equals(pluginId))
                        .findFirst()
                        .map(info -> success(pluginConvert.convert(info)))
                        .orElse(CommonResult.error(404, String.format(PluginErrorMessages.PLUGIN_NOT_FOUND, pluginId))));
    }
    @PostMapping("/{pluginId}/start")
    @Operation(summary = "启动指定插件")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "hello-plugin")
    public CommonResult<PluginOperationResultItemVO> startPlugin(@PathVariable("pluginId") String pluginId) {
        if (!isPluginExists(pluginId)) {
            return CommonResult.error(404, String.format(PluginErrorMessages.PLUGIN_NOT_FOUND, pluginId));
        }
        PluginState state = pluginManager.startPlugin(pluginId);
        return success(PluginOperationResultItemVO.success(pluginId, state.toString()));
    }

    @PostMapping("/{pluginId}/stop")
    @Operation(summary = "停止指定插件")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "hello-plugin")
    public CommonResult<PluginOperationResultItemVO> stopPlugin(@PathVariable("pluginId") String pluginId) {
        if (!isPluginExists(pluginId)) {
            return CommonResult.error(404, String.format(PluginErrorMessages.PLUGIN_NOT_FOUND, pluginId));
        }
        PluginState state = pluginManager.stopPlugin(pluginId);
        return success(PluginOperationResultItemVO.success(pluginId, state.toString()));
    }

    @PostMapping("/load")
    @Operation(summary = "从指定路径加载插件")
    @Parameter(name = "path", description = "插件文件路径（绝对或相对）", required = true, example = "plugins/hello-plugin-1.0.0-SNAPSHOT.zip")
    public CommonResult<PluginLoadRespVO> loadPlugin(@RequestParam("path") String path) {
        Path pluginPath = Paths.get(path);
        if (!Files.exists(pluginPath)) {
            return CommonResult.error(404, String.format(PluginErrorMessages.PLUGIN_FILE_NOT_FOUND, path));
        }
        
        String pluginId = pluginManager.loadPlugin(pluginPath);
        if (pluginId == null) {
            return CommonResult.error(500, String.format(PluginErrorMessages.PLUGIN_LOAD_FAILED, path));
        }
        
        PluginLoadRespVO respVO = new PluginLoadRespVO();
        respVO.setPluginId(pluginId);
        respVO.setPluginPath(path);
        respVO.setState(pluginManager.getPlugin(pluginId)
            .map(w -> w.getPluginState().toString())
            .orElse("UNKNOWN"));
        
        return success(respVO);
    }

    @PostMapping("/{pluginId}/unload")
    @Operation(summary = "卸载指定插件")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "hello-plugin")
    public CommonResult<PluginOperationResultItemVO> unloadPlugin(@PathVariable("pluginId") String pluginId) {
        if (!isPluginExists(pluginId)) {
            return CommonResult.error(404, String.format(PluginErrorMessages.PLUGIN_NOT_FOUND, pluginId));
        }
        boolean ok = pluginManager.unloadPlugin(pluginId);
        if (ok) {
            return success(PluginOperationResultItemVO.success(pluginId, "UNLOADED"));
        } else {
            return CommonResult.error(500, String.format(PluginErrorMessages.PLUGIN_UNLOAD_FAILED, pluginId));
        }
    }

    @PostMapping("/{pluginId}/reload")
    @Operation(summary = "重新加载指定插件")
    @Parameter(name = "pluginId", description = "插件ID", required = true, example = "hello-plugin")
    public CommonResult<PluginOperationResultItemVO> reloadPlugin(@PathVariable("pluginId") String pluginId) {
        if (!isPluginExists(pluginId)) {
            return CommonResult.error(404, String.format(PluginErrorMessages.PLUGIN_NOT_FOUND, pluginId));
        }
        PluginState state = pluginManager.reloadPlugin(pluginId);
        return success(PluginOperationResultItemVO.success(pluginId, state.toString()));
    }

    @PostMapping("/start-all")
    @Operation(summary = "启动所有插件")
    public CommonResult<PluginBatchOperationRespVO> startAllPlugins() {
        List<OneBasePluginManager.PluginInfo> plugins = pluginManager.getLoadedPlugins();
        List<PluginOperationResultItemVO> items = new ArrayList<>();
        
        for (OneBasePluginManager.PluginInfo plugin : plugins) {
            try {
                PluginState state = pluginManager.startPlugin(plugin.getPluginId());
                items.add(PluginOperationResultItemVO.success(plugin.getPluginId(), state.toString()));
            } catch (Exception e) {
                items.add(PluginOperationResultItemVO.error(plugin.getPluginId(), e.getMessage()));
            }
        }
        
        return success(new PluginBatchOperationRespVO(items));
    }

    @PostMapping("/stop-all")
    @Operation(summary = "停止所有插件")
    public CommonResult<PluginBatchOperationRespVO> stopAllPlugins() {
        // 使用getLoadedPlugins获取所有插件，然后尝试停止每一个
        List<OneBasePluginManager.PluginInfo> plugins = pluginManager.getLoadedPlugins();
        List<PluginOperationResultItemVO> items = new ArrayList<>();
        
        for (OneBasePluginManager.PluginInfo plugin : plugins) {
            try {
                PluginState state = pluginManager.stopPlugin(plugin.getPluginId());
                items.add(PluginOperationResultItemVO.success(plugin.getPluginId(), state.toString()));
            } catch (Exception e) {
                items.add(PluginOperationResultItemVO.error(plugin.getPluginId(), e.getMessage()));
            }
        }
        
        return success(new PluginBatchOperationRespVO(items));
    }

    /**
     * 检查插件是否存在
     *
     * @param pluginId 插件ID
     * @return 插件是否存在
     */
    private boolean isPluginExists(String pluginId) {
        return pluginManager.getLoadedPlugins().stream()
                .anyMatch(info -> info.getPluginId().equals(pluginId));
    }

}
