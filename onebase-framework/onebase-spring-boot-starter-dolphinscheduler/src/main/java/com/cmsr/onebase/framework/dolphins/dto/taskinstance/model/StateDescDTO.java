package com.cmsr.onebase.framework.dolphins.dto.taskinstance.model;

import com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums.WorkflowInstanceStateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 状态描述 DTO
 *
 * 对应 swagger 中的 StateDesc
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class StateDescDTO {

    @JsonProperty("time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @JsonProperty("state")
    private WorkflowInstanceStateEnum state;

    @JsonProperty("desc")
    private String desc;
}
