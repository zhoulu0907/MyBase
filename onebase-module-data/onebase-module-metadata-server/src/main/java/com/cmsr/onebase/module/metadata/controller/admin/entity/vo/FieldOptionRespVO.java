package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段选项 响应 VO
 */
@Data
public class FieldOptionRespVO {
    @Schema(description = "选项ID")
    private String id;
    @Schema(description = "字段ID")
    private Long fieldId;
    @Schema(description = "显示名称")
    private String optionLabel;
    @Schema(description = "选项值")
    private String optionValue;
    @Schema(description = "排序")
    private Integer optionOrder;
    @Schema(description = "是否启用：0-是，1-否")
    private Integer isEnabled;
    @Schema(description = "描述")
    private String description;
}


