package com.cmsr.onebase.framework.dolphins.dto.taskinstance.model;

import com.cmsr.onebase.framework.dolphins.dto.task.enums.TaskPriorityEnum;
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

    @JsonProperty("recovery")
    private String recovery;

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

    @JsonProperty("commandType")
    private String commandType;

    @JsonProperty("commandParam")
    private String commandParam;

    @JsonProperty("taskDependType")
    private String taskDependType;

    @JsonProperty("maxTryTimes")
    @Deprecated
    private Integer maxTryTimes;

    @JsonProperty("failureStrategy")
    private String failureStrategy;

    @JsonProperty("warningType")
    private String warningType;

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

    @JsonProperty("isSubWorkflow")
    private String isSubWorkflow;

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

    @JsonProperty("cmdTypeIfComplement")
    private String cmdTypeIfComplement;

    @JsonProperty("complementData")
    private Boolean complementData;
}
