package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自动编号配置响应VO
 *
 * @author bty418
 * @date 2025-09-17
 */
@Data
@Schema(description = "自动编号配置响应VO")
public class AutoNumberConfigRespVO {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "编号方式", example = "NATURAL")
    private String numberMode;

    @Schema(description = "指定位数", example = "4")
    private Short digitWidth;

    @Schema(description = "超出位数后继续递增(1-是, 0-否)", example = "1")
    private Integer overflowContinue;

    @Schema(description = "初始值", example = "1")
    private Long initialValue;

    @Schema(description = "重置周期", example = "NEVER")
    private String resetCycle;

    @Schema(description = "下一条记录以修改后的开始值编号(1-是, 0-否)", example = "0")
    private Integer resetOnInitialChange;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "规则项列表")
    @JsonProperty("rules")
    private List<AutoNumberRuleItemRespVO> ruleItems;
}