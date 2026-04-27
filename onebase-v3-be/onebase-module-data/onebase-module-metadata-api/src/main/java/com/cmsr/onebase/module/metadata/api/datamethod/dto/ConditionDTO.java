package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 校验规则定义 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "校验规则定义 VO")
@Data
public class ConditionDTO {


    @Schema(description = "关联的实体字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "条件操作符，当logic_type=CONDITION时使用", example = ">=")
    private String operator;

    @Schema(description = "条件值")
    private List<String> fieldValue;

    @Schema(description = "主条件/子条件 mainCondition/subCondition")
    private String conditionType;

}
