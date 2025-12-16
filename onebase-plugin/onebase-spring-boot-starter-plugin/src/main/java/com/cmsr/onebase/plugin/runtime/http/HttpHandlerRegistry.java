package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP处理器注册器
 * <p>
 * 负责将插件中的HttpHandler（@RestController）动态注册到主应用的DispatcherServlet。
 * 采用直接注册方式，将处理器对象和其映射方法直接添加到RequestMappingHandlerMapping。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Slf4j
public class HttpHandlerRegistry {

    private final RequestMappingHandlerMapping handlerMapping;
    private final List<Object> registeredHandlers = new ArrayList<>();
    // 记录每个 pluginId 注册的处理器实例
    private final Map<String, List<Object>> pluginHandlers = new ConcurrentHashMap<>();
    // 记录每个 pluginId 注册的映射字符串（不再直接向 RequestMappingHandlerMapping 注册），用于管理元数据与卸载
    private final Map<String, List<String>> pluginMappings = new ConcurrentHashMap<>();

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

        log.info("HTTP处理器已注册，共 {} 个处理器", registeredHandlers.size());
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
            // 获取每个方法上的路径和请求方式
            List<String> paths = getMethodPaths(method);
            Set<RequestMethod> verbs = getMethodRequestMethods(method);
            if (paths.isEmpty() || verbs.isEmpty()) continue;

            hasMapping = true;
            for (String path : paths) {
                for (RequestMethod verb : verbs) {
                    // 不再直接向 Spring 的 RequestMappingHandlerMapping 注册映射，使用统一的代理控制器 `/plugin/**` 转发。
                    String mappingStr = verb + " " + path;
                    String pluginId = resolvePluginIdFromClassLoader(handlerClass.getClassLoader());
                    if (pluginId == null) pluginId = "unknown";
                    pluginMappings.computeIfAbsent(pluginId, k -> new ArrayList<>()).add(mappingStr);
                    log.debug("  记录映射元数据: {} -> {}#{} (plugin={})", verb, path, handlerClass.getName(), method.getName(), pluginId);
                }
            }
        }

        if (hasMapping) {
            registeredHandlers.add(handlerObject);
            String pluginId = resolvePluginIdFromClassLoader(handlerClass.getClassLoader());
            if (pluginId == null) pluginId = "unknown";
            pluginHandlers.computeIfAbsent(pluginId, k -> new ArrayList<>()).add(handlerObject);
            log.debug("HTTP处理器注册成功: {}", handlerClass.getName());
        } else {
            log.warn("HTTP处理器 {} 中未找到任何映射方法，跳过注册", handlerClass.getName());
        }
    }

    /**
     * 注销指定 pluginId 注册的处理器
     * 目前移除内部记录，若将来需要从 RequestMappingHandlerMapping 中解绑实际映射，请在此处补充实现。
     */
    public void unregisterHandlers(String pluginId) {
        if (pluginId == null) {
            return;
        }
        // 1) 移除映射元数据（不再直接对 RequestMappingHandlerMapping 做注册/注销）
        List<String> mappings = pluginMappings.remove(pluginId);
        if (mappings != null) {
            for (String info : mappings) {
                log.info("已移除映射元数据: {} (plugin={})", info, pluginId);
            }
        }

        // 2) 移除内部已注册 handler 实例记录
        List<Object> handlers = pluginHandlers.remove(pluginId);
        if (handlers != null) {
            for (Object h : handlers) {
                registeredHandlers.remove(h);
                log.info("已移除 HTTP 处理器记录: {} (plugin={})", h.getClass().getName(), pluginId);
            }
        }

        log.info("plugin {} 的 HTTP 处理器和映射已移除", pluginId);
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

    private List<String> getMethodPaths(Method method) {
        List<String> paths = new ArrayList<>();
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
            org.springframework.web.bind.annotation.GetMapping m = method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
            for (String p : m.value()) paths.add(normalizePath(p));
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
            org.springframework.web.bind.annotation.PostMapping m = method.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class);
            for (String p : m.value()) paths.add(normalizePath(p));
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) {
            org.springframework.web.bind.annotation.PutMapping m = method.getAnnotation(org.springframework.web.bind.annotation.PutMapping.class);
            for (String p : m.value()) paths.add(normalizePath(p));
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) {
            org.springframework.web.bind.annotation.DeleteMapping m = method.getAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class);
            for (String p : m.value()) paths.add(normalizePath(p));
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)) {
            org.springframework.web.bind.annotation.PatchMapping m = method.getAnnotation(org.springframework.web.bind.annotation.PatchMapping.class);
            for (String p : m.value()) paths.add(normalizePath(p));
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)) {
            org.springframework.web.bind.annotation.RequestMapping m = method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
            for (String p : m.value()) paths.add(normalizePath(p));
        }
        return paths;
    }

    private Set<RequestMethod> getMethodRequestMethods(Method method) {
        Set<RequestMethod> set = new HashSet<>();
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) set.add(RequestMethod.GET);
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) set.add(RequestMethod.POST);
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) set.add(RequestMethod.PUT);
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) set.add(RequestMethod.DELETE);
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)) set.add(RequestMethod.PATCH);
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)) {
            org.springframework.web.bind.annotation.RequestMapping m = method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
            org.springframework.web.bind.annotation.RequestMethod[] arr = m.method();
            if (arr != null && arr.length > 0) {
                for (org.springframework.web.bind.annotation.RequestMethod rm : arr) {
                    try {
                        set.add(RequestMethod.valueOf(rm.name()));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return set;
    }

    private String normalizePath(String p) {
        if (p == null || p.isEmpty()) return "/";
        return p.startsWith("/") ? p : ("/" + p);
    }

    private String resolvePluginIdFromClassLoader(ClassLoader cl) {
        if (cl == null) return null;
        try {
            Method m = cl.getClass().getMethod("getPluginId");
            Object val = m.invoke(cl);
            return val != null ? String.valueOf(val) : null;
        } catch (Throwable ignored) {
            return null;
        }
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
