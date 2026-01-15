package com.cmsr.onebase.plugin.core.constant;

/**
 * 插件模块Redis常量定义
 *
 * @author matianyu
 * @date 2026-01-06
 */
public interface PluginRedisConstants {

    /**
     * 插件命令发布订阅Channel
     */
    String PLUGIN_COMMAND_CHANNEL = "onebase:plugin:command";

    /**
     * 插件状态缓存Key前缀
     */
    String PLUGIN_STATUS_PREFIX = "plugin:status:";

}
