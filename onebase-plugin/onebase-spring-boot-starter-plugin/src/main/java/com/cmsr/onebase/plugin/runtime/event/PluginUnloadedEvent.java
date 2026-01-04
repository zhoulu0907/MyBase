package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已卸载事件
 */
public class PluginUnloadedEvent extends BasePluginEvent {
    public PluginUnloadedEvent(String pluginId) {
        super(pluginId);
    }
}
