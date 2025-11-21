package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "etl_workflow_table")
@com.mybatisflex.annotation.Table("etl_workflow_table")
public class ETLWorkflowTableDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "workflow_id")
    private Long workflowId;

    @Column(name = "relation")
    private String relation;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "table_id")
    private Long tableId;
}
