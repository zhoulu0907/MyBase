package com.cmsr.onebase.framework.dolphins.dto.taskinstance.model;

import com.cmsr.onebase.framework.dolphins.dto.taskinstance.model.TaskInstanceDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 任务实例分页信息 DTO
 *
 * 对应 swagger 中的 PageInfoTaskInstance
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class PageInfoTaskInstanceDTO {

    @JsonProperty("totalList")
    private List<TaskInstanceDTO> totalList;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("totalPage")
    private Integer totalPage;

    @JsonProperty("pageSize")
    private Integer pageSize;

    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("pageNo")
    private Integer pageNo;
}
