package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(value = "etl_schedule_job")
public class EtlScheduleJobDO extends BaseAppEntity {

    @Column(value = "workflow_uuid")
    private String workflowUuid;

    @Column(value = "job_id")
    private String jobId;

    @Column(value = "job_status")
    private String jobStatus;

    @Column(value = "last_job_time")
    private LocalDateTime lastJobTime;

    @Column(value = "last_success_time")
    private LocalDateTime lastSuccessTime;

}
