package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件 Controller 动态注册器
 * <p>
 * 负责将插件的 @RestController 动态注册到 Spring MVC 的 RequestMappingHandlerMapping 中，
 * 实现插件路由的热插拔。完全复用 Spring MVC 的参数解析机制，无需手动分发。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-24
 */
@Slf4j
public class PluginControllerRegistrar {

    private final RequestMappingHandlerMapping handlerMapping;

    /**
     * 记录每个插件注册的路由信息，用于卸载时精确清理
     * Key: pluginId, Value: List<RequestMappingInfo>
     */
    private final Map<String, List<RequestMappingInfo>> pluginMappings = new ConcurrentHashMap<>();

    /**
     * 记录每个 Controller 类名对应的 pluginId，用于热重载场景
     * Key: Controller 类名, Value: pluginId
     */
    private final Map<String, String> controllerToPlugin = new ConcurrentHashMap<>();

    public PluginControllerRegistrar(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * 注册插件的所有 HttpHandler Controller
     *
     * @param pluginId 插件ID
     * @param handlers HttpHandler 列表
     */
    public void registerControllers(String pluginId, List<HttpHandler> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            log.debug("插件 {} 没有 HttpHandler，跳过注册", pluginId);
            return;
        }

        log.info("开始注册插件 {} 的 {} 个 Controller", pluginId, handlers.size());

        List<RequestMappingInfo> mappingInfos = new ArrayList<>();

        for (HttpHandler handler : handlers) {
            try {
                // 校验路由前缀
                validateRoutePrefix(pluginId, handler);

                // 注册 Controller
                List<RequestMappingInfo> infos = registerController(pluginId, handler);
                mappingInfos.addAll(infos);

                // 记录类名与插件的关联
                controllerToPlugin.put(handler.getClass().getName(), pluginId);

                log.info("成功注册插件 {} 的 Controller: {}", pluginId, handler.getClass().getName());
            } catch (Exception e) {
                log.error("注册插件 {} 的 Controller 失败: {}", pluginId, handler.getClass().getName(), e);
                // 注册失败时回滚已注册的路由
                unregisterControllers(pluginId);
                throw new RuntimeException("插件 Controller 注册失败: " + e.getMessage(), e);
            }
        }

        // 记录插件的所有路由映射
        pluginMappings.put(pluginId, mappingInfos);
        log.info("插件 {} 共注册 {} 个路由映射", pluginId, mappingInfos.size());
    }

    /**
     * 注册单个 Controller（用于热重载）
     *
     * @param pluginId   插件ID
     * @param controller Controller 实例
     * @return 注册的 RequestMappingInfo 列表
     */
    protected List<RequestMappingInfo> registerController(String pluginId, Object controller) throws Exception {
        List<RequestMappingInfo> mappingInfos = new ArrayList<>();
        Class<?> controllerClass = controller.getClass();

        // 获取类级别的 @RequestMapping
        RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
        String[] classPaths = classMapping != null ? classMapping.value() : new String[] {};

        // 遍历所有方法
        for (Method method : controllerClass.getMethods()) {
            RequestMappingInfo mappingInfo = createMappingInfo(method, classPaths);
            if (mappingInfo != null) {
                // 检查路由是否已经注册（防止与 Spring Boot 自动注册冲突或热重载）
                if (handlerMapping.getHandlerMethods().containsKey(mappingInfo)) {
                    log.debug("路由 {} 已存在，跳过注册",
                            mappingInfo.getPatternValues());
                    mappingInfos.add(mappingInfo); // 仍然记录，用于后续卸载
                    continue;
                }

                // 注册到 Spring MVC
                handlerMapping.registerMapping(mappingInfo, controller, method);
                mappingInfos.add(mappingInfo);

                log.debug("注册路由: {} -> {}.{}",
                        mappingInfo.getPatternValues(),
                        controllerClass.getSimpleName(),
                        method.getName());
            }
        }

        return mappingInfos;
    }

    /**
     * 创建 RequestMappingInfo
     */
    private RequestMappingInfo createMappingInfo(Method method, String[] classPaths) {
        // 检查方法上的各种 Mapping 注解
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);

        String[] paths = null;
        RequestMethod[] methods = null;

        if (requestMapping != null) {
            paths = requestMapping.value();
            methods = requestMapping.method();
        } else if (getMapping != null) {
            paths = getMapping.value();
            methods = new RequestMethod[] { RequestMethod.GET };
        } else if (postMapping != null) {
            paths = postMapping.value();
            methods = new RequestMethod[] { RequestMethod.POST };
        } else if (putMapping != null) {
            paths = putMapping.value();
            methods = new RequestMethod[] { RequestMethod.PUT };
        } else if (deleteMapping != null) {
            paths = deleteMapping.value();
            methods = new RequestMethod[] { RequestMethod.DELETE };
        } else if (patchMapping != null) {
            paths = patchMapping.value();
            methods = new RequestMethod[] { RequestMethod.PATCH };
        }

        if (paths == null) {
            return null;
        }

        // 组合类级别和方法级别的路径
        Set<String> combinedPaths = new HashSet<>();
        if (classPaths.length == 0) {
            combinedPaths.addAll(Arrays.asList(paths));
        } else {
            for (String classPath : classPaths) {
                for (String methodPath : paths) {
                    combinedPaths.add(combinePath(classPath, methodPath));
                }
            }
        }

        // 构建 RequestMappingInfo
        return RequestMappingInfo
                .paths(combinedPaths.toArray(new String[0]))
                .methods(methods)
                .build();
    }

    /**
     * 组合路径
     */
    private String combinePath(String prefix, String suffix) {
        if (prefix == null || prefix.isEmpty()) {
            return suffix;
        }
        if (suffix == null || suffix.isEmpty()) {
            return prefix;
        }

        String normalizedPrefix = prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
        String normalizedSuffix = suffix.startsWith("/") ? suffix : "/" + suffix;

        return normalizedPrefix + normalizedSuffix;
    }

    /**
     * 校验路由前缀是否符合规范
     * <p>
     * 注意：DEV 模式下使用虚拟插件ID "dev-mode-plugin"，
     * 但实际插件的路由可能使用不同的前缀（如 hello-plugin），
     * 因此 DEV 模式下只验证路由是否以 /plugin/ 开头，不强制匹配 pluginId。
     * </p>
     * <p>
     * 支持两种路由定义方式：
     * <ul>
     * <li>类级别 @RequestMapping + 方法级别注解</li>
     * <li>仅方法级别注解（完整路径）</li>
     * </ul>
     * </p>
     */
    private void validateRoutePrefix(String pluginId, Object controller) {
        Class<?> controllerClass = controller.getClass();
        RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);

        // DEV 模式下只验证路由以 /plugin/ 开头
        boolean isDevMode = "dev-mode-plugin".equals(pluginId);
        String requiredPrefix = isDevMode ? "/plugin/" : "/plugin/" + pluginId;

        // 如果有类级别的 @RequestMapping，验证它
        if (classMapping != null && classMapping.value().length > 0) {
            for (String path : classMapping.value()) {
                if (!path.startsWith(requiredPrefix)) {
                    throw new IllegalArgumentException(
                            String.format("插件 %s 的路由前缀不符合规范: %s，必须以 %s 开头",
                                    pluginId, path, requiredPrefix));
                }
            }
            return; // 类级别验证通过，无需检查方法级别
        }

        // 如果没有类级别的 @RequestMapping，检查方法级别的路由
        // 至少要有一个方法定义了路由
        boolean hasValidRoute = false;
        for (java.lang.reflect.Method method : controllerClass.getMethods()) {
            String[] paths = getMethodPaths(method);
            if (paths != null && paths.length > 0) {
                for (String path : paths) {
                    if (path.startsWith(requiredPrefix)) {
                        hasValidRoute = true;
                    } else if (!path.isEmpty()) {
                        throw new IllegalArgumentException(
                                String.format("插件 %s 的路由前缀不符合规范: %s，必须以 %s 开头",
                                        pluginId, path, requiredPrefix));
                    }
                }
            }
        }

        if (!hasValidRoute) {
            log.warn("插件 {} 的 Controller {} 没有定义有效的路由", pluginId, controllerClass.getName());
        }
    }

    /**
     * 获取方法上的路由路径
     */
    private String[] getMethodPaths(java.lang.reflect.Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null)
            return requestMapping.value();

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null)
            return getMapping.value();

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null)
            return postMapping.value();

        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null)
            return putMapping.value();

        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null)
            return deleteMapping.value();

        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null)
            return patchMapping.value();

        return null;
    }

    /**
     * 注销插件的所有 Controller
     *
     * @param pluginId 插件ID
     */
    public void unregisterControllers(String pluginId) {
        List<RequestMappingInfo> mappingInfos = pluginMappings.remove(pluginId);
        if (mappingInfos == null || mappingInfos.isEmpty()) {
            log.debug("插件 {} 没有已注册的路由，跳过注销", pluginId);
            return;
        }

        log.info("开始注销插件 {} 的 {} 个路由映射", pluginId, mappingInfos.size());

        for (RequestMappingInfo mappingInfo : mappingInfos) {
            try {
                handlerMapping.unregisterMapping(mappingInfo);
                log.debug("注销路由: {}", mappingInfo.getPatternValues());
            } catch (Exception e) {
                log.warn("注销路由失败: {}", mappingInfo.getPatternValues(), e);
            }
        }

        // 清理类名映射
        controllerToPlugin.entrySet().removeIf(entry -> entry.getValue().equals(pluginId));

        log.info("插件 {} 的路由已全部注销", pluginId);
    }

    /**
     * 按 Controller 类名注销路由（用于热重载）
     *
     * @param className Controller 完整类名
     */
    public void unregisterControllerByClassName(String className) {
        String pluginId = controllerToPlugin.get(className);
        if (pluginId == null) {
            log.debug("Controller {} 未注册，跳过注销", className);
            return;
        }

        log.info("热重载：注销 Controller {}", className);

        // 直接遍历 handlerMapping 中的所有路由,按 className 匹配
        // 不依赖 RequestMappingInfo.equals(),更可靠
        Map<RequestMappingInfo, HandlerMethod> allMethods = new HashMap<>(handlerMapping.getHandlerMethods());

        List<RequestMappingInfo> unregistered = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : allMethods.entrySet()) {
            if (entry.getValue().getBeanType().getName().equals(className)) {
                handlerMapping.unregisterMapping(entry.getKey());
                unregistered.add(entry.getKey());
                log.debug("注销路由: {}", entry.getKey().getPatternValues());
            }
        }

        // 清理 pluginMappings (按路径匹配,不依赖 equals)
        List<RequestMappingInfo> mappingInfos = pluginMappings.get(pluginId);
        if (mappingInfos != null) {
            mappingInfos.removeIf(info -> unregistered.stream()
                    .anyMatch(unregisteredInfo -> info.getPatternValues().equals(unregisteredInfo.getPatternValues())));

            if (mappingInfos.isEmpty()) {
                pluginMappings.remove(pluginId);
                log.debug("插件 {} 的所有路由已注销，清理 pluginMappings", pluginId);
            }
        }

        controllerToPlugin.remove(className);
    }

    /**
     * 获取插件已注册的路由数量
     */
    public int getRegisteredMappingCount(String pluginId) {
        List<RequestMappingInfo> mappingInfos = pluginMappings.get(pluginId);
        return mappingInfos != null ? mappingInfos.size() : 0;
    }
}
