package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * 插件加载失败事件
 * <p>
 * 当插件加载过程中发生异常时发布此事件，用于监控和告警。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-17
 */
@Getter
public class PluginLoadFailedEvent {

    /**
     * 插件文件路径
     */
    private final Path pluginPath;

    /**
     * 插件ID（如果已部分加载）
     */
    private final String pluginId;

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

    public PluginLoadFailedEvent(Path pluginPath, String pluginId, Throwable cause) {
        this(pluginPath, pluginId, cause, "system");
    }

    public PluginLoadFailedEvent(Path pluginPath, String pluginId, Throwable cause, String operator) {
        this.pluginPath = pluginPath;
        this.pluginId = pluginId;
        this.cause = cause;
        this.operator = operator;
        this.timestamp = LocalDateTime.now();
    }
}
