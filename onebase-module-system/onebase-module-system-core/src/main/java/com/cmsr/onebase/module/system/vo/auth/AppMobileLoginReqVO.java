package com.cmsr.onebase.module.system.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 应用ID")
@Data
public class AppMobileLoginReqVO extends MobileLoginReqVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "应用ID不能为空")
    private Long appId;
}