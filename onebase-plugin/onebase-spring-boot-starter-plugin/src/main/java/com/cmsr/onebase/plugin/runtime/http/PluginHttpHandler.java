package com.cmsr.onebase.plugin.runtime.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 插件HTTP请求代理处理器
 * <p>
 * 作为主应用的代理，接收所有/plugin/*开头的请求，转发给具体的插件处理器。
 * 这是必要的，因为插件处理器在隔离的ClassLoader中，Spring无法直接扫描注册。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@RestController
@RequestMapping("/plugin")
public class PluginHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(PluginHttpHandler.class);

    private final PluginHttpDispatcher dispatcher;

    public PluginHttpHandler(PluginHttpDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        log.debug("PluginHttpHandler已初始化，dispatcher={}", dispatcher != null ? "OK" : "NULL");
    }

    /**
     * 处理所有插件的GET请求
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @return 响应结果
     */
    @GetMapping("/**")
    public Object handleGet(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI().substring("/plugin".length());
        log.debug("GET请求: {}", path);
        try {
            Object result = dispatcher.dispatch(request, response, path, "GET");
            log.debug("返回结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("处理GET请求失败: {}", path, e);
            return Map.of(
                    "code", 500,
                    "message", "错误: " + e.getMessage(),
                    "success", false
            );
        }
    }

    /**
     * 处理所有插件的POST请求
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @return 响应结果
     */
    @PostMapping("/**")
    public Object handlePost(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI().substring("/plugin".length());
        log.debug("POST请求: {}", path);
        try {
            Object result = dispatcher.dispatch(request, response, path, "POST");
            log.debug("返回结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("处理POST请求失败: {}", path, e);
            return Map.of(
                    "code", 500,
                    "message", "错误: " + e.getMessage(),
                    "success", false
            );
        }
    }

    /**
     * 处理所有插件的PUT请求
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @return 响应结果
     */
    @PutMapping("/**")
    public Object handlePut(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI().substring("/plugin".length());
        return dispatcher.dispatch(request, response, path, "PUT");
    }

    /**
     * 处理所有插件的DELETE请求
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @return 响应结果
     */
    @DeleteMapping("/**")
    public Object handleDelete(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI().substring("/plugin".length());
        return dispatcher.dispatch(request, response, path, "DELETE");
    }
}
