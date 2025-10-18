package com.cmsr.onebase.framework.dolphins.dto.task.response;

import com.cmsr.onebase.framework.dolphins.dto.task.model.TaskDefinitionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取任务定义详情响应 DTO
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskGetResponseDTO {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private TaskDefinitionDTO data;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("failed")
    private Boolean failed;
}
