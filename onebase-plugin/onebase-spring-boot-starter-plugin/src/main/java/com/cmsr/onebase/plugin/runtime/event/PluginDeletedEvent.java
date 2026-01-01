package com.cmsr.onebase.plugin.runtime.event;

/**
 * 插件已删除事件
 */
public class PluginDeletedEvent extends BasePluginEvent {
    public PluginDeletedEvent(String pluginId) {
        super(pluginId);
    }
}
