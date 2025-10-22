package com.cmsr.onebase.framework.dolphins.dto.schedule.response;

import com.cmsr.onebase.framework.dolphins.dto.schedule.model.ScheduleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 定时调度分页数据 DTO
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class SchedulePageInfoDTO {

    /**
     * 数据列表
     */
    @JsonProperty("totalList")
    private List<ScheduleDTO> totalList;

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
     * 页码号
     */
    @JsonProperty("pageNo")
    private Integer pageNo;
}
