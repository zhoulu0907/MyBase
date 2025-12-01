package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticConnectorCardinalityEnum;
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


       /**
     * 选择字段id
     * <p>
     * 该字段主要用于当关系类型relationshipType为数据选择时：
     * - targetFieldId存的是关联表的主键字段id
     * - selectFieldId存的是关联表中被选择的字段id（用于展示给用户的字段）
     * <p>
     * 用户动态建的实体中存的是关联表主键id，通过id查到一条或多条数据后，
     * 需要把selectFieldId对应的字段值取出来展示给用户。因此需要存储该字段。
     */
    @Schema(description = "选择字段id")
    private String selectFieldId;

    @Schema(description = "关系属性字段模型")
    private List<SemanticFieldSchemaDTO> relationAttributes;
}
