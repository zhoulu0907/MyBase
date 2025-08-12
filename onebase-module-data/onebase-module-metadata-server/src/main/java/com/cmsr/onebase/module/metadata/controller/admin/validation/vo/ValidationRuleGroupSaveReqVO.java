package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 校验规则分组新增/修改 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则分组新增/修改 Request VO")
@Data
public class ValidationRuleGroupSaveReqVO {

    @Schema(description = "规则组编号", example = "1024")
    private Long id;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "客户信用评级规则")
    @NotBlank(message = "规则组名称不能为空")
    @Size(max = 100, message = "规则组名称长度不能超过100个字符")
    private String rgName;

    @Schema(description = "规则组描述", example = "用于识别高价值潜力的客户")
    @Size(max = 500, message = "规则组描述长度不能超过500个字符")
    private String rgDesc;

    @Schema(description = "规则组状态", example = "ACTIVE")
    private String rgStatus;

    @Schema(description = "规则定义列表")
    @Valid
    private List<ValidationRuleDefinitionVO> ruleDefinitions;

}
