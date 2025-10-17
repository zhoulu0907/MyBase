package com.cmsr.onebase.framework.dolphins.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页信息 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class PageInfoDTO<T> {

    /**
     * 数据列表
     */
    @JsonProperty("totalList")
    private List<T> totalList;

    /**
     * 总记录数
     */
    @JsonProperty("total")
    private Integer total;

    /**
     * 总页数
     */
    @JsonProperty("totalPage")
    private Integer totalPage;

    /**
     * 每页大小
     */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /**
     * 当前页码
     */
    @JsonProperty("currentPage")
    private Integer currentPage;

    /**
     * 页码（与 currentPage 相同）
     */
    @JsonProperty("pageNo")
    private Integer pageNo;
}
