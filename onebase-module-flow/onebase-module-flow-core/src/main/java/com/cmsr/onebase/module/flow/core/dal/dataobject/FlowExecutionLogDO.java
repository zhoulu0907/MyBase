package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "flow_execution_log")
public class FlowExecutionLogDO extends BaseDO {

    @Column(name = "trigger_user_id")
    private Long triggerUserId;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "execution_uuid")
    private String executionUuid;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "process_id", nullable = false)
    private Long processId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "execution_result")
    private String executionResult;

    @Column(name = "log_text")
    private String logText;

    @Column(name = "error_message")
    private String errorMessage;


}