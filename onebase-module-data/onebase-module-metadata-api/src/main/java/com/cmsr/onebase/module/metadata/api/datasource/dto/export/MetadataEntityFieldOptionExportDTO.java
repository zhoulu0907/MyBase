package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段选项导出DTO
 * 对应表: metadata_entity_field_option
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "字段选项导出DTO")
@Data
public class MetadataEntityFieldOptionExportDTO {

    @Schema(description = "选项UUID")
    private String optionUuid;

    @Schema(description = "关联字段UUID")
    private String fieldUuid;

    @Schema(description = "选项显示名称")
    private String optionLabel;

    @Schema(description = "选项值")
    private String optionValue;

    @Schema(description = "选项排序")
    private Integer optionOrder;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "说明")
    private String description;
}
