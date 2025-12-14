package com.cmsr.onebase.plugin.runtime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 插件运行时配置属性
 * <p>
 * 定义插件系统的配置选项。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
@ConfigurationProperties(prefix = "onebase.plugin")
public class PluginProperties {

    /**
     * 是否启用插件系统
     */
    private boolean enabled = true;

    /**
     * 插件目录路径
     */
    private String pluginsDir = "plugins";

    /**
     * 是否自动加载插件
     */
    private boolean autoLoad = true;

    /**
     * 是否启动时自动启动已加载的插件
     */
    private boolean autoStart = true;

    /**
     * 是否开启开发模式（用于调试）
     */
    private boolean devMode = false;

    /**
     * 开发模式下的插件类路径
     */
    private String devPluginClasspath;

    /**
     * 插件扫描间隔（毫秒），0表示禁用自动扫描
     */
    private long scanInterval = 0;

    /**
     * 是否严格检查插件依赖
     */
    private boolean strictDependencyCheck = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPluginsDir() {
        return pluginsDir;
    }

    public void setPluginsDir(String pluginsDir) {
        this.pluginsDir = pluginsDir;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public String getDevPluginClasspath() {
        return devPluginClasspath;
    }

    public void setDevPluginClasspath(String devPluginClasspath) {
        this.devPluginClasspath = devPluginClasspath;
    }

    public long getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(long scanInterval) {
        this.scanInterval = scanInterval;
    }

    public boolean isStrictDependencyCheck() {
        return strictDependencyCheck;
    }

    public void setStrictDependencyCheck(boolean strictDependencyCheck) {
        this.strictDependencyCheck = strictDependencyCheck;
    }
}
