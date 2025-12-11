package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName CreatePageViewReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class CreatePageViewReqVO {
    @Schema(description = "pageSetId", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面集id不能为空")
    private Long pageSetId;

    @Schema(description = "viewType", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "视图类型")
    private String viewType;

    @Schema(description = "viewName", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "视图名称")
    private String viewName;
}
