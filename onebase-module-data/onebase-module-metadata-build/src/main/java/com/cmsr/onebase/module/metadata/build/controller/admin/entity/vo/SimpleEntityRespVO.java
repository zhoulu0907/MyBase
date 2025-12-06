package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 简单实体信息 Response VO
 *
 * @author matianyu
 * @date 2025-08-09
 */
@Schema(description = "管理后台 - 简单实体信息 Response VO")
@Data
public class SimpleEntityRespVO {

    @Schema(description = "实体ID", example = "1001")
    private Long entityId;

    @Schema(description = "实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "实体显示名称", example = "用户信息")
    private String entityName;

    @Schema(description = "实际表名", example = "user_info")
    private String tableName;

    @Schema(description = "关系类型：MASTER(主表)/SLAVE(子表)/NONE(无关系)", example = "MASTER")
    private String relationType;
}
