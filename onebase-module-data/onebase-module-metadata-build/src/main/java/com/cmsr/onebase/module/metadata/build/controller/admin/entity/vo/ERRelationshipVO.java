package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ER图关联关系VO
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Schema(description = "管理后台 - ER图关联关系")
@Data
public class ERRelationshipVO {

    @Schema(description = "关联关系ID", example = "1024")
    private String relationshipId;

    @Schema(description = "源实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "165890174290853888")
    private String sourceEntityId;

    @Schema(description = "源实体UUID", example = "019ae8db-5443-7175-b816-6c3a5b1441f2")
    private String sourceEntityUuid;

    @Schema(description = "源实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户表")
    private String sourceEntityName;

    @Schema(description = "源字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "165890174290853889")
    private String sourceFieldId;

    @Schema(description = "源字段UUID", example = "019ae8db-5443-7175-b816-6c3a5b1441f3")
    private String sourceFieldUuid;

    @Schema(description = "源字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_id")
    private String sourceFieldName;

    @Schema(description = "目标实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "165890174290853890")
    private String targetEntityId;

    @Schema(description = "目标实体UUID", example = "019ae8db-5443-7175-b816-6c3a5b1441f4")
    private String targetEntityUuid;

    @Schema(description = "目标实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "角色表")
    private String targetEntityName;

    @Schema(description = "目标字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "165890174290853891")
    private String targetFieldId;

    @Schema(description = "目标字段UUID", example = "019ae8db-5443-7175-b816-6c3a5b1441f5")
    private String targetFieldUuid;

    @Schema(description = "目标字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "role_id")
    private String targetFieldName;

    @Schema(description = "关联关系类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "关联关系名称", example = "用户角色关联")
    private String relationshipName;

    @Schema(description = "关联关系描述", example = "用户与角色的多对多关联关系")
    private String description;
}
