package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.ConnectorTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.ConnectorCardinalityEnum;

@Schema(description = "关联对象模型 DTO")
@Data
public class RelationSchemaDTO {
    @Schema(description = "关联名称")
    private String name;

    @Schema(description = "关联类型")
    private ConnectorTypeEnum type;

    @Schema(description = "基数")
    private ConnectorCardinalityEnum cardinality;

    @Schema(description = "关系类型原始值")
    private String relationshipType;

    @Schema(description = "源实体ID")
    private Long sourceEntityId;

    @Schema(description = "目标实体ID")
    private Long targetEntityId;

    @Schema(description = "源字段ID")
    private Long sourceKeyFieldId;

    @Schema(description = "目标字段ID")
    private Long targetKeyFieldId;

    @Schema(description = "关系属性字段模型")
    private List<FieldSchemaDTO> relationAttributes;
}
