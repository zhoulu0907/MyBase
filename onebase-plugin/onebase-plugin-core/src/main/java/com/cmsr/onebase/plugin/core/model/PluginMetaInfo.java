package com.cmsr.onebase.plugin.core.model;

import lombok.Data;

import java.util.List;

/**
 * 插件元数据信息（从plugin.json解析）
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Data
public class PluginMetaInfo {

    /**
     * 插件唯一标识
     */
    private Long pluginId;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 插件版本
     */
    private String pluginVersion;

    /**
     * 插件描述
     */
    private String description;

    /**
     * 版本描述
     */
    private String versionDescription;

    /**
     * 作者
     */
    private String author;

    /**
     * 依赖列表
     */
    private List<PluginDependency> dependencies;

    /**
     * 配置模板
     */
    private List<PluginConfigTemplate> configTemplates;

    /**
     * 包信息
     */
    private List<PluginPackageInfo> packages;

    /**
     * 插件依赖
     */
    @Data
    public static class PluginDependency {
        /**
         * 依赖插件ID
         */
        private Long pluginId;
        /**
         * 最低版本
         */
        private String minVersion;
    }

    /**
     * 配置模板
     */
    @Data
    public static class PluginConfigTemplate {
        /**
         * 配置键
         */
        private String configKey;
        /**
         * 默认值
         */
        private String defaultValue;
        /**
         * 值类型
         */
        private String valueType;
        /**
         * 配置描述
         */
        private String description;
    }

    /**
     * 包信息
     */
    @Data
    public static class PluginPackageInfo {
        /**
         * 包名称
         */
        private String packageName;
        /**
         * 包类型：0前端，1后端
         */
        private Integer packageType;
    }

}
