package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已停止事件
 */
public class PluginStoppedEvent {
    private final String pluginId;

    public PluginStoppedEvent(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginId() {
        return pluginId;
    }
}
