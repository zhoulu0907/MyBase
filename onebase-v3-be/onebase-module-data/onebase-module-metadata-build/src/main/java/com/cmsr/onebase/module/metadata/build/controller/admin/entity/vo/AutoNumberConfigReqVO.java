package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 自动编号配置请求VO
 * <p>
 * 采用统一规则项列表，SEQUENCE配置作为规则项之一与TEXT/DATE等平级排序
 *
 * @author bty418
 * @date 2025-09-17
 */
@Data
@Schema(description = "自动编号配置请求VO")
public class AutoNumberConfigReqVO {

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;

    @Schema(description = "规则项列表（包含TEXT/DATE/SEQUENCE/FIELD_REF，必须且只能有一个SEQUENCE类型）", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("rules")
    @Valid
    @NotNull(message = "规则项列表不能为空")
    private List<AutoNumberRuleVO> ruleItems;
}