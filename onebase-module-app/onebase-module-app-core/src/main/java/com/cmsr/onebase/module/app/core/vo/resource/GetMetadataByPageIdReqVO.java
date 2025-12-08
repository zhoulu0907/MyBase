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

    @Schema(description = "pageId",  example = "xxx")
    private Long pageId;

    @Schema(description = "pageUuid",  example = "xxx")
    private String pageUuid;

}
