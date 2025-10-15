package com.cmsr.onebase.framework.remote.dto.process;

import com.cmsr.onebase.framework.remote.enums.TaskExecuteTypeEnum;
import lombok.Data;

/**
 * 任务定义 DTO（仅数据传输）
 */
@Data
public class TaskDefinitionDTO {
    private Long code;
    private Integer version;
    private String name;
    private String description;
    private String taskType;
    private Object taskParams;
    private String flag;
    private String taskPriority;
    private String workerGroup;
    private String failRetryTimes;
    private String failRetryInterval;
    private String timeoutFlag;
    private String timeoutNotifyStrategy;
    private Integer timeout = 0;
    private String delayTime = "0";
    private Integer environmentCode = -1;
    private TaskExecuteTypeEnum taskExecuteType;
    private Integer cpuQuota = -1;
    private Long memoryMax = -1L;
    private String isCache = "NO";
}

