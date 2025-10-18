package com.cmsr.onebase.framework.dolphins.dto.workflow.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流定义删除版本响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowDeleteVersionResponseDTO {

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
     * 删除结果数据
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
