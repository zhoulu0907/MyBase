package com.cmsr.onebase.framework.dolphins.dto.workflow.request;

import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ReleaseStateEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流分页查询请求参数
 *
 * 对应 DolphinScheduler API: POST /v2/workflows/query
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowQueryRequestDTO {

    /**
     * 页码号(必填)
     */
    @JsonProperty("pageNo")
    private Integer pageNo;

    /**
     * 页大小(必填)
     */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 项目名称(可选)
     */
    @JsonProperty("projectName")
    private String projectName;

    /**
     * 工作流名称(可选)
     */
    @JsonProperty("workflowName")
    private String workflowName;

    /**
     * 发布状态(可选: ONLINE/OFFLINE)
     */
    @JsonProperty("releaseState")
    private ReleaseStateEnum releaseState;

    /**
     * 调度发布状态(可选: ONLINE/OFFLINE)
     */
    @JsonProperty("scheduleReleaseState")
    private ReleaseStateEnum scheduleReleaseState;
}

