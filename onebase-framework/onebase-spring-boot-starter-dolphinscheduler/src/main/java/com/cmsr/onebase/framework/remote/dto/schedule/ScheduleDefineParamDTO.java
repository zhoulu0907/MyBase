package com.cmsr.onebase.framework.remote.dto.schedule;

import com.cmsr.onebase.framework.remote.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.remote.enums.PriorityEnum;
import com.cmsr.onebase.framework.remote.enums.WarningTypeEnum;

/**
 * 调度定义创建/更新参数 DTO
 */
public class ScheduleDefineParamDTO {
    private ScheduleDTO schedule;
    private FailureStrategyEnum failureStrategy = FailureStrategyEnum.END;
    private WarningTypeEnum warningType = WarningTypeEnum.NONE;
    private PriorityEnum processInstancePriority = PriorityEnum.MEDIUM;
    private String warningGroupId = "0";
    private String workerGroup = "default";
    private String environmentCode = "";
    private Long workflowDefinitionCode;

    public ScheduleDTO getSchedule() { return schedule; }
    public void setSchedule(ScheduleDTO schedule) { this.schedule = schedule; }
    public FailureStrategyEnum getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(FailureStrategyEnum failureStrategy) { this.failureStrategy = failureStrategy; }
    public WarningTypeEnum getWarningType() { return warningType; }
    public void setWarningType(WarningTypeEnum warningType) { this.warningType = warningType; }
    public PriorityEnum getProcessInstancePriority() { return processInstancePriority; }
    public void setProcessInstancePriority(PriorityEnum processInstancePriority) { this.processInstancePriority = processInstancePriority; }
    public String getWarningGroupId() { return warningGroupId; }
    public void setWarningGroupId(String warningGroupId) { this.warningGroupId = warningGroupId; }
    public String getWorkerGroup() { return workerGroup; }
    public void setWorkerGroup(String workerGroup) { this.workerGroup = workerGroup; }
    public String getEnvironmentCode() { return environmentCode; }
    public void setEnvironmentCode(String environmentCode) { this.environmentCode = environmentCode; }
    public Long getWorkflowDefinitionCode() { return workflowDefinitionCode; }
    public void setWorkflowDefinitionCode(Long workflowDefinitionCode) { this.workflowDefinitionCode = workflowDefinitionCode; }
}
