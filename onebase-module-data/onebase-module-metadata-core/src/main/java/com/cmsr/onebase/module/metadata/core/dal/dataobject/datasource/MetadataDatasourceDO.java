package com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据源表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(value = "metadata_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataDatasourceDO extends BaseTenantEntity {

    /**
     * 数据源名称
     */
    @Column(value = "datasource_name", comment = "数据源名称")
    private String datasourceName;

    /**
     * 数据源编码
     */
    @Column(value = "code", comment = "数据源编码")
    private String code;

    /**
     * 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
     */
    @Column(value = "datasource_type", comment = "数据源类型")
    private String datasourceType;

    /**
     * 数据源配置信息(JSON格式存储所有连接参数)
     * 使用自定义TypeHandler确保明文JSON存储
     */
    @Column(value = "config", comment = "数据源配置信息")
    private String config;

    /**
     * 描述
     */
    @Column(value = "description", comment = "描述")
    private String description;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    @Column(value = "run_mode", comment = "运行模式：0 编辑态，1 运行态")
    private Integer runMode;

    /**
     * 应用ID
     */
    @Column(value = "app_id", comment = "应用ID")
    private Long appId;

    /**
     * 数据源来源：0：系统默认，1：自有数据源，2：外部数据源
     */
    @Column(value = "datasource_origin", comment = "数据源来源")
    private Integer datasourceOrigin;

}
