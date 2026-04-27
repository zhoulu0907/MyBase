package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("etl_workflow")
public class EtlWorkflowDO extends BaseAppEntity {

    @Column("workflow_uuid")
    private String workflowUuid;

    @Column("workflow_name")
    private String workflowName;

    @Column("declaration")
    private String declaration;

    @Column("config")
    private String config;

    @Column("is_enabled")
    private Integer isEnabled;

    // FIXED, OBSERVE, MANUALLY(default)
    @Column("schedule_strategy")
    private String scheduleStrategy;

    @Column("schedule_config")
    private String scheduleConfig;
}
