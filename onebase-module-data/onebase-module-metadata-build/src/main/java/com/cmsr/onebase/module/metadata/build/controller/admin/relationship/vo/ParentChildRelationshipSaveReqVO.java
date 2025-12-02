package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 主子关系保存 Request VO
 *
 * @author matianyu
 * @date 2025-08-08
 */
@Schema(description = "管理后台 - 主子关系保存 Request VO")
@Data
public class ParentChildRelationshipSaveReqVO {

    @Schema(description = "主表实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "主表实体ID不能为空")
    private String parentEntityId;

    @Schema(description = "子表实体ID", example = "2002")
    private String childEntityId;

    @Schema(description = "子表编码（新建子表时使用）", example = "order_detail")
    @Size(max = 32, message = "子表编码长度不能超过32个字符")
    private String childTableCode;

    @Schema(description = "子表名称（新建子表时使用）", example = "订单明细表")
    @Size(max = 64, message = "子表名称长度不能超过64个字符")
    private String childTableName;

    @Schema(description = "子表描述（新建子表时使用）", example = "订单的详细明细信息")
    @Size(max = 512, message = "子表描述长度不能超过512个字符")
    private String childTableDescription;

    @Schema(description = "应用ID", example = "12345")
    private String applicationId;

    @Schema(description = "数据源ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据源ID不能为空")
    private String datasourceId;

}
