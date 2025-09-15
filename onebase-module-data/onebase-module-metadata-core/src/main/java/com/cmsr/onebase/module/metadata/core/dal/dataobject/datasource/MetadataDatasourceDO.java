package com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据源表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(name = "metadata_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDatasourceDO extends TenantBaseDO {

    // 列名常量
    public static final String DATASOURCE_NAME   = "datasource_name";
    public static final String CODE              = "code";
    public static final String DATASOURCE_TYPE   = "datasource_type";
    public static final String DATASOURCE_ORIGIN = "datasource_origin";
    public static final String CONFIG            = "config";
    public static final String DESCRIPTION       = "description";
    public static final String RUN_MODE          = "run_mode";
    public static final String APP_ID            = "app_id";

    public MetadataDatasourceDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源编码
     */
    private String code;

    /**
     * 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
     */
    private String datasourceType;

    /**
     * 数据源配置信息(JSON格式存储所有连接参数)
     * 使用自定义TypeHandler确保明文JSON存储
     */
    private String config;

    /**
     * 描述
     */
    private String description;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 数据源来源：0：系统默认，1：自有数据源，2：外部数据源
     */
    private Integer datasourceOrigin;

}
