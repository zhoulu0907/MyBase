package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 实体关系 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 实体关系 Response VO")
@Data
public class EntityRelationshipRespVO {

    @Schema(description = "关系ID", example = "5001")
    private Long id;

    @Schema(description = "关系UUID", example = "01onal1s-0000-0000-0000-000000000005")
    private String relationshipUuid;

    @Schema(description = "关系名称", example = "用户订单关系")
    private String relationName;

    @Schema(description = "源实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String sourceEntityUuid;

    @Schema(description = "源实体名称", example = "用户信息")
    private String sourceEntityName;

    @Schema(description = "目标实体UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String targetEntityUuid;

    @Schema(description = "目标实体名称", example = "订单信息")
    private String targetEntityName;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "源字段UUID", example = "01onal1s-0000-0000-0000-000000000010")
    private String sourceFieldUuid;

    @Schema(description = "源字段名称", example = "id")
    private String sourceFieldName;

    @Schema(description = "源字段展示名称", example = "主键ID")
    private String sourceFieldDisplayName;

    @Schema(description = "目标字段UUID", example = "01onal1s-0000-0000-0000-000000000011")
    private String targetFieldUuid;

    @Schema(description = "目标字段名称", example = "userId")
    private String targetFieldName;

    @Schema(description = "目标字段展示名称", example = "用户ID")
    private String targetFieldDisplayName;

    @Schema(description = "选择字段UUID（数据选择关系类型时使用）", example = "01onal1s-0000-0000-0000-000000000012")
    private String selectFieldUuid;

    @Schema(description = "选择字段名称", example = "name")
    private String selectFieldName;

    @Schema(description = "选择字段展示名称", example = "名称")
    private String selectFieldDisplayName;

    @Schema(description = "级联类型", example = "READ")
    private String cascadeType;

    @Schema(description = "描述信息", example = "用户与订单的一对多关系")
    private String description;

    @Schema(description = "应用ID", example = "1001")
    private Long applicationId;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "创建时间", example = "2025-07-28T10:30:00")
    private LocalDateTime createTime;

}
