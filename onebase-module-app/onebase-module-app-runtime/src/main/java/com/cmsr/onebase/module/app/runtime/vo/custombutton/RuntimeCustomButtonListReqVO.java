package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "运行态-查询可用自定义按钮请求")
public class RuntimeCustomButtonListReqVO {

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID。查询该页面集下当前用户可见的自定义按钮", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "菜单ID。用于过滤当前用户在该菜单下有权限的按钮")
    private Long menuId;

    @Schema(description = "页面ID。用于后续按页面细分按钮时扩展")
    private Long pageId;

    @Schema(description = "是否查询批量操作按钮：true 查询 BATCH；false 或空查询 SINGLE")
    private Boolean batch;
}
