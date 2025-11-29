package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "记录承载 DTO")
@Data
/**
 * 记录承载 DTO
 *
 * <p>语义解析后统一的载体对象，包含：上下文、实体模型与值模型。</p>
 */
public class SemanticRecordDTO {
    @Schema(description = "上下文")
    private SemanticRecordContextDTO recordContext;

    @Schema(description = "实体模型")
    private SemanticEntitySchemaDTO entitySchema;

    @Schema(description = "值模型")
    private SemanticEntityValueDTO entityValue;
}
