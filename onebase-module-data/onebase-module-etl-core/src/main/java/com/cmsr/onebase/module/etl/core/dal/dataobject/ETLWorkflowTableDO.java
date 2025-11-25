package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.data.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("etl_workflow_table")
public class ETLWorkflowTableDO extends BaseTenantEntity {

    @Column(value = "workflow_id")
    private Long workflowId;

    @Column(value = "relation")
    private String relation;

    @Column(value = "datasource_id")
    private Long datasourceId;

    @Column(value = "table_id")
    private Long tableId;
}
