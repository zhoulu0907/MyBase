package com.cmsr.onebase.framework.remote.dto.task;

import lombok.Data;

/** 任务定义信息（简要）DTO */
@Data
public class TaskDefinitionRespDTO {
    private Long code;
    private Integer version;
    private String name;
    private String description;
    private String taskType;
    private String flag;
    private String taskPriority;
    private String workerGroup;
    private String failRetryTimes;
    private String failRetryInterval;
    private Integer timeout;
    private Integer environmentCode;
    private String taskExecuteType;
    private Integer cpuQuota;
    private Long memoryMax;
    private String isCache;
}

