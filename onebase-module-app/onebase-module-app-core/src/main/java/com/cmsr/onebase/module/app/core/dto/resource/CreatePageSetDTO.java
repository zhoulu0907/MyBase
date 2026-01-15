package com.cmsr.onebase.module.app.core.dto.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "RPC 服务 - CreatePageSet DTO")
@Data
public class CreatePageSetDTO {

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxxxx")
    private Long applicationId;

    @Schema(description = "页面集名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页页面集")
    @NotNull(message = "页面集名称不能为空")
    private String pageSetName;

    @Schema(description = "页面集显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "首页")
    @NotNull(message = "页面集显示名称不能为空")
    private String displayName;

    @Schema(description = "菜单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxxxx")
    @NotNull(message = "菜单id不能为空")
    private Long menuId;

    @Schema(description = "页面集类型 1-普通表单 2-流程表单 3-工作台 4-数据大屏", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页面集类型不能为空")
    private Integer pageSetType;

    @Schema(description = "页面集描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "这是首页的页面集")
    private String description;

    @Schema(description = "页面集主元数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    private String mainMetadata;

    @Schema(description = "数据大屏页面创建类型", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "xxx")
    private String createDashboardType;

    @Schema(description = "数据大屏/模板id", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "xxx")
    private Long dashboardId;
}
