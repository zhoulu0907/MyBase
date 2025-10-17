package com.cmsr.onebase.framework.dolphins.dto.taskinstance.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务实例 SavePoint 响应 DTO
 *
 * 对应 swagger 中的 ResultObject
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskInstanceSavePointResponseDTO {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("failed")
    private Boolean failed;
}
