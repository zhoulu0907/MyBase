package com.cmsr.onebase.framework.remote.dto.instance;

import com.cmsr.onebase.framework.remote.enums.ExecuteTypeEnum;
import com.cmsr.onebase.framework.remote.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.remote.enums.PriorityEnum;
import com.cmsr.onebase.framework.remote.enums.TaskDependTypeEnum;
import com.cmsr.onebase.framework.remote.enums.WarningTypeEnum;
import lombok.Data;

/**
 * 流程实例创建参数 DTO
 */
@Data
public class ProcessInstanceCreateParamDTO {
    private FailureStrategyEnum failureStrategy;
    private Long processDefinitionCode;
    private PriorityEnum processInstancePriority;
    private String scheduleTime;
    private Long warningGroupId;
    private WarningTypeEnum warningType;
    private Integer dryRun;
    private String environmentCode;
    private ExecuteTypeEnum execType;
    private String expectedParallelismNumber;
    private String runMode;
    private String startNodeList;
    private String startParams;
    private TaskDependTypeEnum taskDependType;
    private String workerGroup;
}

