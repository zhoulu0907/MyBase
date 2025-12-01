package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName ListPageViewReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/10/10 19:15
 */
@Data
public class ListPageViewReqVO {
    @Schema(description = "pageSetId", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面集id不能为空")
    private String pageSetUuid;
}
