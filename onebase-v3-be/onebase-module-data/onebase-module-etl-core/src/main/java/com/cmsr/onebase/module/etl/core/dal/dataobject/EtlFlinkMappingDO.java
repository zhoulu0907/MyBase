package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "etl_flink_mapping")
public class EtlFlinkMappingDO extends BaseEntity {

    @Column(value = "datasource_type")
    private String datasourceType;

    @Column(value = "origin_type")
    private String originType;

    @Column(value = "flink_type")
    private String flinkType;
}
