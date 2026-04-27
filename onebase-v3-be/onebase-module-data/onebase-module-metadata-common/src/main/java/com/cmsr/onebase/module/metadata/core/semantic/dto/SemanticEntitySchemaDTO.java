package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "实体模型 DTO")
@Data
public class SemanticEntitySchemaDTO {
    @Schema(description = "实体ID")
    private Long id;

    @Schema(description = "实体UUID")
    private String entityUuid;

    @Schema(description = "实体编码")
    private String code;

    @Schema(description = "实体显示名称")
    private String displayName;

    @Schema(description = "主表名")
    private String tableName;

    @Schema(description = "数据源UUID")
    private String datasourceUuid;

    @Schema(description = "字段模型列表")
    private List<SemanticFieldSchemaDTO> fields;

    @Schema(description = "关联对象模型列表")
    private List<SemanticRelationSchemaDTO> connectors;
}
