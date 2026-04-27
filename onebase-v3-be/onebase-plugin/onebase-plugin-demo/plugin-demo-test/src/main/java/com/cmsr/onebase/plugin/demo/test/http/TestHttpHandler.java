package com.cmsr.onebase.plugin.demo.test.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试HTTP接口处理器
 * <p>
 * 演示如何实现简单的插件HTTP接口，返回当前时间和测试信息
 * </p>
 *
 * @author OneBase Team
 * @date 2025-12-19
 */
@RestController
public class TestHttpHandler implements HttpHandler {

    /**
     * 获取当前时间和测试信息
     * <p>访问路径：GET /plugin/test-plugin/api/info</p>
     */
    @GetMapping("/plugin/test-plugin/api/info")
    public Map<String, Object> getTestInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "你好，这是一个测试demo");
        info.put("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        info.put("plugin", "plugin-demo-test");
        info.put("version", "1.0.0");
        return info;
    }

}