package com.cmsr.onebase.module.app.core.dto.resource;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private Long id;

    @Schema(description = "菜单UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String menuUuid;

    @Schema(description = "页面集名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页页面集")
    private String pageSetName;

    @Schema(description = "页面集编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "home_set")
    private String pageSetCode;

    @Schema(description = "页面集显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页")
    private String displayName;

    @Schema(description = "页面集描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "这是首页的页面集")
    private String description;

}
