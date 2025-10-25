package com.cmsr.onebase.framework.ds.model.schedule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleInfoResp {

    private int id;

    private long workflowDefinitionCode;

    private String workflowDefinitionName;

    private String projectName;

    private String definitionDescription;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String timezoneId;

    private String crontab;

    private String failureStrategy;

    private String warningType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private int userId;

    private String userName;

    private String releaseState;

    private int warningGroupId;

    private String workflowInstancePriority;

    private String workerGroup;

    private Long environmentCode;

    private String environmentName;
}
