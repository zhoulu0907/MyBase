package com.cmsr.onebase.plugin.runtime.manager;

import com.cmsr.onebase.plugin.core.ExtensionPointScannerSpring;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;

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
     * 扩展点扫描器
     */
    private final ExtensionPointScannerSpring scanner;

    public DevModePluginManager() {
        super(Paths.get(System.getProperty("user.dir")));
        this.scanner = new ExtensionPointScannerSpring();
        log.debug("初始化开发模式插件管理器（DevModePluginManager）");
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
        return scanner.scanExtensions(type);
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
        log.debug("开发模式：启动虚拟插件");
        if (devPluginWrapper != null) {
            devPluginWrapper.setPluginState(PluginState.STARTED);
            getStartedPlugins().add(devPluginWrapper);
            log.debug("开发模式虚拟插件已启动");
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
