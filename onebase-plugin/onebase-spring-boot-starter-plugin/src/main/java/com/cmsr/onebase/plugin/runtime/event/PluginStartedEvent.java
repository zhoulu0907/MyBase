package com.cmsr.onebase.plugin.runtime.event;

import org.pf4j.PluginWrapper;

/**
 * 插件已启动事件
 */
public class PluginStartedEvent {
    private final String pluginId;
    private final PluginWrapper pluginWrapper;

    public PluginStartedEvent(String pluginId, PluginWrapper pluginWrapper) {
        this.pluginId = pluginId;
        this.pluginWrapper = pluginWrapper;
    }

    public String getPluginId() {
        return pluginId;
    }

    public PluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }
}
