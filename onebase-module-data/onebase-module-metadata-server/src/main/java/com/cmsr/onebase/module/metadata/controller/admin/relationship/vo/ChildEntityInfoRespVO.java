package com.cmsr.onebase.module.metadata.controller.admin.relationship.vo;

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

    @Schema(description = "子表实体ID", example = "1002")
    private Long childEntityId;

    @Schema(description = "子表实体名称", example = "订单信息")
    private String childEntityName;

    @Schema(description = "子表实体编码", example = "order_info")
    private String childEntityCode;

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
}
