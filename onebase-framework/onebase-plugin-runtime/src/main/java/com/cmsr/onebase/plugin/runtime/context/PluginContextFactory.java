package com.cmsr.onebase.plugin.runtime.context;

import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.context.PluginContextHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 插件上下文工厂
 * <p>
 * 负责创建和管理插件上下文，提供上下文切换能力。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Component
public class PluginContextFactory {

    private final ApplicationContext applicationContext;

    /**
     * 租户ID提供者（从平台获取当前租户ID）
     */
    private Supplier<String> tenantIdProvider = () -> null;

    /**
     * 用户ID提供者（从平台获取当前用户ID）
     */
    private Supplier<String> userIdProvider = () -> null;

    /**
     * 追踪ID提供者
     */
    private Supplier<String> traceIdProvider = () -> UUID.randomUUID().toString().replace("-", "");

    public PluginContextFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 创建插件上下文
     *
     * @param pluginId 插件ID
     * @return 插件上下文
     */
    public PluginContext createContext(String pluginId) {
        return DefaultPluginContext.builder()
                .pluginId(pluginId)
                .tenantId(tenantIdProvider.get())
                .userId(userIdProvider.get())
                .traceId(traceIdProvider.get())
                .attributes(new HashMap<>())
                .applicationContext(applicationContext)
                .build();
    }

    /**
     * 创建插件上下文（带自定义属性）
     *
     * @param pluginId   插件ID
     * @param attributes 属性
     * @return 插件上下文
     */
    public PluginContext createContext(String pluginId, Map<String, Object> attributes) {
        return DefaultPluginContext.builder()
                .pluginId(pluginId)
                .tenantId(tenantIdProvider.get())
                .userId(userIdProvider.get())
                .traceId(traceIdProvider.get())
                .attributes(attributes != null ? new HashMap<>(attributes) : new HashMap<>())
                .applicationContext(applicationContext)
                .build();
    }

    /**
     * 在指定插件上下文中执行操作
     *
     * @param pluginId 插件ID
     * @param action   要执行的操作
     * @param <T>      返回类型
     * @return 操作结果
     */
    public <T> T executeInContext(String pluginId, Supplier<T> action) {
        PluginContext context = createContext(pluginId);
        PluginContext previousContext = PluginContextHolder.get();
        try {
            PluginContextHolder.set(context);
            return action.get();
        } finally {
            if (previousContext != null) {
                PluginContextHolder.set(previousContext);
            } else {
                PluginContextHolder.clear();
            }
        }
    }

    /**
     * 在指定插件上下文中执行操作（无返回值）
     *
     * @param pluginId 插件ID
     * @param action   要执行的操作
     */
    public void executeInContext(String pluginId, Runnable action) {
        executeInContext(pluginId, () -> {
            action.run();
            return null;
        });
    }

    // ==================== 提供者设置 ====================

    /**
     * 设置租户ID提供者
     *
     * @param provider 租户ID提供者
     */
    public void setTenantIdProvider(Supplier<String> provider) {
        this.tenantIdProvider = provider;
    }

    /**
     * 设置用户ID提供者
     *
     * @param provider 用户ID提供者
     */
    public void setUserIdProvider(Supplier<String> provider) {
        this.userIdProvider = provider;
    }

    /**
     * 设置追踪ID提供者
     *
     * @param provider 追踪ID提供者
     */
    public void setTraceIdProvider(Supplier<String> provider) {
        this.traceIdProvider = provider;
    }
}
