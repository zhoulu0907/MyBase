package com.cmsr.onebase.framework.remote.dto.schedule;

import com.cmsr.onebase.framework.remote.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.remote.enums.PriorityEnum;
import com.cmsr.onebase.framework.remote.enums.WarningTypeEnum;

import lombok.Data;

/**
 * 调度定义创建/更新参数 DTO
 */
@Data
public class ScheduleDefineParamDTO {
    private ScheduleDTO schedule;
    private FailureStrategyEnum failureStrategy = FailureStrategyEnum.END;
    private WarningTypeEnum warningType = WarningTypeEnum.NONE;
    private PriorityEnum processInstancePriority = PriorityEnum.MEDIUM;
    private String warningGroupId = "0";
    private String workerGroup = "default";
    private String environmentCode = "";
    private Long workflowDefinitionCode;
}
