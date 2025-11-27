package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(value = "etl_execution_log")
public class ETLExecutionLogDO extends BaseAppEntity {

    @Column(value = "workflow_id")
    private Long workflowId;

    @Column(value = "business_date")
    private LocalDateTime bussinessDate;

    @Column(value = "start_time")
    private LocalDateTime startTime;

    @Column(value = "end_time")
    private LocalDateTime endTime;

    @Column(value = "duration_time")
    private Long durationTime;

    @Column(value = "trigger_type")
    private String triggerType;

    @Column(value = "trigger_user")
    private Long triggerUser;

    @Column(value = "task_status")
    private String taskStatus;

}
