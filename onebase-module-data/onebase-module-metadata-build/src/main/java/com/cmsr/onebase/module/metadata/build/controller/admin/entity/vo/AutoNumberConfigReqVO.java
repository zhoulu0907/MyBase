package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 自动编号配置请求VO
 *
 * @author bty418
 * @date 2025-09-17
 */
@Data
@Schema(description = "自动编号配置请求VO")
public class AutoNumberConfigReqVO {

    @Schema(description = "编号方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "NATURAL")
    @NotBlank(message = "编号方式不能为空")
    private String numberMode;

    @Schema(description = "指定位数(2-5位)", example = "4")
    @Min(value = 2, message = "位数不能小于2")
    @Max(value = 5, message = "位数不能大于5")
    private Short digitWidth;

    @Schema(description = "超出位数后继续递增(1-是, 0-否)", example = "1")
    private Integer overflowContinue;

    @Schema(description = "初始值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "初始值不能为空")
    @Min(value = 1, message = "初始值不能小于1")
    private Long initialValue;

    @Schema(description = "重置周期", requiredMode = Schema.RequiredMode.REQUIRED, example = "NEVER")
    @NotBlank(message = "重置周期不能为空")
    private String resetCycle;

    @Schema(description = "下一条记录以修改后的开始值编号(1-是, 0-否)", example = "0")
    private Integer resetOnInitialChange;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;

    @Schema(description = "规则项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("rules")
    @Valid
    @NotNull(message = "规则项列表不能为空")
    private List<AutoNumberRuleItemReqVO> ruleItems;
}