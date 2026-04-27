package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 自动编号规则项请求VO
 *
 * @author bty418
 * @date 2025-09-17
 */
@Data
@Schema(description = "自动编号规则项请求VO")
public class AutoNumberRuleItemReqVO {

    @Schema(description = "规则项ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "规则项类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "SEQUENCE")
    @NotBlank(message = "规则项类型不能为空")
    private String itemType;

    @Schema(description = "排序序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序序号不能为空")
    private Integer itemOrder;

    @Schema(description = "格式化规则(日期时间类型使用)", example = "yyyyMMdd")
    private String format;

    @Schema(description = "固定文本值(固定文本类型使用)", example = "ORDER")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String textValue;

    @Schema(description = "引用字段ID(字段引用类型使用)", example = "123")
    private Long refFieldId;

    @Schema(description = "引用字段UUID(字段引用类型使用)", example = "01onal1s-0000-0000-0000-000000000006")
    private String refFieldUuid;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;
}