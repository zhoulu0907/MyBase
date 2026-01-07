package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "字段选项 DTO")
public class SemanticFieldOptionDTO {

    @Schema(description = "选项ID")
    private Long id;

    @Schema(description = "选项UUID")
    private String optionUuid;

    @Schema(description = "关联字段UUID")
    private String fieldUuid;

    @Schema(description = "选项显示名称")
    private String optionLabel;

    @Schema(description = "选项值")
    private String optionValue;

    @Schema(description = "选项排序")
    private Integer optionOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "说明")
    private String description;
}

