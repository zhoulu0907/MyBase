package com.cmsr.onebase.plugin.demo.hello.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义HTTP接口处理器示例（新模式）
 * <p>
 * 演示如何使用标准Spring Controller方式实现复杂的插件HTTP接口。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@RestController
public class CustomApiHandler implements HttpHandler {

    /**
     * 注入 Spring ApplicationContext 以验证依赖注入功能
     */
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 获取插件信息
     * <p>
     * 访问路径：GET /plugin/hello-plugin/api/info
     * </p>
     */
    @GetMapping("/plugin/hello-plugin/api/info")
    public Map<String, Object> getPluginInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("plugin", "hello-plugin");
        info.put("version", "1.0.0");
        info.put("description", "OneBase插件开发示例");
        info.put("features", List.of("自定义函数", "数据处理器", "事件监听器", "HTTP接口"));

        // 验证 Spring Bean 注入功能
        if (applicationContext != null) {
            info.put("springInjectionWorking", true);
            info.put("springApplicationName", applicationContext.getApplicationName());
            info.put("springBeanDefinitionCount", applicationContext.getBeanDefinitionCount());
            info.put("springEnvironmentActive",
                    String.join(",", applicationContext.getEnvironment().getActiveProfiles()));
        } else {
            info.put("springInjectionWorking", false);
        }

        return info;
    }

    /**
     * 获取插件状态
     * <p>
     * 访问路径：GET /plugin/hello-plugin/api/status
     * </p>
     */
    @GetMapping("/plugin/hello-plugin/api/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("uptime", "1h 23m 45s");
        return status;
    }

    /**
     * 处理数据
     * <p>
     * 访问路径：POST /plugin/hello-plugin/api/process
     * </p>
     */
    @PostMapping("/plugin/hello-plugin/api/process")
    public Map<String, Object> processData(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("receivedData", data);
        result.put("processedCount", data.size());
        return result;
    }
}
