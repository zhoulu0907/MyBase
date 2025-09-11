package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 字段选项 批量排序 请求 VO
 */
@Data
public class FieldOptionBatchSortReqVO {
    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long fieldId;

    @Schema(description = "排序项")
    private List<SortItemDTO> items;
}


