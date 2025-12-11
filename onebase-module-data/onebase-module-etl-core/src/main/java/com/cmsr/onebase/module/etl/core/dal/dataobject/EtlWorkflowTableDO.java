package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("etl_workflow_table")
public class EtlWorkflowTableDO extends BaseAppEntity {

    @Column(value = "workflow_uuid")
    private String workflowUuid;

    @Column(value = "relation")
    private String relation;

    @Column(value = "datasource_uuid")
    private String datasourceUuid;

    @Column(value = "table_uuid")
    private String tableUuid;
}
