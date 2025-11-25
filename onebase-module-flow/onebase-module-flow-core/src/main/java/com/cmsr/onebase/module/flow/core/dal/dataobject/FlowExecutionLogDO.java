package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.data.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(value = "flow_execution_log")
public class FlowExecutionLogDO extends BaseEntity {

    @Column(value = "trigger_user_id")
    private Long triggerUserId;

    @Column(value = "trace_id")
    private String traceId;

    @Column(value = "execution_uuid")
    private String executionUuid;

    @Column(value = "application_id")
    private Long applicationId;

    @Column(value = "process_id")
    private Long processId;

    @Column(value = "start_time")
    private LocalDateTime startTime;

    @Column(value = "end_time")
    private LocalDateTime endTime;

    @Column(value = "duration_time")
    private Long durationTime;

    @Column(value = "execution_result")
    private String executionResult;

    @Column(value = "log_text")
    private String logText;

    @Column(value = "error_message")
    private String errorMessage;


}