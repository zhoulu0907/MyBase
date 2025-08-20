package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 自动编号配置请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "自动编号配置请求VO")
@Data
public class AutoNumberConfigReqVO {

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

    @Schema(description = "自动编号规则项列表（若提供则整体替换）")
    private List<AutoNumberRuleItemReqVO> rules;
}
