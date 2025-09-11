package com.cmsr.onebase.module.metadata.controller.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * 动态数据分页查询请求VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "动态数据分页查询请求VO")
public class DynamicDataPageReqVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "页码", example = "1")
    private Integer pageNo = 1;

    @Schema(description = "页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方向", example = "desc")
    private String sortDirection = "desc";

    @Schema(description = "过滤条件")
    private Map<String, Object> filters;

    @Schema(description = "方法编码（可选）")
    private String methodCode;
}
