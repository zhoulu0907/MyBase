package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已卸载事件
 */
public class PluginUnloadedEvent {
    private final String pluginId;

    public PluginUnloadedEvent(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginId() {
        return pluginId;
    }
}
