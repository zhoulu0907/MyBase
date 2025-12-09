package com.cmsr.onebase.plugin.runtime.executor;

import com.cmsr.onebase.plugin.api.CustomFunction;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义函数执行器
 * <p>
 * 负责执行插件提供的自定义函数，供公式引擎调用。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Component
public class CustomFunctionExecutor {

    private static final Logger log = LoggerFactory.getLogger(CustomFunctionExecutor.class);

    private final OneBasePluginManager pluginManager;
    private final PluginContextFactory contextFactory;

    /**
     * 函数名称到函数实例的映射缓存
     */
    private final Map<String, CustomFunction> functionCache = new ConcurrentHashMap<>();

    /**
     * 缓存是否需要刷新
     */
    private volatile boolean cacheInvalid = true;

    public CustomFunctionExecutor(OneBasePluginManager pluginManager, PluginContextFactory contextFactory) {
        this.pluginManager = pluginManager;
        this.contextFactory = contextFactory;
    }

    /**
     * 执行自定义函数
     *
     * @param functionName 函数名称
     * @param args         参数列表
     * @return 执行结果
     */
    public Object execute(String functionName, Object... args) {
        CustomFunction function = getFunction(functionName);
        if (function == null) {
            throw new IllegalArgumentException("未找到自定义函数: " + functionName);
        }

        // 获取或创建上下文
        String pluginId = getPluginIdForFunction(function);
        PluginContext context = contextFactory.createContext(pluginId);

        try {
            log.debug("执行自定义函数: {}({})", functionName, args);
            Object result = function.execute(context, args);
            log.debug("自定义函数执行完成: {} = {}", functionName, result);
            return result;
        } catch (Exception e) {
            log.error("自定义函数执行失败: {}", functionName, e);
            throw new RuntimeException("函数执行失败: " + functionName, e);
        }
    }

    /**
     * 获取函数
     *
     * @param functionName 函数名称
     * @return 函数实例
     */
    public CustomFunction getFunction(String functionName) {
        refreshCacheIfNeeded();
        return functionCache.get(functionName.toUpperCase());
    }

    /**
     * 获取所有已注册的函数
     *
     * @return 函数Map（函数名 -> 函数实例）
     */
    public Map<String, CustomFunction> getAllFunctions() {
        refreshCacheIfNeeded();
        return new HashMap<>(functionCache);
    }

    /**
     * 检查函数是否存在
     *
     * @param functionName 函数名称
     * @return 是否存在
     */
    public boolean hasFunction(String functionName) {
        return getFunction(functionName) != null;
    }

    /**
     * 刷新缓存
     */
    public void refreshCache() {
        cacheInvalid = true;
        refreshCacheIfNeeded();
    }

    /**
     * 标记缓存失效
     */
    public void invalidateCache() {
        cacheInvalid = true;
    }

    /**
     * 如果需要则刷新缓存
     */
    private void refreshCacheIfNeeded() {
        if (cacheInvalid) {
            synchronized (functionCache) {
                if (cacheInvalid) {
                    functionCache.clear();
                    List<CustomFunction> functions = pluginManager.getCustomFunctions();
                    for (CustomFunction function : functions) {
                        String name = function.name().toUpperCase();
                        if (functionCache.containsKey(name)) {
                            log.warn("函数名称冲突: {}，后注册的将覆盖先注册的", name);
                        }
                        functionCache.put(name, function);
                        log.debug("注册自定义函数: {}", name);
                    }
                    cacheInvalid = false;
                    log.info("已加载 {} 个自定义函数", functionCache.size());
                }
            }
        }
    }

    /**
     * 获取函数所属的插件ID
     */
    private String getPluginIdForFunction(CustomFunction function) {
        // 简化实现：通过类加载器获取插件ID
        ClassLoader classLoader = function.getClass().getClassLoader();
        // TODO: 从PF4J获取实际的插件ID
        return "unknown";
    }
}
