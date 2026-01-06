package com.cmsr.onebase.plugin.runtime.context;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

/**
 * 插件上下文工厂
 * <p>
 * 用于创建和管理插件的运行时上下文。
 * </p>
 *
 * @author matianyu
 * @date 2025-07-25
 */
@RequiredArgsConstructor
public class PluginContextFactory {

    private final ApplicationContext applicationContext;

}
