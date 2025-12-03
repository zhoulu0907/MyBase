package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字段约束 保存/更新 请求 VO
 */
@Data
public class FieldConstraintSaveReqVO {
    @Schema(description = "字段UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String fieldUuid;

    @Schema(description = "约束类型：LENGTH_RANGE/REGEX", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String constraintType;

    @Schema(description = "最小长度（LENGTH_RANGE有效）")
    private Integer minLength;

    @Schema(description = "最大长度（LENGTH_RANGE有效）")
    private Integer maxLength;

    @Schema(description = "正则表达式（REGEX有效）")
    private String regexPattern;

    @Schema(description = "提示信息")
    private String promptMessage;

    @Schema(description = "是否启用：0-是，1-否")
    private Integer isEnabled;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long applicationId;
}


