package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已删除事件
 */
public class PluginDeletedEvent {
    private final String pluginId;

    public PluginDeletedEvent(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginId() {
        return pluginId;
    }
}
