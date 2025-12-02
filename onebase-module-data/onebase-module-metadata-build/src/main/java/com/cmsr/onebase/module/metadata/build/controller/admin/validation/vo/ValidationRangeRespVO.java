package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 范围校验响应VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 范围校验响应 VO")
@Data
public class ValidationRangeRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "规则组名称", example = "用户信息校验")
    private String rgName;

    @Schema(description = "字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "实体ID", example = "1")
    private Long entityId;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "规则组ID", example = "1")
    private Long groupId;

    @Schema(description = "是否启用", example = "1")
    private Integer isEnabled;

    @Schema(description = "最小值", example = "0")
    private String minValue;

    @Schema(description = "最大值", example = "100")
    private String maxValue;

    @Schema(description = "是否包含最小值", example = "1")
    private Integer includeMin;

    @Schema(description = "是否包含最大值", example = "1")
    private Integer includeMax;

    @Schema(description = "提示信息", example = "数值必须在0-100之间")
    private String promptMessage;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;
}
