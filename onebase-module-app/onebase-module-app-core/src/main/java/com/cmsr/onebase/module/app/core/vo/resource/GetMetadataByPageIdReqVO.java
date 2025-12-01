package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName GetMetadataByPageIdReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class GetMetadataByPageIdReqVO {
    @Schema(description = "pageId", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面id不能为空")
    private String pageUuid;
}
