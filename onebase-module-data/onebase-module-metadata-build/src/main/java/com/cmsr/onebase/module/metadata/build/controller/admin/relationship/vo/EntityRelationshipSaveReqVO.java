package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 实体关系保存 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 实体关系保存 Request VO")
@Data
public class EntityRelationshipSaveReqVO {

    @Schema(description = "关系ID（更新时必填）", example = "5001")
    private String id;

    @Schema(description = "关系名称", example = "用户订单关系")
    @Size(max = 100, message = "关系名称长度不能超过100个字符")
    private String relationName;

    @Schema(description = "源实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "源实体ID不能为空")
    private String sourceEntityId;

    @Schema(description = "目标实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2002")
    @NotNull(message = "目标实体ID不能为空")
    private String targetEntityId;

    @Schema(description = "关系类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ONE_TO_MANY")
    @NotBlank(message = "关系类型不能为空")
    private String relationshipType;

    @Schema(description = "源字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    @NotNull(message = "源字段ID不能为空")
    private String sourceFieldId;

    @Schema(description = "目标字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3010")
    @NotNull(message = "目标字段ID不能为空")
    private String targetFieldId;

    @Schema(description = "选择字段ID（数据选择关系类型时使用，表示关联表中用于展示给用户的字段ID）", example = "3011")
    private String selectFieldId;

    @Schema(description = "级联类型", example = "READ")
    private String cascadeType;

    @Schema(description = "描述信息", example = "用户与订单的一对多关系")
    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    private String description;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12345")
    @NotNull(message = "应用ID不能为空")
    private String appId;

}
