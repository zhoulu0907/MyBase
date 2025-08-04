package com.cmsr.onebase.module.metadata.dal.dataobject.datasource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.*;

/**
 * 数据源表 DO
 */
@Table(name = "metadata_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDatasourceDO extends TenantBaseDO {

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

}
