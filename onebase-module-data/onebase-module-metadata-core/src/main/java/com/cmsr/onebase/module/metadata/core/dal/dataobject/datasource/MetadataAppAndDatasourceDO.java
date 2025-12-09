package com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用与数据源关联表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(value = "metadata_app_and_datasource")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataAppAndDatasourceDO extends BaseBizEntity {


    /**
     * 数据源UUID
     * <p>
     * 关联 metadata_datasource.datasource_uuid
     */
    @Column(value = "datasource_uuid", comment = "数据源UUID")
    private String datasourceUuid;

    /**
     * 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
     */
    @Column(value = "datasource_type", comment = "数据源类型")
    private String datasourceType;

    /**
     * 应用UID
     */
    @Column(value = "app_uid", comment = "应用UID")
    private String appUid;


}

