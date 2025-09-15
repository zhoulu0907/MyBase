package com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 应用与数据源关联表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(name = "metadata_app_and_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataAppAndDatasourceDO extends TenantBaseDO {

    // 列名常量
    public static final String APPLICATION_ID  = "application_id";
    public static final String DATASOURCE_ID   = "datasource_id";
    public static final String DATASOURCE_TYPE = "datasource_type";
    public static final String APP_UID         = "app_uid";

    public MetadataAppAndDatasourceDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 应用ID
     */
    @Column(name = APPLICATION_ID)
    private Long applicationId;

    /**
     * 数据源ID
     */
    @Column(name = DATASOURCE_ID)
    private Long datasourceId;

    /**
     * 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
     */
    @Column(name = DATASOURCE_TYPE)
    private String datasourceType;

    /**
     * 应用UID
     */
    @Column(name = APP_UID)
    private String appUid;

}

