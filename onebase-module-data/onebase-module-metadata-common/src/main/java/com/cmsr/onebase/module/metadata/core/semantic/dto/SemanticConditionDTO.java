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

    public boolean hasCondition() {
        if (nodeType == SemanticConditionNodeTypeEnum.GROUP) {
            if (children == null || children.isEmpty()) { return false; }
            for (SemanticConditionDTO c : children) { if (c != null && c.hasCondition()) { return true; } }
            return false;
        }
        boolean hasField = (fieldUuid != null && !fieldUuid.trim().isEmpty()) || (fieldName != null && !fieldName.trim().isEmpty());
        boolean hasOp = operator != null;
        boolean hasVal = hasNonBlankValue(fieldValue);
        return hasField && hasOp && hasVal;
    }

    public static boolean hasCondition(SemanticConditionDTO cond) { return cond != null && cond.hasCondition(); }

    private boolean hasNonBlankValue(List<Object> list) {
        if (list == null || list.isEmpty()) { return false; }
        for (Object v : list) {
            if (v == null) { continue; }
            if (v instanceof String s) { if (!s.trim().isEmpty()) { return true; } }
            else { return true; }
        }
        return false;
    }
}
