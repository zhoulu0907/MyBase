package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 关系类型 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 关系类型 Response VO")
@Data
public class RelationshipTypeRespVO {

    @Schema(description = "关系类型编码", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "显示名称", example = "一对多")
    private String displayName;

    @Schema(description = "描述信息", example = "一对多关联关系")
    private String description;

}
