package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字段选项 保存/更新 请求 VO
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
public class FieldOptionSaveReqVO {

    @Schema(description = "选项ID", example = "101")
    private String id;

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    @NotNull
    private Long fieldId;

    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String optionLabel;

    @Schema(description = "选项值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String optionValue;

    @Schema(description = "排序", example = "1")
    private Integer optionOrder;

    @Schema(description = "是否启用：0-是，1-否", example = "0")
    private Integer isEnabled;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long applicationId;
}


