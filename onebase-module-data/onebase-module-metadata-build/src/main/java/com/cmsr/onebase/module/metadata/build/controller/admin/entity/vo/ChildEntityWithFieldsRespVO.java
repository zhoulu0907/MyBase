package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 子表实体及字段信息 Response VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "管理后台 - 子表实体及字段信息 Response VO")
@Data
public class ChildEntityWithFieldsRespVO {

    @Schema(description = "子表实体ID", example = "1001")
    private Long childEntityId;

    @Schema(description = "子表实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String childEntityUuid;

    @Schema(description = "子表实体名称", example = "订单明细")
    private String childEntityName;

    @Schema(description = "子表实体编码", example = "order_detail")
    private String childEntityCode;

    @Schema(description = "子表实际表名", example = "t_order_detail")
    private String childTableName;

    @Schema(description = "关系ID", example = "5001")
    private Long relationshipId;

    @Schema(description = "关系UUID", example = "01onal1s-0000-0000-0000-000000000005")
    private String relationshipUuid;

    @Schema(description = "关系名称", example = "订单-订单明细关系")
    private String relationshipName;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "源字段名称（主表关联字段）", example = "id")
    private String sourceFieldName;

    @Schema(description = "目标字段名称（子表关联字段）", example = "order_id")
    private String targetFieldName;

    @Schema(description = "子表字段列表（完整字段信息）")
    private List<EntityFieldRespVO> childFields;
}
