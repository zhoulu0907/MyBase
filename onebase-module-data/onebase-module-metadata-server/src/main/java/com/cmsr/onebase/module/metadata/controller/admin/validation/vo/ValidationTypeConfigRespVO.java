package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 校验类型配置 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验类型配置 Response VO")
@Data
public class ValidationTypeConfigRespVO {

    @Schema(description = "校验类型编码", example = "FORMAT_VALIDATION")
    private String validationType;

    @Schema(description = "显示名称", example = "格式验证")
    private String displayName;

    @Schema(description = "描述信息", example = "验证字段格式（正则表达式）")
    private String description;

    @Schema(description = "支持的条件列表", example = "[\"REGEX_MATCH\", \"EMAIL_FORMAT\", \"PHONE_FORMAT\", \"ID_CARD_FORMAT\"]")
    private List<String> supportedConditions;

} 