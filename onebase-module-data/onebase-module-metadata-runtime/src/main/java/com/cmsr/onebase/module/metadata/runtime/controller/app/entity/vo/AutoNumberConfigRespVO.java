package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 运行态 - 自动编号配置 Response VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "运行态 - 自动编号配置 Response VO")
@Data
public class AutoNumberConfigRespVO {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置UUID", example = "01onal1s-0000-0000-0000-000000000001")
    private String configUuid;

    @Schema(description = "字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

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
    private List<AutoNumberRuleVO> ruleItems;
}
