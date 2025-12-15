package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Component
public class PluginHttpDispatcher {

    private static final Logger log = LoggerFactory.getLogger(PluginHttpDispatcher.class);

    private final OneBasePluginManager pluginManager;
    private final RequestMappingHandlerAdapter handlerAdapter;
    private final Map<String, MethodHandler> routes = new HashMap<>();

    /**
     * 构造函数
     *
     * @param pluginManager  插件管理器
     * @param handlerAdapter Spring MVC 的请求处理适配器（用于参数解析、类型转换等）
     */
    public PluginHttpDispatcher(OneBasePluginManager pluginManager,
                                RequestMappingHandlerAdapter handlerAdapter) {
        this.pluginManager = pluginManager;
        this.handlerAdapter = handlerAdapter;
        log.debug("PluginHttpDispatcher已创建");
    }

    /**
     * 初始化路由映射
     * 在所有Bean注入完成后执行，手动从pluginManager获取HttpHandler扩展点
     */
    @PostConstruct
    public void init() {
        log.info("=" .repeat(60));
        log.info("初始化插件HTTP路由分发器");
        
        List<HttpHandler> httpHandlers = pluginManager.getHttpHandlers();
        
        if (httpHandlers == null || httpHandlers.isEmpty()) {
            log.warn("未发现任何HttpHandler扩展点，可能插件尚未启动或没有定义HttpHandler");
            return;
        }
        
        for (HttpHandler handler : httpHandlers) {
            scanHandlerMethods((Object) handler);
        }

        log.info("HTTP路由分发器已就绪，共注册 {} 个路由", routes.size());
        log.info("=" .repeat(60));
    }

    /**
     * 扫描处理器中的映射方法
     *
     * @param handler 处理器对象
     */
    private void scanHandlerMethods(Object handler) {
        Class<?> handlerClass = handler.getClass();
        Method[] methods = handlerClass.getMethods();

        for (Method method : methods) {
            String[] paths = null;
            String httpMethod = null;

            // 检查@GetMapping
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[]{"/"};
                httpMethod = "GET";
            }
            // 检查@PostMapping
            else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[]{"/"};
                httpMethod = "POST";
            }
            // 检查@PutMapping
            else if (method.isAnnotationPresent(PutMapping.class)) {
                PutMapping mapping = method.getAnnotation(PutMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[]{"/"};
                httpMethod = "PUT";
            }
            // 检查@DeleteMapping
            else if (method.isAnnotationPresent(DeleteMapping.class)) {
                DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[]{"/"};
                httpMethod = "DELETE";
            }
            // 检查@PatchMapping
            else if (method.isAnnotationPresent(PatchMapping.class)) {
                PatchMapping mapping = method.getAnnotation(PatchMapping.class);
                paths = mapping.value().length > 0 ? mapping.value() : new String[]{"/"};
                httpMethod = "PATCH";
            }

            if (paths != null && httpMethod != null) {
                for (String path : paths) {
                    // 如果路径以 /plugin 开头，去掉这个前缀
                    // 因为 PluginHttpHandler 的 @RequestMapping("/plugin") 会自动处理这个前缀
                    String normalizedPath = path;
                    if (path.startsWith("/plugin")) {
                        normalizedPath = path.substring(7); // 去掉 "/plugin"
                    }
                    
                    String key = buildRouteKey(httpMethod, normalizedPath);
                    routes.put(key, new MethodHandler(handler, method));
                    log.debug("  ✓ 注册路由: {} {} -> {}.{}", httpMethod, normalizedPath, handlerClass.getSimpleName(), method.getName());
                }
            }
        }
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
                    "success", false
            );
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
                    "success", false
            );
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
    }
}
