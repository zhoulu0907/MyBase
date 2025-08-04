package com.cmsr.onebase.module.app.api.appresource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName PageSetDTO
 * @Description TODO
 * @Author mickey
 * @Date 2025/7/31 14:10
 */
@Schema(description = "RPC 服务 - PageSet Response DTO")
@Data
public class PageSetRespDTO {

    @Schema(description = "页面集ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "页面集ID不能为空")
    private Long id;

    @Schema(description = "页面集名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页页面集")
    @NotNull(message = "页面集名称不能为空")
    private String pageSetName;

    @Schema(description = "页面集编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "home_set")
    @NotNull(message = "页面集编码不能为空")
    private String pageSetCode;

    @Schema(description = "页面集显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页")
    @NotNull(message = "页面集显示名称不能为空")
    private String displayName;

    @Schema(description = "页面集描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "这是首页的页面集")
    @NotNull(message = "页面集描述不能为空")
    private String description;

}
