package com.cmsr.onebase.framework.remote.dto.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/** 调度信息响应 DTO */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleInfoRespDTO {
    private int id;
    private long workflowDefinitionCode;
    private String workflowDefinitionName;
    private String projectName;
    private String definitionDescription;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private String timezoneId;
    private String crontab;
    private String failureStrategy;
    private String warningType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private int userId;
    private String userName;
    private String releaseState;
    private int warningGroupId;
    private String workflowInstancePriority;
    private String tenantCode;
    private String workerGroup;
    private Long environmentCode;
    private String environmentName;
}

