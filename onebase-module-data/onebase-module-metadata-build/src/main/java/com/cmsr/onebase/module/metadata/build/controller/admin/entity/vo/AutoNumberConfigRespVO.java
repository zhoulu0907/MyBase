package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自动编号配置响应VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "自动编号配置响应VO")
@Data
public class AutoNumberConfigRespVO {

    @Schema(description = "配置ID", example = "1001")
    private Long id;

    @Schema(description = "字段ID", example = "2001")
    private Long fieldId;

    @Schema(description = "是否启用：0-启用，1-禁用", example = "0")
    private Integer isEnabled;

    @Schema(description = "编号模式：NATURAL（自然递增）/FIXED_DIGITS（固定位数）", example = "FIXED_DIGITS")
    private String numberMode;

    @Schema(description = "固定位数（当模式为FIXED_DIGITS时有效）", example = "6")
    private Short digitWidth;

    @Schema(description = "溢出时继续：0-是，1-否", example = "0")
    private Integer overflowContinue;

    @Schema(description = "初始值", example = "1")
    private Long initialValue;

    @Schema(description = "重置周期：NONE（不重置）/DAILY（每日）/MONTHLY（每月）/YEARLY（每年）", example = "MONTHLY")
    private String resetCycle;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", example = "12345")
    private Long appId;

    @Schema(description = "创建时间", example = "2025-01-25T10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-25T10:30:00")
    private LocalDateTime updateTime;

    @Schema(description = "自动编号规则项列表")
    private List<AutoNumberRuleItemRespVO> rules;
}
