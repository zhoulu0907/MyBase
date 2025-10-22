package com.cmsr.onebase.framework.dolphins.dto.taskinstance.response;

import com.cmsr.onebase.framework.dolphins.dto.taskinstance.model.TaskInstanceDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务实例查询响应 DTO
 *
 * 对应 swagger 中查询单个任务实例的返回
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskInstanceQueryResponseDTO {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private TaskInstanceDTO data;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("failed")
    private Boolean failed;
}
