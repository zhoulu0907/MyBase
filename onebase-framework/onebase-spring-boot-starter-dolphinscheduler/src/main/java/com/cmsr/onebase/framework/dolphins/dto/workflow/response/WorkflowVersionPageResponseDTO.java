package com.cmsr.onebase.framework.dolphins.dto.workflow.response;

import com.cmsr.onebase.framework.dolphins.dto.workflow.model.WorkflowVersionDTO;
import com.cmsr.onebase.framework.dolphins.dto.workflow.model.PageInfoWorkflowVersionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流定义版本分页查询响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowVersionPageResponseDTO {

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
     * 版本分页数据
     */
    @JsonProperty("data")
    private PageInfoWorkflowVersionDTO data;

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
