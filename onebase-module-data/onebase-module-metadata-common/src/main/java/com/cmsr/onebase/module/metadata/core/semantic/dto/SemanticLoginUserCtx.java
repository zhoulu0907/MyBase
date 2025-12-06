package com.cmsr.onebase.module.metadata.core.semantic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录用户上下文 DTO")
public class SemanticLoginUserCtx {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "用户名")
    private String username;
}
