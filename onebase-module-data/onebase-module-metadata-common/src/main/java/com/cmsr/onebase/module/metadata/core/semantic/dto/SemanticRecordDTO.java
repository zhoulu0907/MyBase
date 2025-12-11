package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "记录承载 DTO")
@Data
public class SemanticRecordDTO {
    @Schema(description = "上下文")
    private SemanticRecordContextDTO recordContext;

    @Schema(description = "实体模型")
    private SemanticEntitySchemaDTO entitySchema;

    @Schema(description = "值模型")
    private SemanticEntityValueDTO entityValue;

    @Schema(description = "结果值模型")
    private SemanticEntityValueDTO resultValue;
}
