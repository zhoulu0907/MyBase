package com.cmsr.onebase.module.metadata.core.semantic.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 语义条件DTO
 *
 * <p>用于表示查询条件，包含字段ID、操作符、值和条件类型。</p>
 */
@Schema(description = "语义条件DTO")
@Data
public class SemanticConditionDTO {
    
    @Schema(description = "关联的实体字段Uuid", example = "")
    private String fieldUuid;

    @Schema(description = "关联的实体字段fieldName", example = "")
    private String fieldName;

    @Schema(description = "条件操作符，当logic_type=CONDITION时使用", example = ">=")
    private String operator;

    @Schema(description = "条件值")
    private List<Object> fieldValue;

    @Schema(description = "主条件/子条件 mainCondition/subCondition")
    private String conditionType;
}
