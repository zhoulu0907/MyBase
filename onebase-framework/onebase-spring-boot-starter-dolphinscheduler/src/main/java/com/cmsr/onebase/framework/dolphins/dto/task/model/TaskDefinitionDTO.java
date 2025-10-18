package com.cmsr.onebase.framework.dolphins.dto.task.model;

import com.cmsr.onebase.framework.dolphins.dto.common.PropertyDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务定义 DTO
 *
 * 对应 swagger 中 TaskDefinition
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskDefinitionDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("code")
    private Long code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("description")
    private String description;

    @JsonProperty("projectCode")
    private Long projectCode;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("taskType")
    private String taskType;

    /**
     * 任务参数，可能是JSON对象或字符串
     */
    @JsonProperty("taskParams")
    private Object taskParams;

    @JsonProperty("taskParamList")
    private List<PropertyDTO> taskParamList;

    @JsonProperty("taskParamMap")
    private Map<String, String> taskParamMap;

    @JsonProperty("flag")
    private FlagEnum flag;

    @JsonProperty("taskPriority")
    private TaskPriorityEnum taskPriority;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("projectName")
    private String projectName;

    @JsonProperty("workerGroup")
    private String workerGroup;

    @JsonProperty("environmentCode")
    private Long environmentCode;

    @JsonProperty("failRetryTimes")
    private Integer failRetryTimes;

    @JsonProperty("failRetryInterval")
    private Integer failRetryInterval;

    @JsonProperty("timeoutFlag")
    private TimeoutFlagEnum timeoutFlag;

    @JsonProperty("timeoutNotifyStrategy")
    private TimeoutNotifyStrategyEnum timeoutNotifyStrategy;

    @JsonProperty("timeout")
    private Integer timeout;

    @JsonProperty("delayTime")
    private Integer delayTime;

    /**
     * 资源 ID 列表（逗号分隔），已废弃
     */
    @JsonProperty("resourceIds")
    private String resourceIds;

    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonProperty("modifyBy")
    private String modifyBy;

    @JsonProperty("taskGroupId")
    private Integer taskGroupId;

    @JsonProperty("taskGroupPriority")
    private Integer taskGroupPriority;

    @JsonProperty("cpuQuota")
    private Integer cpuQuota;

    @JsonProperty("memoryMax")
    private Integer memoryMax;

    @JsonProperty("taskExecuteType")
    private TaskExecuteTypeEnum taskExecuteType;
}
