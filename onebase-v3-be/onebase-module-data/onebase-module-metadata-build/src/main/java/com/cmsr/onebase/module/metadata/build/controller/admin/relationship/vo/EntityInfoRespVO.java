package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 实体信息 Response VO
 *
 * @author matianyu
 * @date 2025-08-09
 */
@Schema(description = "管理后台 - 实体信息 Response VO")
@Data
public class EntityInfoRespVO {

    @Schema(description = "实体ID", example = "1001")
    private String entityId;

    @Schema(description = "实体显示名称", example = "用户信息")
    private String entityName;

    @Schema(description = "实体类型", example = "主表")
    private String entityType;

    @Schema(description = "实际表名", example = "user_info")
    private String tableName;

    @Schema(description = "实体字段列表")
    private List<EntityFieldInfoRespVO> fields;
}
