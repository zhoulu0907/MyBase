package com.cmsr.onebase.plugin.simulator.constant;

/**
 * 插件管理错误消息常量
 *
 * @author matianyu
 * @date 2025-12-20
 */
public class PluginErrorMessages {

    /**
     * 插件不存在错误消息模板
     */
    public static final String PLUGIN_NOT_FOUND = "插件 '%s' 不存在";

    /**
     * 插件文件不存在错误消息模板
     */
    public static final String PLUGIN_FILE_NOT_FOUND = "插件文件不存在: %s";

    /**
     * 加载插件失败错误消息模板
     */
    public static final String PLUGIN_LOAD_FAILED = "加载插件失败: %s";

    /**
     * 卸载插件失败错误消息模板
     */
    public static final String PLUGIN_UNLOAD_FAILED = "卸载插件 '%s' 失败";

    /**
     * 上传文件为空错误消息
     */
    public static final String UPLOAD_FILE_EMPTY = "上传文件不能为空";

    /**
     * 上传文件格式错误消息
     */
    public static final String UPLOAD_FILE_FORMAT_ERROR = "仅支持上传ZIP或JAR文件";

    private PluginErrorMessages() {
        // 私有构造函数，防止实例化
    }
}
