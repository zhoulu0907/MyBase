package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "字段值 DTO")
@Data
/**
 * 字段值 DTO
 *
 * <p>封装字段原始值与类型代码。</p>
 */
public class SemanticValueDTO {
    @Schema(description = "字段原始值")
    private Object value;

    @Schema(description = "值类型代码，例如 STRING/NUMBER/DATE/BOOLEAN")
    private String type;
}
