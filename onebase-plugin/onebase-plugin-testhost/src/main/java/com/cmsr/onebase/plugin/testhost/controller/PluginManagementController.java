package com.cmsr.onebase.plugin.testhost.controller;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.pf4j.PluginState;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件管理 REST API
 * <p>
 * 提供插件生命周期管理的接口。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@RestController
@RequestMapping("/api/plugin")
public class PluginManagementController {

    @Resource(name = "oneBasePluginManager")
    private OneBasePluginManager pluginManager;

    /**
     * 获取所有已加载的插件列表
     *
     * @return 插件信息列表
     */
    @GetMapping("/list")
    public Map<String, Object> listPlugins() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("plugins", pluginManager.getLoadedPlugins());
        result.put("count", pluginManager.getLoadedPlugins().size());
        return result;
    }

    /**
     * 启动指定插件
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @PostMapping("/{pluginId}/start")
    public Map<String, Object> startPlugin(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            PluginState state = pluginManager.startPlugin(pluginId);
            result.put("success", true);
            result.put("pluginId", pluginId);
            result.put("state", state.toString());
            result.put("message", "Plugin started successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 停止指定插件
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @PostMapping("/{pluginId}/stop")
    public Map<String, Object> stopPlugin(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            PluginState state = pluginManager.stopPlugin(pluginId);
            result.put("success", true);
            result.put("pluginId", pluginId);
            result.put("state", state.toString());
            result.put("message", "Plugin stopped successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 重新加载指定插件
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @PostMapping("/{pluginId}/reload")
    public Map<String, Object> reloadPlugin(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            PluginState state = pluginManager.reloadPlugin(pluginId);
            result.put("success", true);
            result.put("pluginId", pluginId);
            result.put("state", state.toString());
            result.put("message", "Plugin reloaded successfully");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 获取指定插件的详细信息
     *
     * @param pluginId 插件ID
     * @return 插件详细信息
     */
    @GetMapping("/{pluginId}/info")
    public Map<String, Object> getPluginInfo(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            var pluginWrapper = pluginManager.getPlugin(pluginId);
            if (pluginWrapper.isPresent()) {
                var wrapper = pluginWrapper.get();
                result.put("success", true);
                result.put("pluginId", wrapper.getPluginId());
                result.put("description", wrapper.getDescriptor().getPluginDescription());
                result.put("version", wrapper.getDescriptor().getVersion());
                result.put("provider", wrapper.getDescriptor().getProvider());
                result.put("state", wrapper.getPluginState().toString());
            } else {
                result.put("success", false);
                result.put("error", "Plugin not found: " + pluginId);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 启动所有插件
     *
     * @return 操作结果
     */
    @PostMapping("/start-all")
    public Map<String, Object> startAllPlugins() {
        Map<String, Object> result = new HashMap<>();
        try {
            pluginManager.startAllPlugins();
            result.put("success", true);
            result.put("message", "All plugins started");
            result.put("count", pluginManager.getStartedPlugins().size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 停止所有插件
     *
     * @return 操作结果
     */
    @PostMapping("/stop-all")
    public Map<String, Object> stopAllPlugins() {
        Map<String, Object> result = new HashMap<>();
        try {
            pluginManager.stopAllPlugins();
            result.put("success", true);
            result.put("message", "All plugins stopped");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
