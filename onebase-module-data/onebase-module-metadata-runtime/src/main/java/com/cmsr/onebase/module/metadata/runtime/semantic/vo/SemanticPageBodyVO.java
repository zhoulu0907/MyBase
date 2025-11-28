package com.cmsr.onebase.module.metadata.runtime.semantic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "分页请求体：data 与分页排序过滤")
@Data
/**
 * 分页请求体 VO
 *
 * <p>用于分页查询场景，包含分页参数、排序与过滤条件，
 * 可附带顶层业务数据包装。</p>
 */
public class SemanticPageBodyVO {

    @Schema(description = "业务数据包装")
    private Map<String, Object> data;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "分页大小")
    private Integer pageSize;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方向 ASC/DESC")
    private String sortDirection;

    @Schema(description = "过滤条件")
    private Map<String, Object> filters;
}
