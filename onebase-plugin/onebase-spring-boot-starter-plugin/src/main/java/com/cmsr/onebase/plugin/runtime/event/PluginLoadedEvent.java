package com.cmsr.onebase.plugin.runtime.event;

import org.pf4j.PluginWrapper;

/**
 * 发布当插件成功加载后
 */
public class PluginLoadedEvent {
    private final String pluginId;
    private final PluginWrapper pluginWrapper;

    public PluginLoadedEvent(String pluginId, PluginWrapper pluginWrapper) {
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
