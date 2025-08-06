package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoadPageSetReqVO {
    @Schema(description = "页面集编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面集编码不能为空")
    private String pageSetCode;

}
