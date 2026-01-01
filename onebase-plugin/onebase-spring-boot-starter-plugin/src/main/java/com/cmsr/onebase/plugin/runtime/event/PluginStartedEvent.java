package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;
import org.pf4j.PluginWrapper;

/**
 * 插件已启动事件
 */
@Getter
public class PluginStartedEvent extends BasePluginEvent {
    private final PluginWrapper pluginWrapper;

    public PluginStartedEvent(String pluginId, PluginWrapper pluginWrapper) {
        super(pluginId);
        this.pluginWrapper = pluginWrapper;
    }
}
