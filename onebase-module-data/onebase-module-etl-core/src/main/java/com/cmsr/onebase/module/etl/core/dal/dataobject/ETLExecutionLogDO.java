package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "etl_execution_log")
@com.mybatisflex.annotation.Table("etl_execution_log")
public class ETLExecutionLogDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "workflow_id")
    private Long workflowId;

    @Column(name = "business_date")
    private LocalDateTime bussinessDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_time")
    private Long durationTime;

    @Column(name = "trigger_type")
    private String triggerType;

    @Column(name = "trigger_user")
    private Long triggerUser;

    @Column(name = "task_status")
    private String taskStatus;

}
