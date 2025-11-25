package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.data.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "etl_flink_mapping")
@com.mybatisflex.annotation.Table("etl_flink_mapping")
public class ETLFlinkMappingDO extends BaseEntity {

    @Column(name = "datasource_type")
    private String datasourceType;

    @Column(name = "origin_type")
    private String originType;

    @Column(name = "flink_type")
    private String flinkType;
}
