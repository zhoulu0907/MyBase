package com.cmsr.onebase.plugin.core.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 插件命令消息
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginCommandMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 命令类型
     */
    private PluginCommand command;

    /**
     * 插件ID
     */
    private String pluginId;

    /**
     * 插件版本
     */
    private String pluginVersion;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 插件包文件ID（MinIO中的文件ID）
     */
    private Long packageFileId;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 插件包信息列表（用于UPLOAD命令）
     */
    private List<PackageInfo> packages;

    /**
     * 插件命令枚举
     */
    public enum PluginCommand {
        /**
         * 上传插件（通知Runtime下载并解压插件）
         */
        UPLOAD,
        /**
         * 启用插件
         */
        ENABLE,
        /**
         * 禁用插件
         */
        DISABLE,
        /**
         * 重新加载插件
         */
        RELOAD
    }

    /**
     * 插件包信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 包名称
         */
        private String packageName;

        /**
         * 包类型（0=前端，1=后端）
         */
        private Integer packageType;
    }

    /**
     * 包类型常量
     */
    public static final int PACKAGE_TYPE_FRONTEND = 0;
    public static final int PACKAGE_TYPE_BACKEND = 1;

}
