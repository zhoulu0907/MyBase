package com.cmsr.onebase.framework.dolphins.dto.workflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 工作流定义版本分页信息 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class PageInfoWorkflowVersionDTO {

    /**
     * 总记录数
     */
    @JsonProperty("totalCount")
    private Integer totalCount;

    /**
     * 总页数
     */
    @JsonProperty("totalPage")
    private Integer totalPage;

    /**
     * 当前页码
     */
    @JsonProperty("pageNo")
    private Integer pageNo;

    /**
     * 每页大小
     */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 起始记录索引
     */
    @JsonProperty("start")
    private Integer start;

    /**
     * 版本列表
     */
    @JsonProperty("totalList")
    private List<WorkflowVersionDTO> totalList;

    /**
     * 当前页列表
     */
    @JsonProperty("currentPage")
    private Integer currentPage;
}
