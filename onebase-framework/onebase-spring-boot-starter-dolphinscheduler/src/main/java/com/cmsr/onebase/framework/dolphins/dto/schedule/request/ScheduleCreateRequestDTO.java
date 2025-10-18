package com.cmsr.onebase.framework.dolphins.dto.schedule.request;

import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.FailureStrategyEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.ReleaseStateEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WarningTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.WorkflowInstancePriorityEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 定时调度创建请求 DTO
 * 
 * 对应 DolphinScheduler API: POST /v2/schedules
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class ScheduleCreateRequestDTO {

    /**
     * 工作流定义编码(必填)
     */
    @NotNull(message = "工作流定义编码不能为空")
    @JsonProperty("workflowDefinitionCode")
    private Long workflowDefinitionCode;

    /**
     * Cron表达式(必填)
     */
    @NotBlank(message = "Cron表达式不能为空")
    @JsonProperty("crontab")
    private String crontab;

    /**
     * 开始时间(必填,格式: yyyy-MM-dd HH:mm:ss)
     */
    @NotBlank(message = "开始时间不能为空")
    @JsonProperty("startTime")
    private String startTime;

    /**
     * 结束时间(必填,格式: yyyy-MM-dd HH:mm:ss)
     */
    @NotBlank(message = "结束时间不能为空")
    @JsonProperty("endTime")
    private String endTime;

    /**
     * 时区ID(必填,例如: Asia/Shanghai)
     */
    @NotBlank(message = "时区ID不能为空")
    @JsonProperty("timezoneId")
    private String timezoneId;

    /**
     * 失败策略(CONTINUE/END,默认CONTINUE)
     */
    @JsonProperty("failureStrategy")
    private FailureStrategyEnum failureStrategy;

    /**
     * 发布状态(ONLINE/OFFLINE,默认OFFLINE)
     */
    @JsonProperty("releaseState")
    private ReleaseStateEnum releaseState;

    /**
     * 告警类型(NONE/SUCCESS/FAILURE/ALL,默认NONE)
     */
    @JsonProperty("warningType")
    private WarningTypeEnum warningType;

    /**
     * 告警组ID(默认0)
     */
    @JsonProperty("warningGroupId")
    private Integer warningGroupId;

    /**
     * 工作流实例优先级(HIGHEST/HIGH/MEDIUM/LOW/LOWEST,默认MEDIUM)
     */
    @JsonProperty("workflowInstancePriority")
    private WorkflowInstancePriorityEnum workflowInstancePriority;

    /**
     * Worker组名称
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
     * 定时参数(JSON格式)
     */
    @JsonProperty("scheduleParam")
    private String scheduleParam;
}
