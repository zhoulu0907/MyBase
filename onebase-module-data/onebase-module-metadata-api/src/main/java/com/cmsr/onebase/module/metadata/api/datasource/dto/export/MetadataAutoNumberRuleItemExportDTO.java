package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 自动编号规则项导出DTO
 * 对应表: metadata_auto_number_rule_item
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "自动编号规则项导出DTO")
@Data
public class MetadataAutoNumberRuleItemExportDTO {

    @Schema(description = "规则项UUID")
    private String ruleItemUuid;

    @Schema(description = "配置UUID")
    private String configUuid;

    @Schema(description = "项类型")
    private String itemType;

    @Schema(description = "项顺序")
    private Integer itemOrder;

    @Schema(description = "格式")
    private String format;

    @Schema(description = "文本值")
    private String textValue;

    @Schema(description = "引用字段UUID")
    private String refFieldUuid;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;
}
