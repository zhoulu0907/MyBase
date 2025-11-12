package com.cmsr.onebase.module.etl.core.vo.etl;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExecutionLogVO {

    private Long applicationId;

    private Long workflowId;

    private LocalDateTime businessDate;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long duration;

    private String triggerType;

    private String triggerUser;

    private String taskStatus;
}
