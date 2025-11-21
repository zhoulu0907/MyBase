package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "etl_schedule_job")
@com.mybatisflex.annotation.Table("etl_schedule_job")
public class ETLScheduleJobDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "workflow_id")
    private Long workflowId;

    @Column(name = "job_id")
    private String jobId;

    @Column(name = "job_status")
    private String jobStatus;

    @Column(name = "last_job_time")
    private LocalDateTime lastJobTime;

    @Column(name = "last_success_time")
    private LocalDateTime lastSuccessTime;

}
