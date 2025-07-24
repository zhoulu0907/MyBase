package com.cmsr.onebase.module.metadata.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实体关系 Response VO")
@Data
public class EntityRelationshipRespVO {

    @Schema(description = "关系编号", example = "1024")
    private Long id;

    @Schema(description = "关系名称", example = "用户部门关系")
    private String relationName;

    @Schema(description = "源实体ID", example = "1")
    private Long sourceEntityId;

    @Schema(description = "目标实体ID", example = "2")
    private Long targetEntityId;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "源字段id", example = "1")
    private String sourceFieldId;

    @Schema(description = "目标字段id", example = "2")
    private String targetFieldId;

    @Schema(description = "级联操作类型", example = "read")
    private String cascadeType;

    @Schema(description = "关系描述", example = "用户与部门的关联关系")
    private String description;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", example = "1")
    private Long appId;

    @Schema(description = "版本锁标识", example = "0")
    private Integer lockVersion;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private Long creator;

    @Schema(description = "更新人")
    private Long updater;

    @Schema(description = "租户ID")
    private Long tenantId;

}
