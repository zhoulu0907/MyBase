package com.cmsr.onebase.framework.dolphins.dto.workflow.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流定义删除响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowDeleteResponseDTO {

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
     * 删除成功的工作流定义编码
     */
    @JsonProperty("data")
    private Long data;

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
