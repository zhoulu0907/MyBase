package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件HTTP管理器（统一HTTP处理模块）
 * <p>
 * 整合原有的HttpHandlerRegistry、HttpRoutingManager、HttpHandlerInitializer三个类的职责：
 * 1. 启动时发现并注册插件的HttpHandler
 * 2. 运行时注册/注销插件的HTTP处理器
 * 3. 管理路由元数据（pluginId -> handlers/mappings）
 * 4. 支持代理模式（当前默认）和直接注册模式（预留扩展）
 * </p>
 * <p>
 * 设计原则：
 * - 高内聚：所有HTTP相关的注册、注销、元数据管理集中在一个类中
 * - 职责清晰：启动时发现、运行时管理、元数据维护分离明确
 * - 扩展友好：预留直接向Spring RequestMappingHandlerMapping注册的开关
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Slf4j
public class PluginHttpManager implements ApplicationContextAware {

    private final ObjectProvider<OneBasePluginManager> pluginManagerProvider;
    private final RequestMappingHandlerMapping handlerMapping; // optional, for direct registration mode
    private final PluginProperties pluginProperties;

    // 元数据管理：pluginId -> handler对象列表
    private final Map<String, List<Object>> pluginHandlers = new ConcurrentHashMap<>();
    // 元数据管理：pluginId -> 映射路径列表
    private final Map<String, List<String>> pluginMappings = new ConcurrentHashMap<>();
    // 所有已注册的处理器（跨插件）
    private final List<Object> registeredHandlers = new ArrayList<>();

    /**
     * 构造函数
     *
     * @param pluginManagerProvider 插件管理器提供者
     * @param handlerMapping        Spring MVC的RequestMappingHandlerMapping（可选）
     * @param pluginProperties      插件配置属性
     */
    public PluginHttpManager(ObjectProvider<OneBasePluginManager> pluginManagerProvider,
            @Nullable RequestMappingHandlerMapping handlerMapping,
            PluginProperties pluginProperties) {
        this.pluginManagerProvider = pluginManagerProvider;
        this.handlerMapping = handlerMapping;
        this.pluginProperties = pluginProperties;
    }

    /**
     * 启动时初始化：发现并注册已启动插件的HttpHandler
     */
    @PostConstruct
    public void init() {
        if (!pluginProperties.isEnabled()) {
            log.info("插件系统已禁用，跳过PluginHttpManager初始化");
            return;
        }

        OneBasePluginManager manager = pluginManagerProvider.getIfAvailable();
        if (manager == null) {
            log.warn("OneBasePluginManager尚不可用，跳过启动时HttpHandler发现");
            return;
        }

        try {
            List<org.pf4j.PluginWrapper> started = manager.getStartedPlugins();
            if (started == null || started.isEmpty()) {
                log.debug("未发现已启动的插件，跳过初始HttpHandler注册");
                return;
            }

            int idx = 1;
            for (org.pf4j.PluginWrapper wrapper : started) {
                String pluginId = wrapper.getPluginId();
                List<HttpHandler> handlers = manager.getHttpHandlers(pluginId);
                if (handlers == null || handlers.isEmpty()) {
                    log.debug("插件 {} 无HttpHandler", pluginId);
                    continue;
                }
                log.info("[{}] 插件 {}: 发现 {} 个HttpHandler", idx++, pluginId, handlers.size());
                registerPlugin(pluginId, handlers);
            }
        } catch (Exception e) {
            log.error("启动时发现并注册HttpHandler失败", e);
        }
    }

    /**
     * 注册指定插件的HTTP处理器
     *
     * @param pluginId 插件ID
     * @param handlers HTTP处理器列表
     */
    public synchronized void registerPlugin(String pluginId, List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            log.debug("插件 {} 无HttpHandler需要注册", pluginId);
            return;
        }

        List<Object> list = pluginHandlers.computeIfAbsent(pluginId, k -> new ArrayList<>());
        int before = registeredHandlers.size();

        for (HttpHandler handler : handlers) {
            registerHandlerInternal(pluginId, (Object) handler);
            if (!list.contains(handler)) {
                list.add(handler);
            }
        }

        log.info("为插件 {} 注册完成，新增处理器数: {} (总处理器数: {})",
                pluginId, registeredHandlers.size() - before, registeredHandlers.size());
    }

    /**
     * 注销指定插件的HTTP处理器
     *
     * @param pluginId 插件ID
     */
    public synchronized void unregisterPlugin(String pluginId) {
        if (pluginId == null) {
            return;
        }

        // 1. 移除映射元数据
        List<String> mappings = pluginMappings.remove(pluginId);
        if (mappings != null) {
            for (String info : mappings) {
                log.info("已移除映射元数据: {} (plugin={})", info, pluginId);
            }
        }

        // 2. 移除处理器记录
        List<Object> handlers = pluginHandlers.remove(pluginId);
        if (handlers != null) {
            for (Object h : handlers) {
                // 从 Spring MVC 注销
                unregisterFromSpringMvc(h);

                registeredHandlers.remove(h);
                log.info("已移除HTTP处理器记录: {} (plugin={})", h.getClass().getName(), pluginId);
            }
        }

        log.info("插件 {} 的HTTP处理器和映射已移除", pluginId);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 从 Spring MVC 注销处理器
     */
    private void unregisterFromSpringMvc(Object handler) {
        if (handlerMapping == null) {
            log.debug("handlerMapping为空，无法从Spring MVC注销");
            return;
        }
        try {
            Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
            List<RequestMappingInfo> toRemove = new ArrayList<>();
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
                Object bean = entry.getValue().getBean();
                Object beanInstance = bean;

                // 如果是Bean名称，则解析为实例
                if (bean instanceof String && applicationContext != null) {
                    try {
                        beanInstance = applicationContext.getBean((String) bean);
                    } catch (Exception ignore) {
                        // Bean可能已不存在
                        continue;
                    }
                }

                // 比较 bean 实例
                boolean match = (beanInstance == handler);
                if (!match && beanInstance != null && handler != null) {
                    // 尝试类名匹配 (Fallback: 适用于实例不一致但类一致的情况，例如代理)
                    if (beanInstance.getClass().getName().equals(handler.getClass().getName())) {
                        match = true;
                    }
                }

                if (match) {
                    toRemove.add(entry.getKey());
                }
            }

            for (RequestMappingInfo info : toRemove) {
                handlerMapping.unregisterMapping(info);
                log.info("已从 Spring MVC 注销路由: {}", info);
            }
        } catch (Exception e) {
            log.error("从 Spring MVC 注销处理器失败", e);
        }
    }

    /**
     * 运行时注册HTTP处理器（不指定pluginId，从ClassLoader推断）
     *
     * @param handlers HTTP处理器列表
     */
    public synchronized void registerHandlers(List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return;
        }

        log.info("运行时注册 {} 个HttpHandler实例", handlers.size());
        for (HttpHandler h : handlers) {
            registerHandlerInternal(null, (Object) h);
        }
        log.info("运行时注册完成，当前已注册处理器数: {}", registeredHandlers.size());
    }

    /**
     * 获取所有已注册的处理器
     *
     * @return 处理器列表
     */
    public List<Object> getRegisteredHandlers() {
        return new ArrayList<>(registeredHandlers);
    }

    /**
     * 获取指定插件的处理器
     *
     * @param pluginId 插件ID
     * @return 处理器列表
     */
    public List<Object> getPluginHandlers(String pluginId) {
        List<Object> handlers = pluginHandlers.get(pluginId);
        return handlers != null ? new ArrayList<>(handlers) : new ArrayList<>();
    }

    /**
     * 获取指定插件的映射路径
     *
     * @param pluginId 插件ID
     * @return 映射路径列表
     */
    public List<String> getPluginMappings(String pluginId) {
        List<String> mappings = pluginMappings.get(pluginId);
        return mappings != null ? new ArrayList<>(mappings) : new ArrayList<>();
    }

    /**
     * 获取所有插件的映射元数据
     *
     * @return Map<pluginId, List<mappingPath>>
     */
    public Map<String, List<String>> getAllPluginMappings() {
        Map<String, List<String>> result = new ConcurrentHashMap<>();
        pluginMappings.forEach((k, v) -> result.put(k, new ArrayList<>(v)));
        return result;
    }

    // ========== 内部方法 ==========

    /**
     * 注册单个处理器的内部实现
     *
     * @param pluginId      插件ID（可为null，将从ClassLoader推断）
     * @param handlerObject 处理器对象
     */
    private void registerHandlerInternal(String pluginId, Object handlerObject) {
        Class<?> handlerClass = handlerObject.getClass();

        // 检查是否为@RestController
        if (!handlerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)) {
            log.warn("HTTP处理器 {} 未添加@RestController注解，跳过注册", handlerClass.getName());
            return;
        }

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

        boolean hasMapping = false;
        Method[] methods = handlerClass.getMethods();

        // 2. 遍历方法，提取路径并组合
        for (Method method : methods) {
            List<String> methodPaths = extractMethodPaths(method);
            if (methodPaths.isEmpty()) {
                continue;
            }

            hasMapping = true;
            for (String methodPath : methodPaths) {
                // 3. 组合类级别前缀和方法级别路径
                String fullPath = combinePath(classLevelPrefix, methodPath);

                // 记录映射元数据（不直接向Spring注册，使用代理模式）
                String pid = pluginId != null ? pluginId
                        : resolvePluginIdFromClassLoader(handlerClass.getClassLoader());
                if (pid == null) {
                    pid = "unknown";
                }
                pluginMappings.computeIfAbsent(pid, k -> new ArrayList<>()).add(fullPath);
                log.debug("  记录映射元数据: {} -> {}#{} (plugin={})",
                        fullPath, handlerClass.getName(), method.getName(), pid);
            }
        }

        if (hasMapping) {
            if (!registeredHandlers.contains(handlerObject)) {
                registeredHandlers.add(handlerObject);
            }

            String pid = pluginId != null ? pluginId : resolvePluginIdFromClassLoader(handlerClass.getClassLoader());
            if (pid == null) {
                pid = "unknown";
            }
            pluginHandlers.computeIfAbsent(pid, k -> new ArrayList<>()).add(handlerObject);
            log.debug("HTTP处理器注册成功: {} (plugin={})", handlerClass.getName(), pid);
        } else {
            log.warn("HTTP处理器 {} 中未找到任何映射方法，跳过注册", handlerClass.getName());
        }
    }

    /**
     * 提取方法的映射路径（仅方法级别，不包含类级别前缀）
     *
     * @param method 方法对象
     * @return 映射路径列表
     */
    private List<String> extractMethodPaths(Method method) {
        List<String> paths = new ArrayList<>();

        // 支持的映射注解
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
            for (String p : method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class).value()) {
                paths.add(normalizePath(p));
            }
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
            for (String p : method.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class).value()) {
                paths.add(normalizePath(p));
            }
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) {
            for (String p : method.getAnnotation(org.springframework.web.bind.annotation.PutMapping.class).value()) {
                paths.add(normalizePath(p));
            }
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) {
            for (String p : method.getAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class).value()) {
                paths.add(normalizePath(p));
            }
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)) {
            for (String p : method.getAnnotation(org.springframework.web.bind.annotation.PatchMapping.class).value()) {
                paths.add(normalizePath(p));
            }
        }
        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)) {
            for (String p : method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class)
                    .value()) {
                paths.add(normalizePath(p));
            }
        }

        return paths;
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
     * 规范化路径（确保以/开头）
     *
     * @param path 原始路径
     * @return 规范化后的路径
     */
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        return path.startsWith("/") ? path : ("/" + path);
    }

    /**
     * 从ClassLoader解析pluginId
     *
     * @param cl ClassLoader
     * @return pluginId，如果无法解析则返回null
     */
    private String resolvePluginIdFromClassLoader(ClassLoader cl) {
        if (cl == null) {
            return null;
        }
        try {
            Method m = cl.getClass().getMethod("getPluginId");
            Object val = m.invoke(cl);
            return val != null ? String.valueOf(val) : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
