package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;

@Schema(description = "关联对象模型 DTO")
@Data
public class SemanticRelationSchemaDTO {
    @Schema(description = "关联名称")
    private String relationName;

    @Schema(description = "基数")
    private SemanticConnectorCardinalityEnum cardinality;

    @Schema(description = "关系类型")
    private RelationshipTypeEnum relationshipType;

    @Schema(description = "源实体UUID")
    private String sourceEntityUuid;

    @Schema(description = "目标实体UUID")
    private String targetEntityUuid;

    @Schema(description = "目标实体编码")
    private String targetEntityCode;

    @Schema(description = "目标实体显示名称")
    private String targetEntityDisplayName;

    @Schema(description = "目标实体表名")
    private String targetEntityTableName;

    @Schema(description = "源字段UUID")
    private String sourceKeyFieldUuid;

    @Schema(description = "目标字段UUID")
    private String targetKeyFieldUuid;

    @Schema(description = "选择字段UUID")
    private String selectFieldUuid;

    @Schema(description = "关系属性字段模型")
    private List<SemanticFieldSchemaDTO> relationAttributes;
}
