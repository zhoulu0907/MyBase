package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "运行态-查询可用自定义按钮请求")
public class RuntimeCustomButtonListReqVO {

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "页面ID")
    private Long pageId;

    @Schema(description = "是否批量操作")
    private Boolean batch;
}
