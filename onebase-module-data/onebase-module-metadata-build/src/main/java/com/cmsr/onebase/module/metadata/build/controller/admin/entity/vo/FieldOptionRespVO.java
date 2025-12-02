package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段选项 响应 VO
 */
@Data
public class FieldOptionRespVO {
    @Schema(description = "选项ID")
    private String id;

    @Schema(description = "选项UUID", example = "01onal1s-0000-0000-0000-000000000004")
    private String optionUuid;

    @Schema(description = "字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "显示名称")
    private String optionLabel;
    @Schema(description = "选项值")
    private String optionValue;
    @Schema(description = "排序")
    private Integer optionOrder;
    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;
    @Schema(description = "描述")
    private String description;
}


