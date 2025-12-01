package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;

@Schema(description = "关联对象模型 DTO")
@Data
public class SemanticRelationSchemaDTO {
    @Schema(description = "关联名称")
    private String relationName;

    @Schema(description = "关联类型")
    private SemanticConnectorTypeEnum type;

    @Schema(description = "基数")
    private SemanticConnectorCardinalityEnum cardinality;

    @Schema(description = "关系类型原始值")
    private String relationshipType;

    @Schema(description = "源实体ID")
    private Long sourceEntityId;

    @Schema(description = "目标实体ID")
    private Long targetEntityId;

    @Schema(description = "目标实体编码")
    private String targetEntityCode;

    @Schema(description = "目标实体显示名称")
    private String targetEntityDisplayName;

    @Schema(description = "目标实体表名")
    private String targetEntityTableName;

    @Schema(description = "源字段ID")
    private Long sourceKeyFieldId;

    @Schema(description = "目标字段ID")
    private Long targetKeyFieldId;

    @Schema(description = "关系属性字段模型")
    private List<SemanticFieldSchemaDTO> relationAttributes;
}
