package com.cmsr.onebase.plugin.demo.hello.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.service.PluginContextService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件配置演示 Controller
 * <p>
 * 演示如何在插件中读取和使用动态配置参数。
 * </p>
 *
 * @author OneBase Team
 * @date 2026-01-05
 */
@RestController
@RequestMapping("/plugin/hello-plugin/context")
public class ContextDemoController implements HttpHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private PluginContextService pluginContextService;

    /**
     * 获取所有配置
     * <p>
     * 示例：GET /plugin/hello-plugin/context/all
     * </p>
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("pluginId", getPluginId());
        result.put("pluginVersion", getPluginVersion());
        result.put("config", pluginContextService.getConfig(getPluginId(), getPluginVersion()));
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));

        return ResponseEntity.ok(result);
    }

    /**
     * 获取租户ID
     * <p>
     * 示例：GET /plugin/hello-plugin/context/current-tenant/tenantId
     * </p>
     */
    @GetMapping("/current-tenant/tenantId")
    public ResponseEntity<Map<String, Object>> getCurrentTenantId() {
        Long tenantId = pluginContextService.getTenantId() ;

        Map<String, Object> result = new HashMap<>();
        result.put("tenantId", tenantId);
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));

        return ResponseEntity.ok(result);
    }

    /**
     * 获取指定配置项
     * <p>
     * 示例：GET /plugin/hello-plugin/context/key/apiKey
     * </p>
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<Map<String, Object>> getConfigValue(@PathVariable String key) {
        Object value = pluginContextService.getConfigValue(getPluginId(), getPluginVersion(), key);

        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("exists", pluginContextService.hasConfigKey(getPluginId(), getPluginVersion(), key));
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));

        return ResponseEntity.ok(result);
    }

    /**
     * 配置应用演示
     * <p>
     * 演示如何在实际业务中使用配置参数。
     * 示例：GET /plugin/hello-plugin/context/demo
     * </p>
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> configDemo() {
        // 读取各种类型的配置
        String apiKey = pluginContextService.getConfigValue(getPluginId(), getPluginVersion(), "apiKey",
                "default-key");
        String provider = pluginContextService.getConfigValue(getPluginId(), getPluginVersion(), "provider",
                "unknown");
        Integer timeout = pluginContextService.getConfigValue(getPluginId(), getPluginVersion(), "timeout",
                3000);
        Integer maxRetries = pluginContextService.getConfigValue(getPluginId(), getPluginVersion(),
                "maxRetries", 3);
        Boolean enableLog = pluginContextService.getConfigValue(getPluginId(), getPluginVersion(), "enableLog",
                false);

        // 模拟业务逻辑
        Map<String, Object> businessResult = new HashMap<>();
        businessResult.put("message", "配置应用演示");
        businessResult.put("provider", provider);
        businessResult.put("apiKeyConfigured", !apiKey.equals("default-key"));
        businessResult.put("timeout", timeout + "ms");
        businessResult.put("maxRetries", maxRetries);
        businessResult.put("logEnabled", enableLog);

        if (enableLog) {
            businessResult.put("log", String.format("[%s] 使用 %s 服务，超时时间: %dms",
                    LocalDateTime.now().format(FORMATTER), provider, timeout));
        }

        return ResponseEntity.ok(businessResult);
    }

    /**
     * 配置信息总览
     * <p>
     * 示例：GET /plugin/hello-plugin/context/info
     * </p>
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> configInfo() {
        Map<String, Object> allConfig = pluginContextService.getConfig(getPluginId(), getPluginVersion());

        Map<String, Object> info = new HashMap<>();
        info.put("pluginId", getPluginId());
        info.put("pluginVersion", getPluginVersion());
        info.put("configCount", allConfig.size());
        info.put("configKeys", allConfig.keySet());
        info.put("timestamp", LocalDateTime.now().format(FORMATTER));

        // 提示信息
        Map<String, String> tips = new HashMap<>();
        tips.put("viewAll", "GET /plugin/hello-plugin/config/all");
        tips.put("viewKey", "GET /plugin/hello-plugin/config/key/{key}");
        tips.put("demo", "GET /plugin/hello-plugin/config/demo");
        tips.put("updateConfig", "PUT http://localhost:8080/api/plugin-context/dev-mode-plugin");

        info.put("apiTips", tips);

        return ResponseEntity.ok(info);
    }
}
