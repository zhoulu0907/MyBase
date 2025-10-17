package com.cmsr.onebase.framework.dolphins.dto.taskinstance.model;

import com.cmsr.onebase.framework.dolphins.dto.task.enums.TaskPriorityEnum;
import com.cmsr.onebase.framework.dolphins.dto.task.enums.TaskExecuteTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.task.enums.FlagEnum;
import com.cmsr.onebase.framework.dolphins.dto.task.model.TaskDefinitionDTO;
import com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums.AlertFlagEnum;
import com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums.TaskInstanceStateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实例 DTO
 *
 * 对应 swagger 中的 TaskInstance
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskInstanceDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("taskType")
    private String taskType;

    @JsonProperty("workflowInstanceId")
    private Integer workflowInstanceId;

    @JsonProperty("workflowInstanceName")
    private String workflowInstanceName;

    @JsonProperty("projectCode")
    private Long projectCode;

    @JsonProperty("taskCode")
    private Long taskCode;

    @JsonProperty("taskDefinitionVersion")
    private Integer taskDefinitionVersion;

    @JsonProperty("processDefinitionName")
    private String processDefinitionName;

    @JsonProperty("taskGroupPriority")
    private Integer taskGroupPriority;

    @JsonProperty("state")
    private TaskInstanceStateEnum state;

    @JsonProperty("firstSubmitTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstSubmitTime;

    @JsonProperty("submitTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("host")
    private String host;

    @JsonProperty("executePath")
    private String executePath;

    @JsonProperty("logPath")
    private String logPath;

    @JsonProperty("retryTimes")
    private Integer retryTimes;

    @JsonProperty("alertFlag")
    private AlertFlagEnum alertFlag;

    @JsonProperty("workflowInstance")
    private WorkflowInstanceDTO workflowInstance;

    @JsonProperty("workflowDefinition")
    private Object workflowDefinition;

    @JsonProperty("taskDefine")
    private TaskDefinitionDTO taskDefine;

    @JsonProperty("pid")
    private Integer pid;

    @JsonProperty("appLink")
    private String appLink;

    @JsonProperty("flag")
    private FlagEnum flag;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("maxRetryTimes")
    private Integer maxRetryTimes;

    @JsonProperty("retryInterval")
    private Integer retryInterval;

    @JsonProperty("taskInstancePriority")
    private TaskPriorityEnum taskInstancePriority;

    @JsonProperty("workflowInstancePriority")
    private TaskPriorityEnum workflowInstancePriority;

    @JsonProperty("workerGroup")
    private String workerGroup;

    @JsonProperty("environmentCode")
    private Long environmentCode;

    @JsonProperty("environmentConfig")
    private String environmentConfig;

    @JsonProperty("executorId")
    private Integer executorId;

    @JsonProperty("varPool")
    private String varPool;

    @JsonProperty("executorName")
    private String executorName;

    @JsonProperty("delayTime")
    private Integer delayTime;

    @JsonProperty("taskParams")
    private String taskParams;

    @JsonProperty("dryRun")
    private Integer dryRun;

    @JsonProperty("taskGroupId")
    private Integer taskGroupId;

    @JsonProperty("cpuQuota")
    private Integer cpuQuota;

    @JsonProperty("memoryMax")
    private Integer memoryMax;

    @JsonProperty("taskExecuteType")
    private TaskExecuteTypeEnum taskExecuteType;
}
