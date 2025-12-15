package com.cmsr.onebase.plugin.runtime.manager;

import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 开发模式插件管理器
 * <p>
 * 用于开发调试场景，直接扫描当前classpath中的扩展点，无需打包成ZIP/JAR。
 * 适合在IDE中直接启动和调试插件代码。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-15
 */
public class DevModePluginManager extends DefaultPluginManager {

    private static final Logger log = LoggerFactory.getLogger(DevModePluginManager.class);

    /**
     * 虚拟插件ID，用于开发模式下的扩展点
     */
    private static final String DEV_PLUGIN_ID = "dev-mode-plugin";

    /**
     * 虚拟插件包装器
     */
    private PluginWrapper devPluginWrapper;

    /**
     * 开发模式下扫描的包路径
     */
    private String[] scanPackages;

    public DevModePluginManager() {
        this("com.cmsr.onebase.plugin");
    }

    public DevModePluginManager(String scanPackages) {
        super(Paths.get(System.getProperty("user.dir")));
        this.scanPackages = scanPackages.split(",");
        log.info("=".repeat(60));
        log.info("初始化开发模式插件管理器（DevModePluginManager）");
        log.info("插件加载方式: 扫描classpath中的扩展点实现类");
        log.info("扫描包路径: {}", scanPackages);
        log.info("适用场景: IDE中直接启动和调试，无需打包ZIP/JAR");
        log.info("=".repeat(60));
    }

    @Override
    protected ExtensionFinder createExtensionFinder() {
        // 开发模式不使用ExtensionFinder，直接在getExtensions中使用Spring classpath扫描
        return null;
    }

    /**
     * 开发模式下使用Spring的classpath扫描从classpath加载扩展点
     * <p>
     * 零配置：无需META-INF/services，无需extensions.idx
     * IDE友好：修改Java代码后直接运行，无需Maven编译
     * </p>
     */
    @Override
    public <T> List<T> getExtensions(Class<T> type) {
        log.debug("开发模式：扫描classpath查找 {} 扩展点", type.getName());
        
        List<T> extensions = new ArrayList<>();
        
        try {
            // 使用Spring的classpath扫描器
            ClassPathScanningCandidateComponentProvider scanner = 
                new ClassPathScanningCandidateComponentProvider(false);
            
            // 添加过滤器：查找实现了指定接口的类
            scanner.addIncludeFilter(new AssignableTypeFilter(type));
            
            // 扫描所有配置的包路径
            Set<BeanDefinition> allCandidates = new HashSet<>();
            for (String basePackage : scanPackages) {
                String packageToScan = basePackage.trim();
                if (!packageToScan.isEmpty()) {
                    Set<BeanDefinition> candidates = scanner.findCandidateComponents(packageToScan);
                    allCandidates.addAll(candidates);
                    log.debug("开发模式：在包 {} 中发现 {} 个候选类", packageToScan, candidates.size());
                }
            }
            
            for (BeanDefinition bd : allCandidates) {
                String className = bd.getBeanClassName();
                try {
                    Class<?> clazz = Class.forName(className);
                    
                    // 跳过抽象类和接口
                    if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                        log.debug("开发模式：跳过抽象类/接口: {}", className);
                        continue;
                    }
                    
                    // 确保类确实实现了目标接口
                    if (!type.isAssignableFrom(clazz)) {
                        log.debug("开发模式：类 {} 未实现接口 {}", className, type.getName());
                        continue;
                    }
                    
                    @SuppressWarnings("unchecked")
                    T instance = (T) clazz.getDeclaredConstructor().newInstance();
                    extensions.add(instance);
                    log.info("开发模式：发现扩展点 {} -> {}", type.getSimpleName(), className);
                    
                } catch (Exception e) {
                    log.warn("开发模式：实例化扩展点失败 {}: {}", className, e.getMessage());
                }
            }
            
            if (extensions.isEmpty()) {
                log.warn("开发模式：未在classpath中找到 {} 的扩展点实现", type.getName());
                log.warn("扫描的包路径: {}", Arrays.toString(scanPackages));
                log.warn("请确保扩展点实现类已编译到classpath且在上述包路径下");
            }
            
        } catch (Exception e) {
            log.error("开发模式：扫描扩展点失败", e);
        }
        
        log.debug("开发模式：共发现 {} 个 {} 扩展点", extensions.size(), type.getSimpleName());
        return extensions;
    }

    @Override
    public void loadPlugins() {
        log.info("开发模式：跳过ZIP/JAR插件加载，使用当前classpath");
        
        // 创建虚拟插件包装器
        PluginDescriptor descriptor = new DefaultPluginDescriptor(
                DEV_PLUGIN_ID,
                "开发模式虚拟插件",
                "DevModePlugin",
                "1.0.0-DEV",
                null,
                null,
                null
        );

        devPluginWrapper = new PluginWrapper(this, descriptor, Paths.get(""), getClass().getClassLoader());
        devPluginWrapper.setPluginState(PluginState.RESOLVED);
        
        // 注册虚拟插件
        getPlugins().add(devPluginWrapper);
        getUnresolvedPlugins().remove(devPluginWrapper);
        getResolvedPlugins().add(devPluginWrapper);
        
        log.info("已创建开发模式虚拟插件: {}", DEV_PLUGIN_ID);
    }

    @Override
    public void startPlugins() {
        log.info("开发模式：启动虚拟插件");
        if (devPluginWrapper != null) {
            devPluginWrapper.setPluginState(PluginState.STARTED);
            getStartedPlugins().add(devPluginWrapper);
            log.info("开发模式虚拟插件已启动");
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
            getPlugins().remove(devPluginWrapper);
            getResolvedPlugins().remove(devPluginWrapper);
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
