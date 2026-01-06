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
 * 插件配置信息表 DO
 *
 * @author GitHub Copilot
 * @date 2026-01-05
 */
@Table(value = "plugin_config_info")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginConfigInfoDO extends BaseTenantEntity {

    /**
     * 插件id，来自zip包中的json文件
     */
    @Column(value = "plugin_id", comment = "插件id")
    private Long pluginId;

    /**
     * 插件版本
     */
    @Column(value = "plugin_version", comment = "插件版本")
    private String pluginVersion;

    /**
     * 配置键
     */
    @Column(value = "config_key", comment = "配置键")
    private String configKey;

    /**
     * 配置值
     */
    @Column(value = "config_value", comment = "配置值")
    private String configValue;

    /**
     * 值类型,normal普通，secret密文,等等
     */
    @Column(value = "value_type", comment = "值类型")
    private String valueType;

    /**
     * 版本锁标识
     */
    @Column(value = "lock_version", comment = "版本锁标识", version = true)
    private Integer lockVersion;

}
