package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.common.meta.TableMeta;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "etl_table")
public class ETLTableDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "schema_id")
    private Long schemaId;

    @Column(name = "table_type")
    private String tableType;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "meta_info")
    private String metaInfo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "declaration")
    private String declaration;

    public TableMeta getMetaInfo() {
        return JsonUtils.parseObject(this.metaInfo, TableMeta.class);
    }

    public void setMetaInfo(TableMeta metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

}
