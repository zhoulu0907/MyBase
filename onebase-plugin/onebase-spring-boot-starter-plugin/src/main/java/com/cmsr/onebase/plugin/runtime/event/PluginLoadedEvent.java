package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;
import org.pf4j.PluginWrapper;

/**
 * 插件已加载事件
 */
@Getter
public class PluginLoadedEvent extends BasePluginEvent {
    private final PluginWrapper pluginWrapper;

    public PluginLoadedEvent(String pluginId, PluginWrapper pluginWrapper) {
        super(pluginId);
        this.pluginWrapper = pluginWrapper;
    }
}
