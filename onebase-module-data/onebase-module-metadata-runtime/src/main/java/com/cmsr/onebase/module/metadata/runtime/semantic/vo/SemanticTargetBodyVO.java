package com.cmsr.onebase.module.metadata.runtime.semantic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "目标请求体：data 中包含 id；详情可附包含控制")
@Data
/**
 * 目标请求体 VO
 *
 * <p>用于删除/详情场景，`data.id` 指定目标记录，
 * 允许通过包含控制决定是否返回子表/关系数据。</p>
 */
public class SemanticTargetBodyVO {

    @Schema(description = "业务数据包装")
    private Map<String, Object> data;

    @Schema(description = "可选：是否包含子表(详情用)")
    private Boolean containSubTable;

    @Schema(description = "可选：是否包含关系(详情用)")
    private Boolean containRelation;

    @Schema(description = "可选：方法编码")
    private String methodCode;
}
