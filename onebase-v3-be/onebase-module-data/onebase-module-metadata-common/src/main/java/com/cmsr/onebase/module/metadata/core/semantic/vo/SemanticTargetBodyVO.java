package com.cmsr.onebase.module.metadata.core.semantic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "目标请求体：顶层直接接收 id；详情可附包含控制")
@Data
/**
 * 目标请求体 VO
 *
 * <p>用于删除/详情场景，顶层 `id` 指定目标记录，
 * 允许通过包含控制决定是否返回子表/关系数据。</p>
 */
public class SemanticTargetBodyVO {

    @Schema(description = "目标表名")
    private String tableName;

    @Schema(description = "触发链路id")
    private String traceId;

    @Schema(description = "目标数据主键id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object id;

    @Schema(description = "可选：是否包含子表(详情用)")
    private Boolean containSubTable;

    @Schema(description = "可选：是否包含关系(详情用)")
    private Boolean containRelation;

    @Schema(description = "可选：方法编码")
    private String methodCode;
}
