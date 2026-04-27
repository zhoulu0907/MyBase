package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 批量更新实体字段 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 批量更新实体字段 Response VO")
@Data
public class EntityFieldBatchUpdateRespVO {

    @Schema(description = "成功更新数量", example = "2")
    private Integer successCount;

    @Schema(description = "失败数量", example = "0")
    private Integer failureCount;

}
