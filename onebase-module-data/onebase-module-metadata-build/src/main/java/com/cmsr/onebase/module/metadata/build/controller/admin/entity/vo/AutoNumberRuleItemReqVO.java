package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 自动编号规则项请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "自动编号规则项请求VO")
@Data
public class AutoNumberRuleItemReqVO {

    @Schema(description = "规则项ID（更新时提供）", example = "1001")
    private Long id;

    @Schema(description = "规则项类型：TEXT（固定文本）/DATE（日期）/TIME（时间）/SEQUENCE（序号）/FIELD_REF（字段引用）", example = "TEXT")
    private String itemType;

    @Schema(description = "排序顺序", example = "1")
    private Integer itemOrder;

    @Schema(description = "格式化字符串（如日期格式：yyyyMMdd）", example = "yyyyMMdd")
    private String format;

    @Schema(description = "固定文本值（当类型为TEXT时）", example = "PRD")
    private String textValue;

    @Schema(description = "引用字段ID（当类型为FIELD_REF时）", example = "2001")
    private Long refFieldId;

    @Schema(description = "是否启用：0-启用，1-禁用", example = "0")
    private Integer isEnabled;
}
