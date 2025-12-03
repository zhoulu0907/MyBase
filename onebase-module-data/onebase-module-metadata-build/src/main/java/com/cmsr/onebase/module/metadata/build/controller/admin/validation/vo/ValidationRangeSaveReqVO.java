package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 范围校验 创建/保存 请求VO
 *
 * @author bty418
 * @date 2025-08-28
 */
@Data
public class ValidationRangeSaveReqVO {

    @Schema(description = "字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "字段ID（兼容旧版，与fieldUuid二选一）", example = "164329365983232003")
    private String fieldId;

    @Schema(description = "是否启用(0/1)", example = "1")
    private Integer isEnabled;

    @Schema(description = "范围类型：NUMBER/DATE", example = "NUMBER")
    private String rangeType;

    @Schema(description = "最小值（数值类型）")
    private BigDecimal minValue;

    @Schema(description = "最大值（数值类型）")
    private BigDecimal maxValue;

    @Schema(description = "最小日期（日期类型）")
    private LocalDateTime minDate;

    @Schema(description = "最大日期（日期类型）")
    private LocalDateTime maxDate;

    @Schema(description = "是否包含最小边界(0/1)", example = "1")
    private Integer includeMin;

    @Schema(description = "是否包含最大边界(0/1)", example = "1")
    private Integer includeMax;

    @Schema(description = "提示信息")
    private String promptMessage;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;
}
