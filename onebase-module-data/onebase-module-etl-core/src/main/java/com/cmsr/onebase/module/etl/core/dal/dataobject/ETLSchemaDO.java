package com.cmsr.onebase.module.etl.core.dal.dataobject;


import com.cmsr.onebase.framework.orm.data.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "etl_schema")
public class ETLSchemaDO extends BaseAppEntity {

    @Column(value = "datasource_id")
    private Long datasourceId;

    @Column(value = "catalog_id")
    private Long catalogId;

    @Column(value = "schema_name")
    private String schemaName;

    @Column(value = "display_name")
    private String displayName;

    // currently no usage, ;.
    @Column(value = "meta_info")
    private String metaInfo;

    @Column(value = "remarks")
    private String remarks;

    @Column(value = "declaration")
    private String declaration;
}
