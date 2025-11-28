package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticSortDirectionEnum;

@Schema(description = "排序规则 DTO")
@Data
/**
 * 排序规则 DTO
 *
 * <p>表示单字段的排序配置，包含字段名与方向。</p>
 */
public class SemanticSortRuleDTO {
    @Schema(description = "排序字段")
    private String field;

    @Schema(description = "排序方向")
    private SemanticSortDirectionEnum direction;
}
