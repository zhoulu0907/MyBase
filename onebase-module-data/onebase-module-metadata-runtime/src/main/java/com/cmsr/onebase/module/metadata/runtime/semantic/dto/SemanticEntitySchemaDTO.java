package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import com.mybatisflex.annotation.Column;

@Schema(description = "实体模型 DTO")
@Data
/**
 * 实体模型 DTO
 *
 * <p>描述记录承载中的主实体信息，包括实体ID/编码/显示名、
 * 主表名以及字段与连接器模型列表。</p>
 */
public class SemanticEntitySchemaDTO {
    @Schema(description = "实体ID")
    private Long id;

    @Schema(description = "实体编码")
    private String code;

    @Schema(description = "实体显示名称")
    private String displayName;

    @Schema(description = "主表名")
    private String tableName;

    @Schema(description = "数据源ID")
    private Long datasourceId;

    @Schema(description = "字段模型列表")
    private List<SemanticFieldSchemaDTO> fields;

    @Schema(description = "关联对象模型列表")
    private List<SemanticRelationSchemaDTO> connectors;
}
