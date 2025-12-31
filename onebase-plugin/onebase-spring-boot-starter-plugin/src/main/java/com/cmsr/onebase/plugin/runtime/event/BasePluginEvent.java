package com.cmsr.onebase.plugin.runtime.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 插件事件基类
 * <p>
 * 所有插件生命周期事件的基类，包含插件ID等通用信息
 * </p>
 *
 * @author OneBase Team
 */
@Getter
@RequiredArgsConstructor
public abstract class BasePluginEvent {
    /**
     * 插件ID
     */
    private final String pluginId;
}
