package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件 HTTP 路由管理器（合并 HttpHandlerRegistry + HttpHandlerInitializer 的职能）
 * <p>
 * 负责启动时发现插件的 HttpHandler、运行时注册/注销、以及维护 pluginId -> handlers/mappings 的元数据。
 * 默认采用代理 Controller + Dispatcher 模式，仅记录元数据；可按需扩展为直接向 Spring 注册映射。
 * </p>
 */
@Slf4j
public class HttpRoutingManager {

    private final ObjectProvider<OneBasePluginManager> pluginManagerProvider;
    private final RequestMappingHandlerMapping handlerMapping; // optional, may be null in some contexts
    private final PluginProperties pluginProperties;

    // bookkeeping
    private final Map<String, List<Object>> pluginHandlers = new ConcurrentHashMap<>();
    private final Map<String, List<String>> pluginMappings = new ConcurrentHashMap<>();
    private final List<Object> registeredHandlers = new ArrayList<>();

    public HttpRoutingManager(ObjectProvider<OneBasePluginManager> pluginManagerProvider,
                              @Nullable RequestMappingHandlerMapping handlerMapping,
                              PluginProperties pluginProperties) {
        this.pluginManagerProvider = pluginManagerProvider;
        this.handlerMapping = handlerMapping;
        this.pluginProperties = pluginProperties;
    }

    @PostConstruct
    public void init() {
        if (!pluginProperties.isEnabled()) {
            log.info("插件系统已禁用，跳过 HttpRoutingManager 初始化");
            return;
        }

        OneBasePluginManager manager = pluginManagerProvider.getIfAvailable();
        if (manager == null) {
            log.warn("OneBasePluginManager 尚不可用，跳过 HttpRoutingManager 启动时发现");
            return;
        }

        try {
            List<org.pf4j.PluginWrapper> started = manager.getStartedPlugins();
            if (started == null || started.isEmpty()) {
                log.debug("未发现已启动的插件，跳过初始 HttpHandler 注册");
                return;
            }

            int idx = 1;
            for (org.pf4j.PluginWrapper wrapper : started) {
                String pluginId = wrapper.getPluginId();
                List<HttpHandler> handlers = manager.getHttpHandlers(pluginId);
                if (handlers == null || handlers.isEmpty()) {
                    log.debug("插件 {} 无 HttpHandler", pluginId);
                    continue;
                }
                log.info("[{}] 插件 {}: 发现 {} 个 HttpHandler", idx++, pluginId, handlers.size());
                registerHandlers(pluginId, handlers);
            }
        } catch (Exception e) {
            log.error("初始化时发现并注册 HttpHandler 失败", e);
        }
    }

    public synchronized void registerHandlers(List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) return;
        log.info("运行时注册 {} 个 HttpHandler 实例", handlers.size());
        for (HttpHandler h : handlers) {
            registerHandlerInternal(null, (Object) h);
        }
        log.info("运行时注册完成，当前已注册处理器数: {}", registeredHandlers.size());
    }

    public synchronized void registerHandlers(String pluginId, List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) return;
        List<Object> list = pluginHandlers.computeIfAbsent(pluginId, k -> new ArrayList<>());
        int before = registeredHandlers.size();
        for (HttpHandler h : handlers) {
            registerHandlerInternal(pluginId, (Object) h);
            if (!list.contains(h)) list.add(h);
        }
        log.info("为 plugin {} 注册完成，新增处理器数: {} (总处理器数: {})", pluginId, registeredHandlers.size() - before, registeredHandlers.size());
    }

    public synchronized void unregisterHandlers(String pluginId) {
        if (pluginId == null) return;
        List<String> mappings = pluginMappings.remove(pluginId);
        if (mappings != null) {
            for (String info : mappings) {
                log.info("已移除映射元数据: {} (plugin={})", info, pluginId);
            }
        }

        List<Object> handlers = pluginHandlers.remove(pluginId);
        if (handlers != null) {
            for (Object h : handlers) {
                registeredHandlers.remove(h);
                log.info("已移除 HTTP 处理器记录: {} (plugin={})", h.getClass().getName(), pluginId);
            }
        }
        log.info("plugin {} 的 HTTP 处理器和映射已移除", pluginId);
    }

    public List<Object> getRegisteredHandlers() {
        return new ArrayList<>(registeredHandlers);
    }

    // internal helper: scan handler methods + record mappings
    private void registerHandlerInternal(String pluginId, Object handlerObject) {
        Class<?> handlerClass = handlerObject.getClass();
        if (!handlerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)) {
            log.warn("HTTP处理器 {} 未添加@RestController注解，跳过注册", handlerClass.getName());
            return;
        }

        boolean hasMapping = false;
        Method[] methods = handlerClass.getMethods();
        for (Method method : methods) {
            List<String> paths = new ArrayList<>();
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
                for (String p : method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class).value()) paths.add(normalizePath(p));
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
                for (String p : method.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class).value()) paths.add(normalizePath(p));
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) {
                for (String p : method.getAnnotation(org.springframework.web.bind.annotation.PutMapping.class).value()) paths.add(normalizePath(p));
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) {
                for (String p : method.getAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class).value()) paths.add(normalizePath(p));
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)) {
                for (String p : method.getAnnotation(org.springframework.web.bind.annotation.PatchMapping.class).value()) paths.add(normalizePath(p));
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)) {
                for (String p : method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()) paths.add(normalizePath(p));
            }

            if (paths.isEmpty()) continue;
            hasMapping = true;
            for (String path : paths) {
                String mappingStr = path; // simplified metadata: only path
                String pid = pluginId;
                if (pid == null) {
                    pid = resolvePluginIdFromClassLoader(handlerClass.getClassLoader());
                    if (pid == null) pid = "unknown";
                }
                pluginMappings.computeIfAbsent(pid, k -> new ArrayList<>()).add(mappingStr);
                log.debug("  记录映射元数据: {} -> {}#{} (plugin={})", method.getName(), path, handlerClass.getName(), pid);
            }
        }

        if (hasMapping) {
            if (!registeredHandlers.contains(handlerObject)) registeredHandlers.add(handlerObject);
            String pid = pluginId;
            if (pid == null) {
                pid = resolvePluginIdFromClassLoader(handlerClass.getClassLoader());
                if (pid == null) pid = "unknown";
            }
            pluginHandlers.computeIfAbsent(pid, k -> new ArrayList<>()).add(handlerObject);
            log.debug("HTTP处理器注册成功: {} (plugin={})", handlerClass.getName(), pid);
        } else {
            log.warn("HTTP处理器 {} 中未找到任何映射方法，跳过注册", handlerClass.getName());
        }
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
}
