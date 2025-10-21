package com.cmsr.onebase.framework.dolphins.dto.workflow.response;

import com.cmsr.onebase.framework.dolphins.dto.workflow.model.WorkflowSimpleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 工作流定义简化列表响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowSimpleListResponseDTO {

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
     * 简化工作流定义列表
     */
    @JsonProperty("data")
    private List<WorkflowSimpleDTO> data;

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
