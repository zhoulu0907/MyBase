package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 校验规则创建/修改 Request VO")
@Data
public class ValidationRuleSaveReqVO {

    @Schema(description = "规则编号", example = "1024")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名唯一性校验")
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 128, message = "规则名称长度不能超过128个字符")
    private String validationName;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "username_unique")
    @NotBlank(message = "规则编码不能为空")
    @Size(max = 128, message = "规则编码长度不能超过128个字符")
    private String validationCode;

    @Schema(description = "关联实体ID", example = "1")
    private Long entityId;

    @Schema(description = "关联字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "校验条件", requiredMode = Schema.RequiredMode.REQUIRED, example = "IS NOT NULL")
    @NotBlank(message = "校验条件不能为空")
    @Size(max = 64, message = "校验条件长度不能超过64个字符")
    private String validationCondition;

    @Schema(description = "校验类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "字段")
    @NotBlank(message = "校验类型不能为空")
    @Size(max = 64, message = "校验类型长度不能超过64个字符")
    private String validationType;

    @Schema(description = "校验比较对象", requiredMode = Schema.RequiredMode.REQUIRED, example = "username")
    @NotBlank(message = "校验比较对象不能为空")
    @Size(max = 64, message = "校验比较对象长度不能超过64个字符")
    private String validationTargetObject;

    @Schema(description = "校验表达式", example = "username IS NOT NULL")
    private String validationExpression;

    @Schema(description = "错误提示信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名不能为空")
    @NotBlank(message = "错误提示信息不能为空")
    @Size(max = 512, message = "错误提示信息长度不能超过512个字符")
    private String errorMessage;

    @Schema(description = "校验时机", requiredMode = Schema.RequiredMode.REQUIRED, example = "新增时")
    @NotBlank(message = "校验时机不能为空")
    @Size(max = 64, message = "校验时机长度不能超过64个字符")
    private String validationTiming;

    @Schema(description = "执行顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "版本锁标识", example = "0")
    private Integer lockVersion;

}
