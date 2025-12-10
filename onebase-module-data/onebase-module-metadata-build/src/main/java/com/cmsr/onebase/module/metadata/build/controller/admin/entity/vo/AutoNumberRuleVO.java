package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自动编号统一规则项VO
 * <p>
 * 统一承载所有类型规则项（TEXT/DATE/SEQUENCE/FIELD_REF）
 * <ul>
 *   <li>SEQUENCE 类型：id 为 configId</li>
 *   <li>其他类型：id 为 ruleItemId</li>
 * </ul>
 *
 * @author bty418
 * @date 2025-12-09
 */
@Data
@Schema(description = "自动编号统一规则项")
public class AutoNumberRuleVO {

    @Schema(description = "规则项ID（SEQUENCE类型为configId，其他类型为ruleItemId）", example = "1")
    private Long id;

    @Schema(description = "规则项UUID（SEQUENCE类型为configUuid，其他类型为ruleItemUuid）", example = "01onal1s-0000-0000-0000-000000000001")
    private String uuid;

    @Schema(description = "规则项类型: TEXT-固定文本/DATE-日期时间/SEQUENCE-序号/FIELD_REF-字段引用", requiredMode = Schema.RequiredMode.REQUIRED, example = "SEQUENCE")
    @NotBlank(message = "规则项类型不能为空")
    private String itemType;

    @Schema(description = "排序序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序序号不能为空")
    private Integer itemOrder;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    // ==================== TEXT 类型专属 ====================

    @Schema(description = "固定文本值（TEXT类型使用）", example = "ORD")
    private String textValue;

    // ==================== DATE 类型专属 ====================

    @Schema(description = "日期格式化规则（DATE类型使用），如 yyyyMMdd、yyyy-MM-dd", example = "yyyyMMdd")
    private String format;

    // ==================== FIELD_REF 类型专属 ====================

    @Schema(description = "引用字段UUID（FIELD_REF类型使用）", example = "01onal1s-0000-0000-0000-000000000006")
    private String refFieldUuid;

    // ==================== SEQUENCE 类型专属 ====================

    @Schema(description = "编号模式（SEQUENCE类型使用）: NATURAL-自然数/FIXED_DIGIT-指定位数", example = "FIXED_DIGIT")
    private String numberMode;

    @Schema(description = "指定位数（SEQUENCE类型使用，2-5位）", example = "4")
    @Min(value = 2, message = "位数不能小于2")
    @Max(value = 5, message = "位数不能大于5")
    private Short digitWidth;

    @Schema(description = "超出位数后继续递增（SEQUENCE类型使用，1-是, 0-否）", example = "1")
    private Integer overflowContinue;

    @Schema(description = "初始值（SEQUENCE类型使用）", example = "1")
    @Min(value = 1, message = "初始值不能小于1")
    private Long initialValue;

    @Schema(description = "重置周期（SEQUENCE类型使用）: NEVER-不重置/DAILY-每日/MONTHLY-每月/YEARLY-每年", example = "DAILY")
    private String resetCycle;

    @Schema(description = "下一条记录以修改后的开始值编号（SEQUENCE类型使用，1-是, 0-否）", example = "0")
    private Integer resetOnInitialChange;

    // ==================== 时间字段（响应时返回） ====================

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
