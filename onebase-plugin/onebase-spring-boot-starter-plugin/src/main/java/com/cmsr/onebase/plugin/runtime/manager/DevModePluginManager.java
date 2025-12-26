package com.cmsr.onebase.plugin.runtime.manager;

import com.cmsr.onebase.plugin.core.ExtensionPointScannerSpring;
import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginDescriptor;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * 开发模式插件管理器
 * <p>
 * 用于开发阶段，直接扫描classpath中的扩展点实现类，无需打包成插件ZIP。
 * 现在继承自 SpringPluginManager，利用 pf4j-spring 的能力自动注册扩展点为 Spring Bean。
 * </p>
 *
 * <h3>主要特性</h3>
 * <ul>
 * <li>扫描指定classpath路径下的扩展点实现类</li>
 * <li>自动将扩展点注册为 Spring Bean（通过 pf4j-spring 的 ExtensionsInjector）</li>
 * <li>支持IDE中直接启动和调试</li>
 * <li>无需打包插件ZIP，开发效率高</li>
 * </ul>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
@Slf4j
public class DevModePluginManager extends SpringPluginManager {

    /**
     * 虚拟插件ID，用于开发模式下的扩展点
     */
    private static final String DEV_PLUGIN_ID = "dev-mode-plugin";

    /**
     * 虚拟插件包装器
     */
    private PluginWrapper devPluginWrapper;

    /**
     * 扩展点扫描器
     */
    private final ExtensionPointScannerSpring scanner;

    /**
     * Spring 应用上下文，用于依赖注入
     */
    private final ApplicationContext applicationContext;

    /**
     * 扩展点缓存，避免重复扫描和注册
     * Key: 扩展点类型, Value: 扩展点实例列表
     */
    private final java.util.Map<Class<?>, java.util.List<?>> extensionCache = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param properties         插件配置属性
     * @param applicationContext Spring应用上下文
     */
    public DevModePluginManager(PluginProperties properties, ApplicationContext applicationContext) {
        super(); // Call to SpringPluginManager's default constructor
        this.applicationContext = applicationContext;
        List<String> devPaths = properties != null && properties.isDevMode()
                ? properties.getDevClassPaths()
                : Collections.emptyList();
        this.scanner = new ExtensionPointScannerSpring(devPaths);
    }

    /**
     * 覆盖 SpringPluginManager.init()
     * <p>
     * 禁止父类通过 @PostConstruct 自动调用 loadPlugins() 和 startPlugins()。
     * 加载和启动逻辑移交给
     * {@link com.cmsr.onebase.plugin.runtime.config.PluginRuntimeAutoConfiguration}
     * 中的 configureAndInitPluginManager 方法统一控制。
     * </p>
     */
    @Override
    public void init() {
        log.debug("覆盖 SpringPluginManager.init()，跳过默认的自动加载/启动");
    }

    /**
     * 开发模式下使用Spring的classpath扫描从classpath加载扩展点
     * <p>
     * 零配置：无需META-INF/services，无需extensions.idx
     * IDE友好：修改Java代码后直接运行，无需Maven编译
     * </p>
     * <p>
     * 注意：只有在插件已启动（STARTED状态）时才返回扩展点，
     * 这样才能正确支持auto-load和auto-start配置。
     * </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getExtensions(Class<T> type) {
        // 检查虚拟插件是否已启动
        if (devPluginWrapper == null || devPluginWrapper.getPluginState() != PluginState.STARTED) {
            log.debug("开发模式：虚拟插件未启动，返回空扩展点列表");
            return Collections.emptyList();
        }

        // 检查缓存
        if (extensionCache.containsKey(type)) {
            log.debug("开发模式：从缓存返回 {} 扩展点", type.getSimpleName());
            return (List<T>) extensionCache.get(type);
        }

        log.debug("开发模式：扫描classpath查找 {} 扩展点", type.getName());
        List<T> extensions = scanner.scanExtensions(type);

        // 为扩展点实例注入 Spring 依赖并注册为 Bean
        if (applicationContext != null) {
            org.springframework.beans.factory.config.AutowireCapableBeanFactory beanFactory = applicationContext
                    .getAutowireCapableBeanFactory();

            for (T extension : extensions) {
                try {
                    // 1. 注入依赖
                    beanFactory.autowireBean(extension);

                    // 2. 注册为 Spring Bean（使用类名作为 Bean 名称）
                    String beanName = extension.getClass().getName();
                    if (beanFactory instanceof org.springframework.beans.factory.support.DefaultListableBeanFactory) {
                        ((org.springframework.beans.factory.support.DefaultListableBeanFactory) beanFactory)
                                .registerSingleton(beanName, extension);
                        log.debug("已为扩展点 {} 注入依赖并注册为 Spring Bean", extension.getClass().getSimpleName());
                    } else {
                        log.debug("已为扩展点 {} 注入依赖（无法注册为 Bean）", extension.getClass().getSimpleName());
                    }
                } catch (Exception e) {
                    log.warn("为扩展点 {} 注入依赖或注册 Bean 失败: {}", extension.getClass().getSimpleName(), e.getMessage());
                }
            }
        }

        // 缓存结果
        extensionCache.put(type, extensions);

        return extensions;
    }

    /**
     * 开发模式下获取特定插件的扩展点
     * <p>
     * 对于开发模式虚拟插件，委托给getExtensions(Class)方法；
     * 对于其他插件ID，返回空列表。
     * </p>
     */
    @Override
    public <T> List<T> getExtensions(Class<T> type, String pluginId) {
        log.debug("开发模式：getExtensions({}, {}) 被调用", type.getSimpleName(), pluginId);
        log.debug("  虚拟插件ID: {}, 请求插件ID: {}", DEV_PLUGIN_ID, pluginId);
        log.debug("  虚拟插件状态: {}", devPluginWrapper != null ? devPluginWrapper.getPluginState() : "NULL");

        if (DEV_PLUGIN_ID.equals(pluginId)) {
            log.debug("开发模式：匹配虚拟插件 {}，委托给 getExtensions(Class)", pluginId);
            List<T> result = getExtensions(type);
            log.debug("开发模式：为虚拟插件 {} 获取到 {} 个 {} 扩展点", pluginId, result.size(), type.getSimpleName());
            return result;
        }

        log.debug("开发模式：插件 {} 不是虚拟插件，返回空列表", pluginId);
        return Collections.emptyList();
    }

    @Override
    public void loadPlugins() {

        // 创建虚拟插件包装器
        PluginDescriptor descriptor = new DefaultPluginDescriptor(
                DEV_PLUGIN_ID,
                "开发模式虚拟插件",
                "DevModePlugin",
                "1.0.0-DEV",
                null,
                null,
                null);

        devPluginWrapper = new PluginWrapper(this, descriptor, Paths.get(""), getClass().getClassLoader());
        devPluginWrapper.setPluginState(PluginState.RESOLVED);

        // 注册虚拟插件
        // 注意：不调用 getPlugins().add()，因为 getPlugins() 返回不可修改的列表
        // devPluginWrapper 会通过 getPlugins() 方法返回
    }

    @Override
    public List<PluginWrapper> getPlugins() {
        // DEV 模式下，返回包含虚拟插件的列表
        if (devPluginWrapper != null) {
            return Collections.singletonList(devPluginWrapper);
        }
        return Collections.emptyList();
    }

    @Override
    public PluginWrapper getPlugin(String pluginId) {
        // DEV 模式下，检查是否请求虚拟插件
        if (DEV_PLUGIN_ID.equals(pluginId) && devPluginWrapper != null) {
            return devPluginWrapper;
        }
        return super.getPlugin(pluginId);
    }

    @Override
    public void startPlugins() {
        if (devPluginWrapper != null) {
            devPluginWrapper.setPluginState(PluginState.STARTED);
            getStartedPlugins().add(devPluginWrapper);
        }
    }

    @Override
    public PluginState startPlugin(String pluginId) {
        if (DEV_PLUGIN_ID.equals(pluginId) && devPluginWrapper != null) {
            devPluginWrapper.setPluginState(PluginState.STARTED);
            if (!getStartedPlugins().contains(devPluginWrapper)) {
                getStartedPlugins().add(devPluginWrapper);
            }
            return PluginState.STARTED;
        }
        return super.startPlugin(pluginId);
    }

    @Override
    public void stopPlugins() {
        log.info("开发模式：停止虚拟插件");
        if (devPluginWrapper != null) {
            devPluginWrapper.setPluginState(PluginState.STOPPED);
            getStartedPlugins().remove(devPluginWrapper);
        }
    }

    @Override
    public PluginState stopPlugin(String pluginId) {
        if (DEV_PLUGIN_ID.equals(pluginId) && devPluginWrapper != null) {
            devPluginWrapper.setPluginState(PluginState.STOPPED);
            getStartedPlugins().remove(devPluginWrapper);
            return PluginState.STOPPED;
        }
        return super.stopPlugin(pluginId);
    }

    @Override
    public boolean unloadPlugin(String pluginId) {
        if (DEV_PLUGIN_ID.equals(pluginId) && devPluginWrapper != null) {
            stopPlugin(pluginId);
            // 不需要从 getPlugins() 移除，因为 getPlugins() 是基于 devPluginWrapper 动态返回的
            // 也不需要从 getResolvedPlugins() 移除，因为我们没有添加到父类的列表中
            devPluginWrapper = null;
            return true;
        }
        return super.unloadPlugin(pluginId);
    }

    @Override
    public boolean deletePlugin(String pluginId) {
        // 开发模式不支持删除插件
        if (DEV_PLUGIN_ID.equals(pluginId)) {
            return unloadPlugin(pluginId);
        }
        return super.deletePlugin(pluginId);
    }
}
