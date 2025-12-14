package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP处理器注册器
 * <p>
 * 负责将插件中的HttpHandler（@RestController）动态注册到主应用的DispatcherServlet。
 * 采用直接注册方式，将处理器对象和其映射方法直接添加到RequestMappingHandlerMapping。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-13
 */
@Component
public class HttpHandlerRegistry {

    private static final Logger log = LoggerFactory.getLogger(HttpHandlerRegistry.class);

    private final RequestMappingHandlerMapping handlerMapping;
    private final List<Object> registeredHandlers = new ArrayList<>();

    public HttpHandlerRegistry(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * 注册HTTP处理器到DispatcherServlet
     * <p>
     * 将插件处理器对象直接注册到RequestMappingHandlerMapping的handlers中。
     * </p>
     *
     * @param handlers HTTP处理器列表
     */
    public void registerHandlers(List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            log.debug("没有HTTP处理器需要注册");
            return;
        }

        for (HttpHandler handler : handlers) {
            registerHandler(handler);
        }

        log.info("HTTP处理器注册完成，共注册 {} 个处理器", registeredHandlers.size());
    }

    /**
     * 注册单个HTTP处理器
     *
     * @param handler HTTP处理器
     */
    private void registerHandler(HttpHandler handler) {
        Object handlerObject = (Object) handler;
        Class<?> handlerClass = handlerObject.getClass();

        log.debug("注册HTTP处理器: {}", handlerClass.getName());

        // 检查是否为@RestController
        if (!handlerClass.isAnnotationPresent(RestController.class)) {
            log.warn("HTTP处理器 {} 未添加@RestController注解，跳过注册", handlerClass.getName());
            return;
        }

        // 扫描所有方法，检查是否有@GetMapping、@PostMapping等注解
        Method[] methods = handlerClass.getMethods();
        boolean hasMapping = false;

        for (Method method : methods) {
            if (hasRequestMapping(method)) {
                hasMapping = true;
                log.debug("  发现映射方法: {} -> {}", method.getName(), getMethodPath(method));
            }
        }

        if (hasMapping) {
            registeredHandlers.add(handlerObject);
            log.info("HTTP处理器注册成功: {}", handlerClass.getName());
        } else {
            log.warn("HTTP处理器 {} 中未找到任何映射方法，跳过注册", handlerClass.getName());
        }
    }

    /**
     * 检查方法是否有请求映射注解
     *
     * @param method 方法对象
     * @return 是否有映射注解
     */
    private boolean hasRequestMapping(Method method) {
        return method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)
                || method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)
                || method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)
                || method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)
                || method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)
                || method.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class);
    }

    /**
     * 获取方法的映射路径
     *
     * @param method 方法对象
     * @return 映射路径
     */
    private String getMethodPath(Method method) {
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
            org.springframework.web.bind.annotation.GetMapping mapping =
                    method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
            return mapping.value().length > 0 ? mapping.value()[0] : "";
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
            org.springframework.web.bind.annotation.PostMapping mapping =
                    method.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class);
            return mapping.value().length > 0 ? mapping.value()[0] : "";
        }
        return "unknown";
    }

    /**
     * 获取已注册的处理器列表
     *
     * @return 处理器列表
     */
    public List<Object> getRegisteredHandlers() {
        return new ArrayList<>(registeredHandlers);
    }
}
