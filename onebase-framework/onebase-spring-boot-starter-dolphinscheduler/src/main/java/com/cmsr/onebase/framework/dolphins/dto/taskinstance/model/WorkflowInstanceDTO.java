package com.cmsr.onebase.framework.dolphins.dto.taskinstance.model;

import com.cmsr.onebase.framework.dolphins.dto.task.enums.FlagEnum;
import com.cmsr.onebase.framework.dolphins.dto.task.enums.TaskPriorityEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WarningTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums.CommandTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums.TaskDependTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums.WorkflowInstanceStateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程实例 DTO
 *
 * 对应 swagger 中的 WorkflowInstance
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class WorkflowInstanceDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("workflowDefinitionCode")
    private Long workflowDefinitionCode;

    @JsonProperty("workflowDefinitionVersion")
    private Integer workflowDefinitionVersion;

    @JsonProperty("projectCode")
    private Long projectCode;

    @JsonProperty("state")
    private WorkflowInstanceStateEnum state;

    @JsonProperty("stateHistory")
    private String stateHistory;

    @JsonProperty("stateDescList")
    private List<StateDescDTO> stateDescList;

    /**
     * 容错标识
     */
    @JsonProperty("recovery")
    private FlagEnum recovery;

    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("runTimes")
    private Integer runTimes;

    @JsonProperty("name")
    private String name;

    @JsonProperty("host")
    private String host;

    @JsonProperty("workflowDefinition")
    private Object workflowDefinition;

    /**
     * 命令类型
     */
    @JsonProperty("commandType")
    private CommandTypeEnum commandType;

    @JsonProperty("commandParam")
    private String commandParam;

    /**
     * 任务依赖类型
     */
    @JsonProperty("taskDependType")
    private TaskDependTypeEnum taskDependType;

    @JsonProperty("maxTryTimes")
    @Deprecated
    private Integer maxTryTimes;

    /**
     * 失败策略
     */
    @JsonProperty("failureStrategy")
    private FailureStrategyEnum failureStrategy;

    /**
     * 告警类型
     */
    @JsonProperty("warningType")
    private WarningTypeEnum warningType;

    @JsonProperty("warningGroupId")
    private Integer warningGroupId;

    @JsonProperty("scheduleTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduleTime;

    @JsonProperty("commandStartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commandStartTime;

    @JsonProperty("globalParams")
    private String globalParams;

    @JsonProperty("dagData")
    private Object dagData;

    @JsonProperty("executorId")
    private Integer executorId;

    @JsonProperty("executorName")
    private String executorName;

    @JsonProperty("tenantCode")
    private String tenantCode;

    @JsonProperty("queue")
    private String queue;

    /**
     * 是否子工作流
     */
    @JsonProperty("isSubWorkflow")
    private FlagEnum isSubWorkflow;

    @JsonProperty("locations")
    private String locations;

    @JsonProperty("historyCmd")
    private String historyCmd;

    @JsonProperty("dependenceScheduleTimes")
    private String dependenceScheduleTimes;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("workflowInstancePriority")
    private TaskPriorityEnum workflowInstancePriority;

    @JsonProperty("workerGroup")
    private String workerGroup;

    @JsonProperty("environmentCode")
    private Long environmentCode;

    @JsonProperty("timeout")
    private Integer timeout;

    @JsonProperty("varPool")
    private String varPool;

    @JsonProperty("nextWorkflowInstanceId")
    @Deprecated
    private Integer nextWorkflowInstanceId;

    @JsonProperty("dryRun")
    private Integer dryRun;

    @JsonProperty("restartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime restartTime;

    /**
     * 补数时的命令类型
     */
    @JsonProperty("cmdTypeIfComplement")
    private CommandTypeEnum cmdTypeIfComplement;

    @JsonProperty("complementData")
    private Boolean complementData;
}
