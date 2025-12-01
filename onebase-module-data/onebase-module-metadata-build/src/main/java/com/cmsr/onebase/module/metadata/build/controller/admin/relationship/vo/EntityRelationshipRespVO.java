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

    @Schema(description = "关系名称", example = "用户订单关系")
    private String relationName;

    @Schema(description = "源实体ID", example = "2001")
    private Long sourceEntityId;

    @Schema(description = "源实体名称", example = "用户信息")
    private String sourceEntityName;

    @Schema(description = "目标实体ID", example = "2002")
    private Long targetEntityId;

    @Schema(description = "目标实体名称", example = "订单信息")
    private String targetEntityName;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "源字段ID", example = "3001")
    private String sourceFieldId;

    @Schema(description = "源字段名称", example = "id")
    private String sourceFieldName;

    @Schema(description = "源字段展示名称", example = "主键ID")
    private String sourceFieldDisplayName;

    @Schema(description = "目标字段ID", example = "3010")
    private String targetFieldId;

    @Schema(description = "目标字段名称", example = "userId")
    private String targetFieldName;

    @Schema(description = "目标字段展示名称", example = "用户ID")
    private String targetFieldDisplayName;

    @Schema(description = "选择字段ID（数据选择关系类型时使用）", example = "3011")
    private String selectFieldId;

    @Schema(description = "选择字段名称", example = "name")
    private String selectFieldName;

    @Schema(description = "选择字段展示名称", example = "名称")
    private String selectFieldDisplayName;

    @Schema(description = "级联类型", example = "READ")
    private String cascadeType;

    @Schema(description = "描述信息", example = "用户与订单的一对多关系")
    private String description;

    @Schema(description = "应用ID", example = "1001")
    private Long appId;

    @Schema(description = "运行模式：0 编辑态，1 运行态", example = "0")
    private Integer runMode;

    @Schema(description = "创建时间", example = "2025-07-28T10:30:00")
    private LocalDateTime createTime;

}
