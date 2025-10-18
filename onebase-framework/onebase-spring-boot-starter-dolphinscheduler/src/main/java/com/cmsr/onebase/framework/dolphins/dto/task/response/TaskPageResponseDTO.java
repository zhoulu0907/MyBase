package com.cmsr.onebase.framework.dolphins.dto.task.response;

import com.cmsr.onebase.framework.dolphins.dto.project.response.PageInfoDTO;
import com.cmsr.onebase.framework.dolphins.dto.task.model.TaskDefinitionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务定义分页响应 DTO
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskPageResponseDTO {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private PageInfoDTO<TaskDefinitionDTO> data;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("failed")
    private Boolean failed;
}
