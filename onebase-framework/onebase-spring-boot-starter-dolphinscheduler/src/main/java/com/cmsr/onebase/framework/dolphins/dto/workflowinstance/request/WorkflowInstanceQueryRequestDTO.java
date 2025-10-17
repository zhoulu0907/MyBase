package com.cmsr.onebase.framework.dolphins.dto.workflowinstance.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工作流实例查询请求 DTO
 * 
 * 对应 DolphinScheduler API: GET /v2/workflow-instances
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class WorkflowInstanceQueryRequestDTO {

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
     * 项目名称
     */
    @JsonProperty("projectName")
    private String projectName;

    /**
     * 工作流名称
     */
    @JsonProperty("workflowName")
    private String workflowName;

    /**
     * 主机地址
     */
    @JsonProperty("host")
    private String host;

    /**
     * 开始时间
     */
    @JsonProperty("startDate")
    private String startDate;

    /**
     * 结束时间
     */
    @JsonProperty("endDate")
    private String endDate;

    /**
     * 状态(状态码,非枚举)
     */
    @JsonProperty("state")
    private Integer state;
}

