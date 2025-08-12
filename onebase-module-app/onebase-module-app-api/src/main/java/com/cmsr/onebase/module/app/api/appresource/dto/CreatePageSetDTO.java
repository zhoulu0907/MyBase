package com.cmsr.onebase.module.app.api.appresource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "RPC 服务 - CreatePageSet DTO")
@Data
public class CreatePageSetDTO {

    @Schema(description = "页面集名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页页面集")
    @NotNull(message = "页面集名称不能为空")
    private String pageSetName;

    @Schema(description = "页面集显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页")
    @NotNull(message = "页面集显示名称不能为空")
    private String displayName;

    @Schema(description = "菜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxxxx")
    @NotNull(message = "菜单编码不能为空")
    private String menuCode;

    @Schema(description = "页面集描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "这是首页的页面集")
    private String description;
}
