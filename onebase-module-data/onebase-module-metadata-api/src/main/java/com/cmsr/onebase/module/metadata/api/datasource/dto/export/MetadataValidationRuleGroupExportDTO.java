package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 验证规则组导出DTO
 * 对应表: metadata_validation_rule_group
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "验证规则组导出DTO")
@Data
public class MetadataValidationRuleGroupExportDTO {

    @Schema(description = "规则组UUID")
    private String groupUuid;

    @Schema(description = "规则组名称")
    private String rgName;

    @Schema(description = "规则组描述")
    private String rgDesc;

    @Schema(description = "状态：1-激活，0-非激活")
    private Integer rgStatus;

    @Schema(description = "校验方式")
    private String valMethod;

    @Schema(description = "弹窗提示内容")
    private String popPrompt;

    @Schema(description = "弹窗类型")
    private String popType;

    @Schema(description = "校验类型")
    private String validationType;

    @Schema(description = "实体UUID")
    private String entityUuid;
}
