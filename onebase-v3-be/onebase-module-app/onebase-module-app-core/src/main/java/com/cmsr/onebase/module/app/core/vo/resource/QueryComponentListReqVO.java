package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName GetComponentListByPageIdReqVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class QueryComponentListReqVO {

    @Schema(description = "PageId", example = "xxx")
    private Long pageId;

    @Schema(description = "PageUuid", example = "xxx")
    private String pageUuid;

}
