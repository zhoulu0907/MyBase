package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName GetComponentListByPageIdReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class QueryComponentListReqVO {

    @Schema(description = "PageId", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotNull(message = "页面id不能为空")
    private Long pageId;

}
