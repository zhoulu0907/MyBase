package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 校验规则保存 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则保存 Request VO")
@Data
public class ValidationRuleSaveReqVO {

    @Schema(description = "规则ID", example = "4001")
    private String id;

    @Schema(description = "校验名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名格式校验")
    @NotBlank(message = "校验名称不能为空")
    @Size(max = 100, message = "校验名称长度不能超过100个字符")
    private String validationName;

    @Schema(description = "校验编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "username_format_rule")
    @NotBlank(message = "校验编码不能为空")
    @Size(max = 100, message = "校验编码长度不能超过100个字符")
    private String validationCode;

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "字段ID", example = "3001")
    private String fieldId;

    @Schema(description = "校验条件", requiredMode = Schema.RequiredMode.REQUIRED, example = "REGEX_MATCH")
    @NotBlank(message = "校验条件不能为空")
    private String validationCondition;

    @Schema(description = "校验类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "FORMAT_VALIDATION")
    @NotBlank(message = "校验类型不能为空")
    private String validationType;

    @Schema(description = "校验目标对象", requiredMode = Schema.RequiredMode.REQUIRED, example = "FIELD")
    @NotBlank(message = "校验目标对象不能为空")
    private String validationTargetObject;

    @Schema(description = "校验表达式", example = "^[a-zA-Z0-9_]{4,20}$")
    @Size(max = 500, message = "校验表达式长度不能超过500个字符")
    private String validationExpression;

    @Schema(description = "错误信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名只能包含字母、数字和下划线，长度4-20位")
    @NotBlank(message = "错误信息不能为空")
    @Size(max = 200, message = "错误信息长度不能超过200个字符")
    private String errorMessage;

    @Schema(description = "校验时机", example = "CREATE,UPDATE")
    @Size(max = 100, message = "校验时机长度不能超过100个字符")
    private String validationTiming;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12345")
    @NotNull(message = "应用ID不能为空")
    private String appId;

}
