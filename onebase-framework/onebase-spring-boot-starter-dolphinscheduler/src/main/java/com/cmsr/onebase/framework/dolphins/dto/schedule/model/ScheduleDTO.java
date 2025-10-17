package com.cmsr.onebase.framework.dolphins.dto.schedule.model;

import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.ReleaseStateEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WarningTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WorkflowInstancePriorityEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时调度模型 DTO
 *
 * 对应 swagger 中的 Schedule 定义
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class ScheduleDTO {

    /**
     * 定时调度ID
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * 工作流定义编码
     */
    @JsonProperty("workflowDefinitionCode")
    private Long workflowDefinitionCode;

    /**
     * 工作流定义名称
     */
    @JsonProperty("workflowDefinitionName")
    private String workflowDefinitionName;

    /**
     * 项目名称
     */
    @JsonProperty("projectName")
    private String projectName;

    /**
     * 定义描述
     */
    @JsonProperty("definitionDescription")
    private String definitionDescription;

    /**
     * 开始时间
     */
    @JsonProperty("startTime")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonProperty("endTime")
    private LocalDateTime endTime;

    /**
     * 时区ID
     */
    @JsonProperty("timezoneId")
    private String timezoneId;

    /**
     * Cron表达式
     */
    @JsonProperty("crontab")
    private String crontab;

    /**
     * 失败策略(END/CONTINUE)
     */
    @JsonProperty("failureStrategy")
    private FailureStrategyEnum failureStrategy;

    /**
     * 告警类型(NONE/SUCCESS/FAILURE/ALL)
     */
    @JsonProperty("warningType")
    private WarningTypeEnum warningType;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;

    /**
     * 用户ID
     */
    @JsonProperty("userId")
    private Integer userId;

    /**
     * 用户名
     */
    @JsonProperty("userName")
    private String userName;

    /**
     * 发布状态(OFFLINE/ONLINE)
     */
    @JsonProperty("releaseState")
    private ReleaseStateEnum releaseState;

    /**
     * 告警组ID
     */
    @JsonProperty("warningGroupId")
    private Integer warningGroupId;

    /**
     * 工作流实例优先级(HIGHEST/HIGH/MEDIUM/LOW/LOWEST)
     */
    @JsonProperty("workflowInstancePriority")
    private WorkflowInstancePriorityEnum workflowInstancePriority;

    /**
     * Worker组
     */
    @JsonProperty("workerGroup")
    private String workerGroup;

    /**
     * 租户编码
     */
    @JsonProperty("tenantCode")
    private String tenantCode;

    /**
     * 环境编码
     */
    @JsonProperty("environmentCode")
    private Long environmentCode;

    /**
     * 环境名称
     */
    @JsonProperty("environmentName")
    private String environmentName;
}
