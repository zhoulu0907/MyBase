package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;
import org.pf4j.PluginWrapper;

import java.time.LocalDateTime;

/**
 * 插件停止前事件
 * <p>
 * 在插件停止操作执行之前发布此事件，允许监听器执行清理或取消停止。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-17
 */
@Getter
public class PluginBeforeStopEvent {

    /**
     * 插件ID
     */
    private final String pluginId;

    /**
     * 插件包装器
     */
    private final PluginWrapper pluginWrapper;

    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;

    /**
     * 操作者（系统/用户ID）
     */
    private final String operator;

    public PluginBeforeStopEvent(String pluginId, PluginWrapper pluginWrapper) {
        this(pluginId, pluginWrapper, "system");
    }

    public PluginBeforeStopEvent(String pluginId, PluginWrapper pluginWrapper, String operator) {
        this.pluginId = pluginId;
        this.pluginWrapper = pluginWrapper;
        this.operator = operator;
        this.timestamp = LocalDateTime.now();
    }
}
