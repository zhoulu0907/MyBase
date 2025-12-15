package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自动编号配置响应VO
 * <p>
 * 采用统一规则项列表，SEQUENCE配置作为规则项之一与TEXT/DATE等平级排序
 *
 * @author bty418
 * @date 2025-09-17
 */
@Data
@Schema(description = "自动编号配置响应VO")
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

    @Schema(description = "规则项列表（包含TEXT/DATE/SEQUENCE/FIELD_REF，按itemOrder排序）")
    @JsonProperty("rules")
    private List<AutoNumberRuleVO> ruleItems;
}