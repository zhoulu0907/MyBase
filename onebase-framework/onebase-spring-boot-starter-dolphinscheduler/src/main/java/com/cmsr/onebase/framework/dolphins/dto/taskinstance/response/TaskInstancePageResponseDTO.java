package com.cmsr.onebase.framework.dolphins.dto.taskinstance.response;

import com.cmsr.onebase.framework.dolphins.dto.taskinstance.model.PageInfoTaskInstanceDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务实例分页查询响应 DTO
 *
 * 对应 swagger 中的 ResultPageInfoTaskInstance
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskInstancePageResponseDTO {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private PageInfoTaskInstanceDTO data;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("failed")
    private Boolean failed;
}
