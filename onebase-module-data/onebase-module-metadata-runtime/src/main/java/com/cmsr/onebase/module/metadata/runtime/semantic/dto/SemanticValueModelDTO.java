package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "值模型 DTO")
@Data
/**
 * 值模型 DTO
 *
 * <p>承载主实体字段值与连接器值。</p>
 */
public class SemanticValueModelDTO {
    @Schema(description = "主实体字段值")
    private Map<String, SemanticValueDTO> data;

    @Schema(description = "关联对象值")
    private Map<String, SemanticRelationValueDTO> connectors;
}
