package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.core.PluginMode;
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
     * 插件运行模式
     * <ul>
     *   <li>dev: 开发模式，只加载classpath下的扩展点，支持IDE断点调试</li>
     *   <li>staging: 预发布模式，只加载plugin目录ZIP包扩展点，用于验证插件完整生命周期</li>
     *   <li>prod: 生产模式，使用PF4J默认策略（classpath + plugin目录都扫描）</li>
     * </ul>
     * 默认值: prod
     */
    private String mode = "prod";

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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * 获取插件运行模式枚举
     * <p>
     * 如果配置的模式值无效，将抛出IllegalArgumentException异常，拒绝启动。
     * </p>
     *
     * @return 插件运行模式枚举
     * @throws IllegalArgumentException 如果模式值不在枚举范围内
     */
    public PluginMode getPluginMode() {
        return PluginMode.fromValue(mode);
    }

    /**
     * 是否为开发模式
     * <p>
     * 使用字符串比较避免重复调用getPluginMode()造成的性能开销
     * </p>
     *
     * @return true表示开发模式
     */
    public boolean isDevMode() {
        return PluginMode.DEV.getValue().equalsIgnoreCase(mode);
    }

    /**
     * 是否为预发布模式（全生命周期验证模式）
     * <p>
     * 使用字符串比较避免重复调用getPluginMode()造成的性能开销
     * </p>
     *
     * @return true表示预发布模式
     */
    public boolean isStagingMode() {
        return PluginMode.STAGING.getValue().equalsIgnoreCase(mode);
    }

    /**
     * 是否为生产模式
     * <p>
     * 使用字符串比较避免重复调用getPluginMode()造成的性能开销
     * </p>
     *
     * @return true表示生产模式
     */
    public boolean isProdMode() {
        return PluginMode.PROD.getValue().equalsIgnoreCase(mode);
    }

    public boolean isStrictDependencyCheck() {
        return strictDependencyCheck;
    }

    public void setStrictDependencyCheck(boolean strictDependencyCheck) {
        this.strictDependencyCheck = strictDependencyCheck;
    }
}
