package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 字段选项 批量排序 请求 VO
 */
@Data
public class FieldOptionBatchSortReqVO {
    @Schema(description = "字段UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字段UUID不能为空")
    private String fieldUuid;

    @Schema(description = "排序项")
    private List<SortItemDTO> items;
}


