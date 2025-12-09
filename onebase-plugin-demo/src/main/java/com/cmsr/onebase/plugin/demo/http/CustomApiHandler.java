package com.cmsr.onebase.plugin.demo.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.HttpRequest;
import com.cmsr.onebase.plugin.model.HttpResponse;
import com.cmsr.onebase.plugin.service.DataService;
import org.pf4j.Extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义HTTP接口处理器示例
 * <p>
 * 演示如何实现自定义HTTP接口扩展点，暴露插件提供的REST API
 * </p>
 *
 * @author OneBase Team
 * @date 2025-11-29
 */
@Extension
public class CustomApiHandler implements HttpHandler {

    @Override
    public String pathPattern() {
        return "/demo";
    }

    @Override
    public String method() {
        return "GET";
    }

    @Override
    public String description() {
        return "示例插件API，提供插件信息、状态查询等功能";
    }

    @Override
    public HttpResponse handle(PluginContext ctx, HttpRequest request) {
        String action = request.getQueryParam("action");

        try {
            if (action == null || action.isEmpty()) {
                // 返回插件信息
                Map<String, Object> info = new HashMap<>();
                info.put("plugin", "demo-plugin");
                info.put("version", "1.0.0");
                info.put("description", "OneBase插件开发示例");
                info.put("features", List.of("自定义函数", "数据处理器", "事件监听器", "HTTP接口"));
                return HttpResponse.ok(info);
            }

            return switch (action) {
                case "status" -> getStatus(ctx);
                case "stats" -> getStats();
                default -> HttpResponse.badRequest("Unknown action: " + action);
            };
        } catch (Exception e) {
            return HttpResponse.error(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private HttpResponse getStatus(PluginContext ctx) {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("tenantId", ctx.getTenantId());
        status.put("userId", ctx.getUserId());
        return HttpResponse.ok(status);
    }

    private HttpResponse getStats() {
        // 模拟返回统计信息
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", 1000);
        stats.put("successRate", "99.5%");
        stats.put("avgResponseTime", "50ms");
        return HttpResponse.ok(stats);
    }
}
