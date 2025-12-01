package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 唯一性校验 更新 请求VO
 *
 * @author bty418
 * @date 2025-08-28
 */
@Data
public class ValidationUniqueUpdateReqVO {

    @Schema(description = "规则组ID（前端传入的id即校验规则组ID，用于定位唯一该类型校验记录）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则组ID不能为空")
    private Long id;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户信息校验")
    @NotBlank(message = "规则组名称不能为空")
    private String rgName;

    @Schema(description = "是否启用(0/1)")
    private Integer isEnabled;

    @Schema(description = "唯一性作用域")
    private String uniqueScope;

    @Schema(description = "忽略空值(0/1)")
    private Integer ignoreNull;

    @Schema(description = "区分大小写(0/1)")
    private Integer caseSensitive;

    @Schema(description = "提示信息")
    private String promptMessage;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;
}
