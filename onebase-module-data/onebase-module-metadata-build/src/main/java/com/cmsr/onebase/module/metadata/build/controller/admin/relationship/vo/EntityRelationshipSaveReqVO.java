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

    @Schema(description = "关系UUID（更新时可用于定位记录）", example = "01onal1s-0000-0000-0000-000000000005")
    private String relationshipUuid;

    @Schema(description = "关系名称", example = "用户订单关系")
    @Size(max = 100, message = "关系名称长度不能超过100个字符")
    private String relationName;

    @Schema(description = "源实体UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "01onal1s-0000-0000-0000-000000000002")
    @NotNull(message = "源实体UUID不能为空")
    private String sourceEntityUuid;

    @Schema(description = "目标实体UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "01onal1s-0000-0000-0000-000000000003")
    @NotNull(message = "目标实体UUID不能为空")
    private String targetEntityUuid;

    @Schema(description = "关系类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ONE_TO_MANY")
    @NotBlank(message = "关系类型不能为空")
    private String relationshipType;

    @Schema(description = "源字段UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "01onal1s-0000-0000-0000-000000000010")
    @NotNull(message = "源字段UUID不能为空")
    private String sourceFieldUuid;

    @Schema(description = "目标字段UUID", requiredMode = Schema.RequiredMode.REQUIRED, example = "01onal1s-0000-0000-0000-000000000011")
    @NotNull(message = "目标字段UUID不能为空")
    private String targetFieldUuid;

    @Schema(description = "选择字段UUID（数据选择关系类型时使用，表示关联表中用于展示给用户的字段UUID）", example = "01onal1s-0000-0000-0000-000000000012")
    private String selectFieldUuid;

    @Schema(description = "级联类型", example = "READ")
    private String cascadeType;

    @Schema(description = "描述信息", example = "用户与订单的一对多关系")
    @Size(max = 500, message = "描述信息长度不能超过500个字符")
    private String description;

    @Schema(description = "应用ID", example = "12345")
    private String applicationId;

}
