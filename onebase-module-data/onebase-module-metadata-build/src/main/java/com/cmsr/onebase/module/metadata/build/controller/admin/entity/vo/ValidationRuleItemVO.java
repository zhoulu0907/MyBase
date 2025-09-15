package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 校验规则项 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则项 VO")
@Data
public class ValidationRuleItemVO {

    @Schema(description = "规则ID", example = "4001")
    private String id;

    @Schema(description = "校验类型", example = "FORMAT_VALIDATION")
    private String validationType;

    @Schema(description = "校验表达式", example = "^[a-zA-Z0-9_]{4,20}$")
    private String validationExpression;

    @Schema(description = "错误信息", example = "用户名只能包含字母、数字和下划线，长度4-20位")
    private String errorMessage;

}
