package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "etl_catalog")
public class ETLCatalogDO extends BaseAppEntity {

    @Column(value = "datasource_id")
    private Long datasourceId;

    @Column(value = "catalog_name")
    private String catalogName;

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
