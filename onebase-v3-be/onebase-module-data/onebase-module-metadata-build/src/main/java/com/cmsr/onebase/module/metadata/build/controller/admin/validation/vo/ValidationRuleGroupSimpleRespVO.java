package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 校验规则分组分页 精简 Response VO
 * 仅用于列表选择展示，不返回完整规则结构
 *
 * @author bty418
 * @date 2025-09-08
 */
@Data
@Schema(description = "管理后台 - 校验规则分组分页精简 Response VO")
public class ValidationRuleGroupSimpleRespVO {

    @Schema(description = "规则组编号", example = "1024")
    private Long id;

    @Schema(description = "规则组名称", example = "客户信用评级规则")
    private String rgName;

    @Schema(description = "校验类型(派生)")
    private String validationType;

    @Schema(description = "校验数据项(派生): 将规则组逻辑转换为可读表达式列表")
    private List<String> validationItems;

    @Schema(description = "验证失败提示语(派生)")
    private String errorMessage;
}
