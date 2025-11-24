package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.data.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "etl_table")
public class ETLTableDO extends BaseTenantEntity {

    @Column(value = "datasource_id")
    private Long datasourceId;

    @Column(value = "catalog_id")
    private Long catalogId;

    @Column(value = "schema_id")
    private Long schemaId;

    @Column(value = "table_type")
    private String tableType;

    @Column(value = "table_name")
    private String tableName;

    @Column(value = "display_name")
    private String displayName;

    @Column(value = "meta_info")
    private String metaInfo;

    @Column(value = "remarks")
    private String remarks;

    @Column(value = "declaration")
    private String declaration;

}
