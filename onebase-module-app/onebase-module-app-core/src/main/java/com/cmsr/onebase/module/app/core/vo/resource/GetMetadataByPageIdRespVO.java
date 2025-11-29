package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName GetMetadataByPageIdRespVO
 * @Description TODO
 * @Author mickey
 * @Date 2025/9/1 18:54
 */
@Data
public class GetMetadataByPageIdRespVO {
    @Schema(description = "metadata")
    private String metadata;
}
