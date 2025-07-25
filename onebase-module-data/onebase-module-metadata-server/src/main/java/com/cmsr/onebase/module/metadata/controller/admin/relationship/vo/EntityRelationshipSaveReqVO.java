package com.cmsr.onebase.module.metadata.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 实体关系创建/修改 Request VO")
@Data
public class EntityRelationshipSaveReqVO {

    @Schema(description = "关系编号", example = "1024")
    private Long id;

    @Schema(description = "关系名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户部门关系")
    @NotBlank(message = "关系名称不能为空")
    @Size(max = 128, message = "关系名称长度不能超过128个字符")
    private String relationName;

    @Schema(description = "源实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "源实体ID不能为空")
    private Long sourceEntityId;

    @Schema(description = "目标实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "目标实体ID不能为空")
    private Long targetEntityId;

    @Schema(description = "关系类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ONE_TO_MANY")
    @NotBlank(message = "关系类型不能为空")
    @Size(max = 32, message = "关系类型长度不能超过32个字符")
    private String relationshipType;

    @Schema(description = "源字段id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "源字段id不能为空")
    @Size(max = 128, message = "源字段id长度不能超过128个字符")
    private String sourceFieldId;

    @Schema(description = "目标字段id", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotBlank(message = "目标字段id不能为空")
    @Size(max = 128, message = "目标字段id长度不能超过128个字符")
    private String targetFieldId;

    @Schema(description = "级联操作类型", example = "read")
    @Size(max = 32, message = "级联操作类型长度不能超过32个字符")
    private String cascadeType;

    @Schema(description = "关系描述", example = "用户与部门的关联关系")
    @Size(max = 256, message = "关系描述长度不能超过256个字符")
    private String description;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "版本锁标识", example = "0")
    private Integer lockVersion;

}
