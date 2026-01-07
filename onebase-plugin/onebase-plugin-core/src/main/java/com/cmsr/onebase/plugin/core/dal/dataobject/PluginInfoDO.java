package com.cmsr.onebase.plugin.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 插件信息表 DO
 *
 * @author GitHub Copilot
 * @date 2026-01-05
 */
@Table(value = "plugin_info")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginInfoDO extends BaseTenantEntity {

    /**
     * 插件id，来自zip包中的json文件
     */
    @Column(value = "plugin_id", comment = "插件id")
    private String pluginId;

    /**
     * 插件名称
     */
    @Column(value = "plugin_name", comment = "插件名称")
    private String pluginName;

    /**
     * 插件icon，存的是插件在minio中的id
     */
    @Column(value = "plugin_icon", comment = "插件icon")
    private Long pluginIcon;

    /**
     * 插件描述
     */
    @Column(value = "plugin_description", comment = "插件描述")
    private String pluginDescription;

    /**
     * 插件版本
     */
    @Column(value = "plugin_version", comment = "插件版本")
    private String pluginVersion;

    /**
     * 插件版本描述
     */
    @Column(value = "plugin_version_description", comment = "插件版本描述")
    private String pluginVersionDescription;

    /**
     * 插件包id，存的是插件包在minio中的id
     */
    @Column(value = "plugin_package", comment = "插件包id")
    private Long pluginPackage;

    /**
     * 插件元数据信息
     */
    @Column(value = "plugin_meta_info", comment = "插件元数据信息")
    private String pluginMetaInfo;

    /**
     * 插件配置信息
     */
    @Column(value = "plugin_config_info", comment = "插件配置信息")
    private String pluginConfigInfo;

    /**
     * 版本锁标识
     */
    @Column(value = "lock_version", comment = "版本锁标识", version = true)
    private Integer lockVersion;

    /**
     * 0 关闭，1 开启
     */
    @Column(value = "status", comment = "状态")
    private Integer status;

}
