package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已重新加载事件
 */
public class PluginReloadedEvent {
    private final String pluginId;

    public PluginReloadedEvent(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginId() {
        return pluginId;
    }
}
