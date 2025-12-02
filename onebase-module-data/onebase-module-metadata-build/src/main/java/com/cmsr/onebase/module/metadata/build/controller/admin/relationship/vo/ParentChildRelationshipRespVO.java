package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 主子关系响应 Response VO
 *
 * @author matianyu
 * @date 2025-08-08
 */
@Schema(description = "管理后台 - 主子关系响应 Response VO")
@Data
public class ParentChildRelationshipRespVO {

    @Schema(description = "关系ID", example = "5001")
    private Long id;

    @Schema(description = "主表实体ID", example = "2001")
    private Long parentEntityId;

    @Schema(description = "主表实体名称", example = "订单信息")
    private String parentEntityName;

    @Schema(description = "子表实体ID", example = "2002")
    private Long childEntityId;

    @Schema(description = "子表实体名称", example = "订单明细")
    private String childEntityName;

    @Schema(description = "关联字段ID（主表的id字段）", example = "3001")
    private Long sourceFieldId;

    @Schema(description = "关联字段名称", example = "id")
    private String sourceFieldName;

    @Schema(description = "关联字段ID（子表的parent_id字段）", example = "3010")
    private Long targetFieldId;

    @Schema(description = "关联字段名称", example = "parent_id")
    private String targetFieldName;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "级联操作类型", example = "ALL")
    private String cascadeType;

    @Schema(description = "应用ID", example = "12345")
    private Long applicationId;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01 10:00:00")
    private LocalDateTime updateTime;

}
