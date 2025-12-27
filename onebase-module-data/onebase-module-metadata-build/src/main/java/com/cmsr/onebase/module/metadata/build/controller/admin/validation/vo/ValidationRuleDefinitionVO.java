package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

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
public class ValidationRuleDefinitionVO {

    @Schema(description = "规则编号", example = "101")
    private Long id;

    @Schema(description = "所属规则组ID", example = "1")
    private Long groupId;

    @Schema(description = "父规则ID，用于层级关系；顶级规则为NULL", example = "101")
    private Long parentRuleId;

    @Schema(description = "关联的业务实体ID", example = "1")
    private Long entityId;

    @Schema(description = "关联的业务实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "关联的实体字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "关联的实体字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "逻辑类型：LOGIC（逻辑操作符）/CONDITION（条件判断）", example = "LOGIC")
    private String logicType;

    @Schema(description = "条件操作符，当logic_type=CONDITION时使用", example = ">=")
    private String operator;

    @Schema(description = "逻辑操作符，当logic_type=LOGIC时使用", example = "AND")
    private String logicOperator;

    @Schema(description = "条件字段编码", example = "ANNUAL_SPEND")
    private String fieldCode;

    @Schema(description = "值类型：VARIABLE（变量）/STATIC（静态值）/FORMULA（公式）", example = "STATIC")
    private String valueType;

    @Schema(description = "条件值引用（单值条件或范围表达式的第一个）", example = "100000")
    private String fieldValue;

    @Schema(description = "条件值引用2（单值条件或范围表达式的第二个）", example = "200000")
    private String fieldValue2;

    @Schema(description = "状态：ACTIVE/INACTIVE", example = "ACTIVE")
    private String status;

}
