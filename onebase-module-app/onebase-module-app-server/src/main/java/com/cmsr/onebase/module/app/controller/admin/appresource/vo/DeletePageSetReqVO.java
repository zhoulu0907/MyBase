package com.cmsr.onebase.module.app.controller.admin.appresource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DeletePageSetReqVO {

    @Schema(description = "菜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单编码不能为空")
    private String menuCode;
}
