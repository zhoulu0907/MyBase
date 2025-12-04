package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体关系 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 实体关系信息")
@Data
public class MetadataEntityRelationshipDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "关系UUID")
    private String relationshipUuid;

    @Schema(description = "关系名称")
    private String relationName;

    @Schema(description = "源实体UUID")
    private String sourceEntityUuid;

    @Schema(description = "目标实体UUID")
    private String targetEntityUuid;

    @Schema(description = "关系类型")
    private String relationshipType;

    @Schema(description = "源字段UUID")
    private String sourceFieldUuid;

    @Schema(description = "目标字段UUID")
    private String targetFieldUuid;

    @Schema(description = "级联操作类型")
    private String cascadeType;

    @Schema(description = "关系描述")
    private String description;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private Long creator;

    @Schema(description = "更新者")
    private Long updater;

    @Schema(description = "是否删除")
    private Long deleted;

    @Schema(description = "租户ID")
    private Long tenantId;

}
