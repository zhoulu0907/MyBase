package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 校验规则 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则 Response VO")
@Data
public class ValidationRuleRespVO {

    @Schema(description = "规则ID", example = "4001")
    private Long id;

    @Schema(description = "校验名称", example = "用户名格式校验")
    private String validationName;

    @Schema(description = "校验编码", example = "username_format_rule")
    private String validationCode;

    @Schema(description = "实体ID", example = "2001")
    private Long entityId;

    @Schema(description = "实体名称", example = "用户信息")
    private String entityName;

    @Schema(description = "字段ID", example = "3001")
    private Long fieldId;

    @Schema(description = "字段名称", example = "username")
    private String fieldName;

    @Schema(description = "校验条件", example = "REGEX_MATCH")
    private String validationCondition;

    @Schema(description = "校验类型", example = "FORMAT_VALIDATION")
    private String validationType;

    @Schema(description = "校验目标对象", example = "FIELD")
    private String validationTargetObject;

    @Schema(description = "校验表达式", example = "^[a-zA-Z0-9_]{4,20}$")
    private String validationExpression;

    @Schema(description = "错误信息", example = "用户名只能包含字母、数字和下划线，长度4-20位")
    private String errorMessage;

    @Schema(description = "校验时机", example = "CREATE,UPDATE")
    private String validationTiming;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

    @Schema(description = "创建时间", example = "2025-07-28T10:30:00")
    private LocalDateTime createTime;

} 