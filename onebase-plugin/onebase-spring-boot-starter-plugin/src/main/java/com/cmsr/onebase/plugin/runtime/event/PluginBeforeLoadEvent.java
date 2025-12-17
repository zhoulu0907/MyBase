package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * 插件加载前事件
 * <p>
 * 在插件加载操作执行之前发布此事件，允许监听器执行预处理或取消加载。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-17
 */
@Getter
public class PluginBeforeLoadEvent {

    /**
     * 插件文件路径
     */
    private final Path pluginPath;

    /**
     * 事件时间戳
     */
    private final LocalDateTime timestamp;

    /**
     * 操作者（系统/用户ID）
     */
    private final String operator;

    public PluginBeforeLoadEvent(Path pluginPath) {
        this(pluginPath, "system");
    }

    public PluginBeforeLoadEvent(Path pluginPath, String operator) {
        this.pluginPath = pluginPath;
        this.operator = operator;
        this.timestamp = LocalDateTime.now();
    }
}
