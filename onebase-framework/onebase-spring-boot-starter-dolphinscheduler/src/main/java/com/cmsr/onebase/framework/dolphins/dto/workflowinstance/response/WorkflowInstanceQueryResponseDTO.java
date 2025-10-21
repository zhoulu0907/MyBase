package com.cmsr.onebase.framework.dolphins.dto.workflowinstance.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流实例详情响应 DTO
 *
 * 对应 DolphinScheduler API: GET /v2/workflow-instances/{workflowInstanceId}
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class WorkflowInstanceQueryResponseDTO {

    /**
     * 响应码
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 响应消息
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 响应数据
     */
    @JsonProperty("data")
    private Object data;

    /**
     * 是否成功
     */
    @JsonProperty("success")
    private Boolean success;

    /**
     * 是否失败
     */
    @JsonProperty("failed")
    private Boolean failed;
}
