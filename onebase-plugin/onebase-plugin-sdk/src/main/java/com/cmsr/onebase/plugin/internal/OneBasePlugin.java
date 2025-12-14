package com.cmsr.onebase.plugin.internal;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OneBase插件基类
 * <p>
 * 封装PF4J的Plugin类，提供插件生命周期管理。
 * 插件开发者无需直接继承此类，Maven插件会自动生成。
 * </p>
 * <p>
 * 生命周期：
 * <ol>
 *     <li>构造函数 - 插件被加载时调用</li>
 *     <li>start() - 插件启动时调用</li>
 *     <li>stop() - 插件停止时调用</li>
 *     <li>delete() - 插件被删除时调用</li>
 * </ol>
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public class OneBasePlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(OneBasePlugin.class);

    /**
     * PF4J要求的构造函数
     *
     * @param wrapper 插件包装器
     */
    public OneBasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * 获取插件ID
     *
     * @return 插件ID，如果wrapper未设置则返回"unknown"
     */
    public String getPluginId() {
        return getWrapper() != null ? getWrapper().getPluginId() : "unknown";
    }

    /**
     * 插件启动
     * <p>
     * 插件被启动时调用，可以在此进行初始化操作。
     * </p>
     */
    @Override
    public void start() {
        log.info("插件 [{}] 启动", getPluginId());
    }

    /**
     * 插件停止
     * <p>
     * 插件被停止时调用，可以在此进行资源清理操作。
     * </p>
     */
    @Override
    public void stop() {
        log.info("插件 [{}] 停止", getPluginId());
    }

    /**
     * 插件删除
     * <p>
     * 插件被卸载删除时调用。
     * </p>
     */
    @Override
    public void delete() {
        log.info("插件 [{}] 删除", getPluginId());
    }
}
