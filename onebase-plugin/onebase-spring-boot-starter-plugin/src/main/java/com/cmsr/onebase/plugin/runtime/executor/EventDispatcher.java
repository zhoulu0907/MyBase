package com.cmsr.onebase.plugin.runtime.executor;

import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 插件事件分发器
 * <p>
 * 负责将系统事件或业务事件分发给相关的插件。
 * </p>
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Slf4j
@RequiredArgsConstructor
public class EventDispatcher {

    private final OneBasePluginManager pluginManager;
    private final PluginContextFactory contextFactory;

}
