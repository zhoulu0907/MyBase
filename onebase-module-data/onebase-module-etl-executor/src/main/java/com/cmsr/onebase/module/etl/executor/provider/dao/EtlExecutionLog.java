package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author：huangjie
 * @Date：2025/11/14 14:48
 */
@Data
public class EtlExecutionLog {

    private Long id;

    private Long applicationId;

    private Long workflowUuid;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long durationTime;

    private String triggerType;

    private Long triggerUser;

    private String taskStatus;

    private String errorMessage;

    public void calcDurationTime() {
        Duration duration = Duration.between(startTime, endTime);
        durationTime = duration.toMillis();
    }
}
