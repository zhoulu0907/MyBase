package com.cmsr.onebase.framework.ds.model.task;

import com.cmsr.onebase.framework.ds.model.task.def.AbstractTask;
import lombok.Data;

@Data
public class TaskDefinition {

    private Long code;

    private Integer version;

    private String name;

    private String description;

    private String taskType;

    private AbstractTask taskParams;

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

    private String taskExecuteType;

    private Integer cpuQuota = -1;

    private Long memoryMax = -1L;

    private String isCache = "NO";
}
