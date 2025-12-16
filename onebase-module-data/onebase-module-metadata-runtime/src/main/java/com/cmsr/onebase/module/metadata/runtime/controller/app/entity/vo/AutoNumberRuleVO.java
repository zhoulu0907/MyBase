package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 运行态 - 自动编号规则项 VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "运行态 - 自动编号规则项 VO")
@Data
public class AutoNumberRuleVO {

    @Schema(description = "规则项ID", example = "1")
    private Long id;

    @Schema(description = "规则项UUID", example = "01onal1s-0000-0000-0000-000000000001")
    private String uuid;

    @Schema(description = "规则项类型: TEXT-固定文本/DATE-日期时间/SEQUENCE-序号/FIELD_REF-字段引用", example = "SEQUENCE")
    private String itemType;

    @Schema(description = "排序序号", example = "1")
    private Integer itemOrder;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    @Schema(description = "固定文本值（TEXT类型使用）", example = "ORD")
    private String textValue;

    @Schema(description = "日期格式化规则（DATE类型使用）", example = "yyyyMMdd")
    private String format;

    @Schema(description = "引用字段UUID（FIELD_REF类型使用）", example = "01onal1s-0000-0000-0000-000000000006")
    private String refFieldUuid;

    @Schema(description = "编号模式（SEQUENCE类型使用）: NATURAL-自然数/FIXED_DIGIT-指定位数", example = "FIXED_DIGIT")
    private String numberMode;

    @Schema(description = "指定位数（SEQUENCE类型使用，2-5位）", example = "4")
    private Short digitWidth;

    @Schema(description = "超出位数后继续递增（SEQUENCE类型使用，1-是, 0-否）", example = "1")
    private Integer overflowContinue;

    @Schema(description = "初始值（SEQUENCE类型使用）", example = "1")
    private Long initialValue;

    @Schema(description = "重置周期（SEQUENCE类型使用）: NEVER-不重置/DAILY-每日/MONTHLY-每月/YEARLY-每年", example = "DAILY")
    private String resetCycle;

    @Schema(description = "下一条记录以修改后的开始值编号（SEQUENCE类型使用，1-是, 0-否）", example = "0")
    private Integer resetOnInitialChange;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
