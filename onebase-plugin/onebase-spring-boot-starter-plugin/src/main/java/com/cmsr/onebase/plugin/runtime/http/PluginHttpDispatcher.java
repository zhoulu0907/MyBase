package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件HTTP请求分发器
 * <p>
 * 负责接收代理处理器转发的请求，动态查找并调用相应的插件处理器方法。
 * 使用 Spring MVC 的 RequestMappingHandlerAdapter 处理请求，自动支持所有 Spring MVC 特性。
 * 当插件系统禁用时（enabled=false），跳过路由初始化。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Slf4j
public class PluginHttpDispatcher {

    private final OneBasePluginManager pluginManager;
    private final RequestMappingHandlerAdapter handlerAdapter;
    private final PluginProperties pluginProperties;
    private final Map<String, MethodHandler> routes = new HashMap<>();
    // 记录每个 pluginId 注册的路由 key 列表，便于卸载时清理
    private final Map<String, java.util.List<String>> pluginRoutes = new java.util.HashMap<>();

    /**
     * 构造函数
     *
     * @param pluginManager    插件管理器
     * @param handlerAdapter   Spring MVC 的请求处理适配器（用于参数解析、类型转换等）
     * @param pluginProperties 插件配置属性
     */
    public PluginHttpDispatcher(OneBasePluginManager pluginManager,
            RequestMappingHandlerAdapter handlerAdapter,
            PluginProperties pluginProperties) {
        this.pluginManager = pluginManager;
        this.handlerAdapter = handlerAdapter;
        this.pluginProperties = pluginProperties;
        log.debug("PluginHttpDispatcher已创建");
    }

    /**
     * 初始化路由映射
     * 监听ApplicationReadyEvent事件，在应用完全启动后执行，确保所有插件都已加载和启动
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 检查插件系统是否启用
        if (!pluginProperties.isEnabled()) {
            log.info("插件系统已禁用，跳过HTTP路由分发器初始化");
            return;
        }

        log.info("=".repeat(60));
        log.info("初始化插件HTTP路由分发器");

        // 为了在卸载时能精确清理路由，我们按已启动的插件逐个查询其 HttpHandler 并记录所属 pluginId
        try {
            java.util.List<org.pf4j.PluginWrapper> started = pluginManager.getStartedPlugins();
            if (started == null || started.isEmpty()) {
                log.warn("未发现已启动的插件，可能插件尚未启动或没有定义HttpHandler");
                return;
            }

            int idx = 1;
            for (org.pf4j.PluginWrapper wrapper : started) {
                String pluginId = wrapper.getPluginId();
                List<HttpHandler> handlers = pluginManager.getHttpHandlers(pluginId);
                if (handlers == null || handlers.isEmpty()) {
                    log.debug("插件 {} 无 HttpHandler", pluginId);
                    continue;
                }

                log.info("[{}] 插件 {}: 发现 {} 个 HttpHandler", idx++, pluginId, handlers.size());
                for (HttpHandler handler : handlers) {
                    try {
                        registerHandlers(pluginId, java.util.List.of(handler));
                    } catch (Exception e) {
                        log.error("注册插件 {} 的 HttpHandler 失败: {}", pluginId, handler.getClass().getName(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化时按 pluginId 注册 HttpHandler 失败", e);
        }

        log.info("HTTP路由分发器已就绪，共注册 {} 个路由", routes.size());
        log.info("=".repeat(60));
    }

    /**
     * 在运行时注册一组 HttpHandler（用于动态加载的插件）
     * <p>
     * 该方法会扫描传入的处理器并将对应的路由注册到内部路由表中，适用于插件在应用启动后被动态加载/启动的场景。
     * </p>
     *
     * @param handlers 要注册的处理器列表
     */
    public synchronized void registerHandlers(List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            log.debug("没有要注册的运行时 HttpHandler");
            return;
        }

        log.info("运行时注册 {} 个 HttpHandler 扩展点实例", handlers.size());
        for (HttpHandler handler : handlers) {
            try {
                // 普通运行时注册，不记录 pluginId 归属
                scanHandlerMethods((Object) handler);
            } catch (Exception e) {
                log.error("注册 HttpHandler 时出错: {}", handler.getClass().getName(), e);
            }
        }

        log.info("运行时注册完成，当前已注册路由数: {}", routes.size());
    }

    /**
     * 为指定 pluginId 注册 handlers 并记录注册的路由键，便于后续卸载清理
     */
    public synchronized void registerHandlers(String pluginId, List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            log.debug("没有要注册的运行时 HttpHandler for {}", pluginId);
            return;
        }

        java.util.List<String> registeredKeys = pluginRoutes.computeIfAbsent(pluginId,
                k -> new java.util.ArrayList<>());
        int before = routes.size();
        for (HttpHandler handler : handlers) {
            try {
                // scanHandlerMethods 返回本次扫描新增的 route keys
                java.util.List<String> newKeys = scanHandlerMethods((Object) handler);
                if (newKeys != null && !newKeys.isEmpty()) {
                    for (String k : newKeys) {
                        if (!registeredKeys.contains(k)) {
                            registeredKeys.add(k);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("注册 HttpHandler 时出错: {}", handler.getClass().getName(), e);
            }
        }

        log.info("为 plugin {} 注册完成，新增路由数: {} (总路由数: {})", pluginId, routes.size() - before, routes.size());
    }

    /**
     * 注销指定 pluginId 注册的路由
     */
    public synchronized void unregisterHandlers(String pluginId) {
        java.util.List<String> keys = pluginRoutes.remove(pluginId);
        if (keys == null || keys.isEmpty()) {
            log.debug("plugin {} 无已注册路由需要移除", pluginId);
            return;
        }

        for (String k : keys) {
            routes.remove(k);
            log.info("已从路由表移除: {} -> {}", pluginId, k);
        }
        log.info("plugin {} 的路由已移除，当前路由总数: {}", pluginId, routes.size());
    }

    /**
     * 按类名卸载路由（用于热重载）
     * <p>
     * 移除所有由指定类注册的路由，用于热重载场景。
     * </p>
     *
     * @param className 完整类名
     */
    public synchronized void unregisterHandlerByClassName(String className) {
        if (className == null || className.isEmpty()) {
            log.warn("类名为空，跳过卸载路由");
            return;
        }

        java.util.List<String> keysToRemove = new java.util.ArrayList<>();

        // 遍历所有路由，找到由该类注册的路由
        for (Map.Entry<String, MethodHandler> entry : routes.entrySet()) {
            MethodHandler handler = entry.getValue();
            if (handler.getHandler().getClass().getName().equals(className)) {
                keysToRemove.add(entry.getKey());
            }
        }

        // 移除找到的路由
        if (!keysToRemove.isEmpty()) {
            for (String key : keysToRemove) {
                routes.remove(key);
                log.debug("已卸载路由: {} (类: {})", key, className);
            }
            log.info("已卸载类 {} 的 {} 个路由，当前路由总数: {}", className, keysToRemove.size(), routes.size());
        } else {
            log.debug("未找到类 {} 注册的路由", className);
        }
    }

    /**
     * 为指定 pluginId 注册单个 handler（用于热重载）
     * <p>
     * 简化版本的注册方法，用于热重载场景。
     * </p>
     *
     * @param pluginId 插件ID
     * @param handler  处理器对象
     */
    public synchronized void registerHandler(String pluginId, Object handler) {
        if (handler == null) {
            log.warn("处理器为空，跳过注册");
            return;
        }

        java.util.List<String> registeredKeys = pluginRoutes.computeIfAbsent(pluginId,
                k -> new java.util.ArrayList<>());
        int before = routes.size();

        try {
            java.util.List<String> newKeys = scanHandlerMethods(handler);
            if (newKeys != null && !newKeys.isEmpty()) {
                for (String k : newKeys) {
                    if (!registeredKeys.contains(k)) {
                        registeredKeys.add(k);
                    }
                }
            }
            log.info("为 plugin {} 注册 handler {}，新增路由数: {}", pluginId, handler.getClass().getSimpleName(),
                    routes.size() - before);
        } catch (Exception e) {
            log.error("注册 handler 时出错: {}", handler.getClass().getName(), e);
        }
    }

    /**
     * 扫描处理器中的映射方法
     *
     * @param handler 处理器对象
     */
    private java.util.List<String> scanHandlerMethods(Object handler) {
        Class<?> handlerClass = handler.getClass();
        Method[] methods = handlerClass.getMethods();
        java.util.List<String> addedKeys = new java.util.ArrayList<>();

        // 1. 获取类级别的@RequestMapping前缀
        String classLevelPrefix = "";
        if (handlerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)) {
            org.springframework.web.bind.annotation.RequestMapping classMapping = handlerClass
                    .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
            String[] classPaths = classMapping.value().length > 0 ? classMapping.value() : classMapping.path();
            if (classPaths.length > 0) {
                classLevelPrefix = classPaths[0];
            }
        }

        log.debug("  类级别前缀: {}", classLevelPrefix.isEmpty() ? "(无)" : classLevelPrefix);

        // 2. 遍历方法级别的映射注解
        for (Method method : methods) {
            String[] paths = null;
            String httpMethod = null;

            // 检查@GetMapping
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[] { "/" };
                httpMethod = "GET";
            }
            // 检查@PostMapping
            else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[] { "/" };
                httpMethod = "POST";
            }
            // 检查@PutMapping
            else if (method.isAnnotationPresent(PutMapping.class)) {
                PutMapping mapping = method.getAnnotation(PutMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[] { "/" };
                httpMethod = "PUT";
            }
            // 检查@DeleteMapping
            else if (method.isAnnotationPresent(DeleteMapping.class)) {
                DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[] { "/" };
                httpMethod = "DELETE";
            }
            // 检查@PatchMapping
            else if (method.isAnnotationPresent(PatchMapping.class)) {
                PatchMapping mapping = method.getAnnotation(PatchMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[] { "/" };
                httpMethod = "PATCH";
            }

            if (paths != null && httpMethod != null) {
                for (String methodPath : paths) {
                    // 3. 组合类级别前缀和方法级别路径
                    String fullPath = combinePath(classLevelPrefix, methodPath);

                    // 4. 如果路径以 /plugin 开头，去掉这个前缀
                    // 因为 PluginHttpHandler 的 @RequestMapping("/plugin") 会自动处理这个前缀
                    String normalizedPath = fullPath;
                    if (fullPath.startsWith("/plugin")) {
                        normalizedPath = fullPath.substring(7); // 去掉 "/plugin"
                    }

                    String key = buildRouteKey(httpMethod, normalizedPath);
                    MethodHandler oldHandler = routes.get(key);
                    if (oldHandler != null) {
                        log.warn("  ⚠ 路由冲突: {} {} 被覆盖!", httpMethod, normalizedPath);
                        log.warn("    旧Handler: {} (哈希码: {})",
                                oldHandler.getHandler().getClass().getName(),
                                System.identityHashCode(oldHandler.getHandler()));
                        log.warn("    新Handler: {} (哈希码: {})",
                                handler.getClass().getName(),
                                System.identityHashCode(handler));
                    }
                    boolean replaced = routes.containsKey(key);
                    routes.put(key, new MethodHandler(handler, method));
                    if (!replaced) {
                        addedKeys.add(key);
                    }
                    log.info("  ✓ 注册路由: {} {} -> {}.{} (Handler哈希码: {})",
                            httpMethod, normalizedPath, handlerClass.getSimpleName(),
                            method.getName(), System.identityHashCode(handler));
                }
            }
        }
        log.info("  Handler扫描完成: {} (本次新增 {} 个路由)", handlerClass.getName(), addedKeys.size());
        return addedKeys;
    }

    /**
     * 组合类级别前缀和方法级别路径
     *
     * @param prefix 类级别前缀
     * @param suffix 方法级别路径
     * @return 组合后的完整路径
     */
    private String combinePath(String prefix, String suffix) {
        if (prefix == null || prefix.isEmpty()) {
            return suffix;
        }
        if (suffix == null || suffix.isEmpty()) {
            return prefix;
        }

        // 确保前缀以 / 开头
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        // 移除前缀末尾的 /
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        // 确保后缀以 / 开头
        if (!suffix.startsWith("/")) {
            suffix = "/" + suffix;
        }

        return prefix + suffix;
    }

    /**
     * 构建路由键
     *
     * @param httpMethod HTTP方法
     * @param path       请求路径
     * @return 路由键
     */
    private String buildRouteKey(String httpMethod, String path) {
        return httpMethod + " " + path;
    }

    /**
     * 分发请求到对应的插件处理器方法
     * <p>
     * 使用 Spring MVC 的 RequestMappingHandlerAdapter 处理请求，
     * 自动支持 @RequestParam、@RequestBody、@RequestHeader、@PathVariable 等所有注解。
     * </p>
     *
     * @param request    HTTP请求对象
     * @param response   HTTP响应对象
     * @param path       请求路径（不含/plugin前缀）
     * @param httpMethod HTTP方法
     * @return 处理结果
     */
    public Object dispatch(HttpServletRequest request, HttpServletResponse response,
            String path, String httpMethod) {
        log.debug("分发请求: {} {}", httpMethod, path);

        // 首先尝试精确匹配
        String exactKey = buildRouteKey(httpMethod, path);
        log.debug("尝试精确匹配键: {}", exactKey);
        MethodHandler handler = routes.get(exactKey);

        // 如果没有精确匹配，尝试前缀匹配
        if (handler == null) {
            log.debug("精确匹配失败，尝试前缀匹配");
            handler = findMatchingHandler(httpMethod, path);
        }

        if (handler == null) {
            log.warn("未找到匹配的处理器: {} {}", httpMethod, path);
            log.debug("当前已注册的路由:");
            routes.keySet().forEach(key -> log.debug("  - {}", key));
            return Map.of(
                    "code", 404,
                    "message", "请求路径不存在: " + path,
                    "success", false);
        }

        try {
            // 使用 Spring MVC 的 HandlerMethod 和 RequestMappingHandlerAdapter 处理请求
            // 这会自动处理所有参数解析、类型转换、数据绑定、验证等
            HandlerMethod handlerMethod = new HandlerMethod(handler.controller, handler.method);
            return handlerAdapter.handle(request, response, handlerMethod);
        } catch (Exception e) {
            log.error("执行处理器方法时出错: {} {}", httpMethod, path, e);
            return Map.of(
                    "code", 500,
                    "message", "执行错误: " + e.getMessage(),
                    "success", false);
        }
    }

    /**
     * 前缀匹配处理器
     *
     * @param httpMethod HTTP方法
     * @param path       请求路径
     * @return 匹配的处理器，或null
     */
    private MethodHandler findMatchingHandler(String httpMethod, String path) {
        for (Map.Entry<String, MethodHandler> entry : routes.entrySet()) {
            String route = entry.getKey();
            String[] parts = route.split(" ", 2);
            if (parts.length == 2) {
                String method = parts[0];
                String routePath = parts[1];

                // 方法匹配
                if (!method.equals(httpMethod)) {
                    continue;
                }

                // 路径匹配：完整匹配或routePath是前缀
                if (path.equals(routePath) || path.startsWith(routePath + "/")) {
                    log.debug("  前缀匹配成功: {} {} 匹配 {} {}", httpMethod, path, method, routePath);
                    return entry.getValue();
                }
            }
        }
        log.debug("  未找到匹配的前缀路由");
        return null;
    }

    /**
     * 方法处理器包装类
     */
    private static class MethodHandler {
        final Object controller;
        final Method method;

        MethodHandler(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getHandler() {
            return controller;
        }

        public Method getMethod() {
            return method;
        }
    }
}
