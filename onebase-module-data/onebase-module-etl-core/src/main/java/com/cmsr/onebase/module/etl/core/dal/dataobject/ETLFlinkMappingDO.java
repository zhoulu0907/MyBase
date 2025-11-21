package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "etl_flink_mapping")
@com.mybatisflex.annotation.Table("etl_flink_mapping")
public class ETLFlinkMappingDO extends TenantBaseDO {

    @Column(name = "datasource_type")
    private String datasourceType;

    @Column(name = "origin_type")
    private String originType;

    @Column(name = "flink_type")
    private String flinkType;
}
