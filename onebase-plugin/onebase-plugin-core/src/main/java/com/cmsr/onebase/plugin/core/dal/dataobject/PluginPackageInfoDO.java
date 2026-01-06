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
 * 插件中前后端包信息表 DO
 *
 * @author GitHub Copilot
 * @date 2026-01-05
 */
@Table(value = "plugin_package_info")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginPackageInfoDO extends BaseTenantEntity {

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
     * 插件包中前端或者后端包的名称
     */
    @Column(value = "package_name", comment = "包名称")
    private String packageName;

    /**
     * 插件包类型前端还是后端，0 前端包，1 后端包
     */
    @Column(value = "package_type", comment = "包类型")
    private Integer packageType;

    /**
     * 版本锁标识
     */
    @Column(value = "lock_version", comment = "版本锁标识", version = true)
    private Integer lockVersion;

}
