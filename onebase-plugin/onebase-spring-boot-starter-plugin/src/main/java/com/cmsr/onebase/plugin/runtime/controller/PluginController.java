package com.cmsr.onebase.plugin.runtime.controller;

import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件管理控制器
 * <p>
 * 提供插件管理和测试的REST API接口。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Resource
    private OneBasePluginManager pluginManager;

    /**
     * 获取所有已加载的插件列表
     *
     * @return 插件信息列表
     */
    @GetMapping("/list")
    public Map<String, Object> listPlugins() {
        Map<String, Object> result = new HashMap<>();
        result.put("plugins", pluginManager.getLoadedPlugins());
        result.put("count", pluginManager.getLoadedPlugins().size());
        return result;
    }

    /**
     * 启动插件
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @PostMapping("/{pluginId}/start")
    public Map<String, Object> startPlugin(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            pluginManager.startPlugin(pluginId);
            result.put("success", true);
            result.put("message", "Plugin started: " + pluginId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 停止插件
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @PostMapping("/{pluginId}/stop")
    public Map<String, Object> stopPlugin(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            pluginManager.stopPlugin(pluginId);
            result.put("success", true);
            result.put("message", "Plugin stopped: " + pluginId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 重新加载插件
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @PostMapping("/{pluginId}/reload")
    public Map<String, Object> reloadPlugin(@PathVariable String pluginId) {
        Map<String, Object> result = new HashMap<>();
        try {
            pluginManager.reloadPlugin(pluginId);
            result.put("success", true);
            result.put("message", "Plugin reloaded: " + pluginId);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
