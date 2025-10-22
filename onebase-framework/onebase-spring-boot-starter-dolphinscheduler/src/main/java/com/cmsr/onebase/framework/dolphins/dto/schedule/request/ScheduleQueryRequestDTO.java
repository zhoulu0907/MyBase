package com.cmsr.onebase.framework.dolphins.dto.schedule.request;

import com.cmsr.onebase.framework.dolphins.dto.schedule.enums.ReleaseStateEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 定时调度查询请求 DTO
 * 
 * 对应 DolphinScheduler API: POST /v2/schedules/filter
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class ScheduleQueryRequestDTO {

    /**
     * 页码号(必填)
     */
    @NotNull(message = "页码号不能为空")
    @JsonProperty("pageNo")
    private Integer pageNo;

    /**
     * 页大小(必填)
     */
    @NotNull(message = "页大小不能为空")
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 项目名称(可选)
     */
    @JsonProperty("projectName")
    private String projectName;

    /**
     * 工作流定义名称(可选)
     */
    @JsonProperty("workflowDefinitionName")
    private String workflowDefinitionName;

    /**
     * 发布状态(ONLINE/OFFLINE,可选)
     */
    @JsonProperty("releaseState")
    private ReleaseStateEnum releaseState;
}
