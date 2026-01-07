package com.cmsr.onebase.plugin.simulator.controller;

import com.cmsr.onebase.plugin.simulator.config.MockPluginConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 插件配置管理 API
 * <p>
 * 提供 HTTP 接口用于：
 * <ul>
 * <li>查看插件配置</li>
 * <li>动态更新配置（开发调试用）</li>
 * <li>重载配置文件</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-01-05
 */
@RestController
@RequestMapping("/api/plugin-config")
public class PluginConfigController {

    private static final Logger log = LoggerFactory.getLogger(PluginConfigController.class);

    @Resource
    private MockPluginConfigService configService;

    /**
     * 获取所有插件配置
     */
    @GetMapping
    public ResponseEntity<Map<String, Map<String, Object>>> getAllConfigs() {
        return ResponseEntity.ok(configService.getAllConfigs());
    }

    /**
     * 获取指定插件的配置
     *
     * @param pluginId 插件ID
     * @return 配置Map
     */
    @GetMapping("/{pluginId}")
    public ResponseEntity<Map<String, Object>> getConfig(@PathVariable String pluginId) {
        // 模拟器中使用默认版本
        Map<String, Object> config = configService.getConfig(pluginId, "1.0.0");
        if (config.isEmpty()) {
            log.warn("[PluginConfigController] 插件 [{}] 无配置", pluginId);
        }
        return ResponseEntity.ok(config);
    }

    /**
     * 获取指定配置项的值
     *
     * @param pluginId 插件ID
     * @param key      配置键
     * @return 配置值
     */
    @GetMapping("/{pluginId}/{key}")
    public ResponseEntity<Object> getConfigValue(
            @PathVariable String pluginId,
            @PathVariable String key) {
        // 模拟器中使用默认版本
        Object value = configService.getConfigValue(pluginId, "1.0.0", key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    /**
     * 设置插件配置（覆盖整个配置）
     *
     * @param pluginId 插件ID
     * @param config   配置Map
     * @return 操作结果
     */
    @PutMapping("/{pluginId}")
    public ResponseEntity<Map<String, Object>> setConfig(
            @PathVariable String pluginId,
            @RequestBody Map<String, Object> config) {
        configService.setConfig(pluginId, config);
        log.info("[PluginConfigController] 插件 [{}] 配置已更新", pluginId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置已更新",
                "pluginId", pluginId,
                "configCount", config.size()));
    }

    /**
     * 设置单个配置项
     *
     * @param pluginId 插件ID
     * @param key      配置键
     * @param value    配置值
     * @return 操作结果
     */
    @PutMapping("/{pluginId}/{key}")
    public ResponseEntity<Map<String, Object>> setConfigValue(
            @PathVariable String pluginId,
            @PathVariable String key,
            @RequestBody Object value) {
        configService.setConfigValue(pluginId, key, value);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置项已更新",
                "pluginId", pluginId,
                "key", key,
                "value", value));
    }

    /**
     * 删除插件配置
     *
     * @param pluginId 插件ID
     * @return 操作结果
     */
    @DeleteMapping("/{pluginId}")
    public ResponseEntity<Map<String, Object>> removeConfig(@PathVariable String pluginId) {
        configService.removeConfig(pluginId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置已删除",
                "pluginId", pluginId));
    }

    /**
     * 删除单个配置项
     *
     * @param pluginId 插件ID
     * @param key      配置键
     * @return 操作结果
     */
    @DeleteMapping("/{pluginId}/{key}")
    public ResponseEntity<Map<String, Object>> removeConfigValue(
            @PathVariable String pluginId,
            @PathVariable String key) {
        configService.removeConfigValue(pluginId, key);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置项已删除",
                "pluginId", pluginId,
                "key", key));
    }

    /**
     * 重载配置文件
     *
     * @return 操作结果
     */
    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reloadConfig() {
        configService.reloadConfig();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置已重新加载",
                "configCount", configService.getAllConfigs().size()));
    }
}
