package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 长度校验更新请求VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 长度校验更新请求 VO")
@Data
public class ValidationLengthUpdateReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户信息校验")
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "是否启用不能为空")
    private Integer isEnabled;

    @Schema(description = "最小长度", example = "1")
    private Integer minLength;

    @Schema(description = "最大长度", example = "100")
    private Integer maxLength;

    @Schema(description = "校验前是否去除前后空格", example = "1")
    private Integer trimBefore;

    @Schema(description = "提示信息", example = "字符长度必须在1-100之间")
    private String promptMessage;

    @Schema(description = "运行模式", example = "1")
    private Integer runMode;
}
