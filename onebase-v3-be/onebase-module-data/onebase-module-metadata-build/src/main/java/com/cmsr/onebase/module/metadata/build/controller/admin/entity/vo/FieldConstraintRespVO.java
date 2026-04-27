package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段约束 响应 VO
 */
@Data
public class FieldConstraintRespVO {
    @Schema(description = "长度范围-启用：1-启用，0-禁用")
    private Integer lengthEnabled;
    @Schema(description = "最小长度")
    private Integer minLength;
    @Schema(description = "最大长度")
    private Integer maxLength;
    @Schema(description = "长度提示语")
    private String lengthPrompt;
    @Schema(description = "正则-启用：1-启用，0-禁用")
    private Integer regexEnabled;
    @Schema(description = "正则表达式")
    private String regexPattern;
    @Schema(description = "正则提示语")
    private String regexPrompt;
}


