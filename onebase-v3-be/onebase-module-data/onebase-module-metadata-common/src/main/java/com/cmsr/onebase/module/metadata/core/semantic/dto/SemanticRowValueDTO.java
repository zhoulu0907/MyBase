package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "行值 DTO")
@Data
public class SemanticRowValueDTO {
    @Schema(description = "行ID")
    private Object id;

    @Schema(description = "软删除标记")
    private Boolean deleted;

    @Schema(description = "字段值Map")
    private Map<String, SemanticFieldValueDTO<Object>> fields;
}
