package com.cmsr.onebase.module.metadata.core.semantic.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionNodeTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticCombinatorEnum;

@Schema(description = "语义条件DTO")
@Data
public class SemanticConditionDTO {

    @Schema(description = "组合关系：AND/OR")
    private SemanticCombinatorEnum combinator;

    @Schema(description = "子条件节点(嵌套)")
    private List<SemanticConditionDTO> children;

    @Schema(description = "节点类型：GROUP/CONDITION")
    private SemanticConditionNodeTypeEnum nodeType;

    @Schema(description = "字段Uuid")
    private String fieldUuid;

    @Schema(description = "字段名")
    private String fieldName;

    @Schema(description = "操作符 eq/ne/gt/ge/lt/le/like/in/nin")
    private SemanticOperatorEnum operator;

    @Schema(description = "条件值")
    private List<Object> fieldValue;

    @Schema(description = "主条件/子条件")
    private SemanticConditionTypeEnum conditionType;

}
