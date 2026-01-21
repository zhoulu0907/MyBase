package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实体关系导出DTO
 * 对应表: metadata_entity_relationship
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "实体关系导出DTO")
@Data
public class MetadataEntityRelationshipExportDTO {

    @Schema(description = "关系UUID")
    private String relationshipUuid;

    @Schema(description = "关系名称")
    private String relationName;

    @Schema(description = "源实体UUID")
    private String sourceEntityUuid;

    @Schema(description = "目标实体UUID")
    private String targetEntityUuid;

    @Schema(description = "关系类型")
    private String relationshipType;

    @Schema(description = "源字段UUID")
    private String sourceFieldUuid;

    @Schema(description = "目标字段UUID")
    private String targetFieldUuid;

    @Schema(description = "选择字段UUID")
    private String selectFieldUuid;

    @Schema(description = "级联操作类型")
    private String cascadeType;

    @Schema(description = "关系描述")
    private String description;
}
