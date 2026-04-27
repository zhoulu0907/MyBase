package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticSortDirectionEnum;

@Schema(description = "排序规则 DTO")
@Data
public class SemanticSortRuleDTO {
    @Schema(description = "排序字段")
    private String field;

    @Schema(description = "排序方向")
    private SemanticSortDirectionEnum direction;
}
