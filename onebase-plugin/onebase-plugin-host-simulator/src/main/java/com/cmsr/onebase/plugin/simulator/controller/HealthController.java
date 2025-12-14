package com.cmsr.onebase.plugin.simulator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查 REST API
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@RestController
public class HealthController {

    /**
     * 应用主页
     *
     * @return 欢迎信息
     */
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("app", "OneBase Plugin Host Simulator");
        response.put("version", "1.0.0");
        response.put("description", "OneBase 插件宿主模拟器 - 既能免打包快速 debug 又能全生命周期插件验证");
        response.put("apis", Map.of(
            "插件列表", "GET /plugin/list",
            "启动插件", "POST /plugin/{pluginId}/start",
            "停止插件", "POST /plugin/{pluginId}/stop",
            "重新加载插件", "POST /plugin/{pluginId}/reload",
            "插件信息", "GET /plugin/{pluginId}/info",
            "启动所有插件", "POST /plugin/start-all",
            "停止所有插件", "POST /plugin/stop-all",
            "健康检查", "GET /health"
        ));
        return response;
    }

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
