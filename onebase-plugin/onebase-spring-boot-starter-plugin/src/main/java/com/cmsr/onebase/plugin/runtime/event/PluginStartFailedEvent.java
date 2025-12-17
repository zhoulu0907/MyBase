package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;
import org.pf4j.PluginWrapper;

import java.time.LocalDateTime;

/**
 * 插件启动失败事件
 * <p>
 * 当插件启动过程中发生异常时发布此事件，用于监控和告警。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-17
 */
@Getter
public class PluginStartFailedEvent {

    /**
     * 插件ID
     */
    private final String pluginId;

    /**
     * 插件包装器
     */
    private final PluginWrapper pluginWrapper;

    /**
     * 失败原因
     */
    private final Throwable cause;

    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;

    /**
     * 操作者（系统/用户ID）
     */
    private final String operator;

    public PluginStartFailedEvent(String pluginId, PluginWrapper pluginWrapper, Throwable cause) {
        this(pluginId, pluginWrapper, cause, "system");
    }

    public PluginStartFailedEvent(String pluginId, PluginWrapper pluginWrapper, Throwable cause, String operator) {
        this.pluginId = pluginId;
        this.pluginWrapper = pluginWrapper;
        this.cause = cause;
        this.operator = operator;
        this.timestamp = LocalDateTime.now();
    }
}
