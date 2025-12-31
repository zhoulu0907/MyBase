package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已停止事件
 */
public class PluginStoppedEvent extends BasePluginEvent {
    public PluginStoppedEvent(String pluginId) {
        super(pluginId);
    }
}
