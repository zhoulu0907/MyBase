package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 格式校验 创建/保存 请求VO
 *
 * @author bty418
 * @date 2025-08-28
 */
@Data
public class ValidationFormatSaveReqVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "字段ID不能为空")
    private Long fieldId;

    @Schema(description = "是否启用(0/1)")
    private Integer isEnabled;

    @Schema(description = "格式类型")
    private String formatType;

    @Schema(description = "正则表达式")
    private String regex;

    @Schema(description = "提示信息")
    private String promptMessage;

    @Schema(description = "运行模式")
    private Integer runMode;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;
}
