package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 范围校验更新请求VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 范围校验更新请求 VO")
@Data
public class ValidationRangeUpdateReqVO {

    @Schema(description = "规则组ID（前端传入的id即校验规则组ID，用于定位唯一该类型校验记录）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "规则组ID不能为空")
    private Long id;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户信息校验")
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "是否启用不能为空")
    private Integer isEnabled;

    @Schema(description = "最小值", example = "0")
    private BigDecimal minValue;

    @Schema(description = "最大值", example = "100")
    private BigDecimal maxValue;

    @Schema(description = "是否包含最小值", example = "1")
    private Integer includeMin;

    @Schema(description = "是否包含最大值", example = "1")
    private Integer includeMax;

    @Schema(description = "提示信息", example = "数值必须在0-100之间")
    private String promptMessage;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;
}
