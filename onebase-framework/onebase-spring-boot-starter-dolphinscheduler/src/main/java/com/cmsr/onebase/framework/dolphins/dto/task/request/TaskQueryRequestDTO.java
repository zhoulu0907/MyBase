package com.cmsr.onebase.framework.dolphins.dto.task.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务定义筛选请求 DTO
 *
 * 对应 swagger "TASK-QUERY"
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskQueryRequestDTO {

    /** 每页大小 */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /** 页码 */
    @JsonProperty("pageNo")
    private Integer pageNo;

    /** 项目名称（可选） */
    @JsonProperty("projectName")
    private String projectName;

    /** 任务名称（可选） */
    @JsonProperty("name")
    private String name;

    /** 任务类型（可选，如 SHELL） */
    @JsonProperty("taskType")
    private String taskType;
}
