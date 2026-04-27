package com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 数据源表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(value = "metadata_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDatasourceDO extends BaseBizEntity {

    /**
     * 数据源UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "datasource_uuid", comment = "数据源UUID")
    private String datasourceUuid;

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
     * 版本标识
     */
    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    /**
     * 数据源来源：0：系统默认，1：自有数据源，2：外部数据源
     */
    @Column(value = "datasource_origin", comment = "数据源来源")
    private Integer datasourceOrigin;

}
