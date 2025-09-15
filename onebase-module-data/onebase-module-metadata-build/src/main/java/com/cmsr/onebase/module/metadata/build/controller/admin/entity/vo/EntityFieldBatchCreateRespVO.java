package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 批量创建实体字段 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 批量创建实体字段 Response VO")
@Data
public class EntityFieldBatchCreateRespVO {

    @Schema(description = "成功创建数量", example = "2")
    private Integer successCount;

    @Schema(description = "失败数量", example = "0")
    private Integer failureCount;

    @Schema(description = "创建成功的字段ID列表", example = "[\"3001\", \"3002\"]")
    private List<String> fieldIds;

}
