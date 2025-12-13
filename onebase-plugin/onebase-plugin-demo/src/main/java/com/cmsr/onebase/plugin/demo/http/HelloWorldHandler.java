package com.cmsr.onebase.plugin.demo.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HelloWorld HTTP接口示例（新模式）
 * <p>
 * 使用标准Spring Controller方式开发插件HTTP接口：
 * <ul>
 *   <li>实现 {@link HttpHandler} 标记接口</li>
 *   <li>添加 {@code @RestController} 和 {@code @RequestMapping} 注解</li>
 *   <li>路由必须以 {@code /plugin/demo-plugin/} 开头</li>
 *   <li>可直接使用Spring MVC的所有特性</li>
 * </ul>
 * </p>
 *
 * @author matianyu
 * @date 2025-12-13
 */
@RestController
public class HelloWorldHandler implements HttpHandler {

    /**
     * Hello World接口
     * <p>访问路径：GET /plugin/demo-plugin/hello?name=xxx</p>
     */
    @GetMapping("/plugin/demo-plugin/hello")
    public Map<String, Object> hello(@RequestParam(defaultValue = "World") String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, " + name + "!");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.put("plugin", "demo-plugin");
        result.put("version", "1.0.0");
        return result;
    }
}

