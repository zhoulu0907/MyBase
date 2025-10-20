package com.cmsr.onebase.framework.dolphins.dto.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 项目查询请求 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ProjectQueryDTO {

    /**
     * 搜索值
     */
    @JsonProperty("searchVal")
    private String searchVal;

    /**
     * 页码号
     */
    @NotNull(message = "页码号不能为空")
    @Min(value = 1, message = "页码号必须大于 0")
    @JsonProperty("pageNo")
    private Integer pageNo;

    /**
     * 页大小
     */
    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小必须大于 0")
    @JsonProperty("pageSize")
    private Integer pageSize;
}
