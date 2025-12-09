package com.cmsr.onebase.plugin.runtime.controller;

import com.cmsr.onebase.plugin.api.CustomFunction;
import com.cmsr.onebase.plugin.runtime.executor.CustomFunctionExecutor;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private CustomFunctionExecutor functionExecutor;

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
     * 获取所有已注册的自定义函数
     *
     * @return 函数列表
     */
    @GetMapping("/functions")
    public Map<String, Object> listFunctions() {
        List<CustomFunction> functions = pluginManager.getExtensions(CustomFunction.class);
        List<Map<String, Object>> functionList = functions.stream()
                .map(f -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("name", f.name());
                    info.put("description", f.description());
                    info.put("category", f.category());
                    info.put("returnType", f.returnType());
                    info.put("params", f.params());
                    return info;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("functions", functionList);
        result.put("count", functionList.size());
        return result;
    }

    /**
     * 执行自定义函数
     *
     * @param functionName 函数名称
     * @param request      请求参数
     * @return 执行结果
     */
    @PostMapping("/functions/{functionName}/execute")
    public Map<String, Object> executeFunction(
            @PathVariable String functionName,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取参数
            Object[] args = {};
            if (request.containsKey("args")) {
                Object argsObj = request.get("args");
                if (argsObj instanceof List) {
                    args = ((List<?>) argsObj).toArray();
                }
            }

            // 执行函数
            Object executeResult = functionExecutor.execute(functionName, args);

            result.put("success", true);
            result.put("result", executeResult);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
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
