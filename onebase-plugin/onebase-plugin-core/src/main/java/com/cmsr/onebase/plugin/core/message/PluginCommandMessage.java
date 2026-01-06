package com.cmsr.onebase.plugin.core.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
    private Long pluginId;

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
     * 插件命令枚举
     */
    public enum PluginCommand {
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

}
