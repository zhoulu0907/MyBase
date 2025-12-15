package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 子表信息 Response VO
 *
 * @author matianyu
 * @date 2025-08-09
 */
@Schema(description = "管理后台 - 子表信息 Response VO")
@Data
public class ChildEntityInfoRespVO {

    @Schema(description = "子表实体ID", example = "173076084088700928")
    private String childEntityId;

    @Schema(description = "子表实体UUID", example = "uuid-1002")
    private String childEntityUuid;

    @Schema(description = "子表实体名称", example = "订单信息")
    private String childEntityName;

    @Schema(description = "子表实体编码", example = "ABC")
    private String childEntityCode;

    @Schema(description = "实际表名", example = "user_info")
    private String childTableName;

    @Schema(description = "关系ID", example = "5001")
    private String relationshipId;

    @Schema(description = "关系名称", example = "用户订单关系")
    private String relationshipName;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "源字段名称", example = "id")
    private String sourceFieldName;

    @Schema(description = "目标字段名称", example = "user_id")
    private String targetFieldName;

    @Schema(description = "子表字段信息列表")
    private List<EntityFieldInfoRespVO> childFields;
}
