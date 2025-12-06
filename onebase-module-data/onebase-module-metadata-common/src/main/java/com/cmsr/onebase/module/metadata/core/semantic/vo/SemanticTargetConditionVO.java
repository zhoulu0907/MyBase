package com.cmsr.onebase.module.metadata.core.semantic.vo;

import java.util.HashMap;
import java.util.Map;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 目标条件 VO
 *
 * <p>用于目标场景，动态接收任意顶层键值对（字段名/连接器名），
 * 并将其存储在 {@code data} 中。</p>
 */
@Data
@Schema(description = "指定查询、删除或更新条件 VO")
public class SemanticTargetConditionVO {
    @Schema(description = "触发链路id")
    private String traceId;
    @Schema(description = "目标表名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tableName;
    @Schema(description = "查询/删除/更新条件")
    private SemanticConditionDTO semanticConditionDTO;
    @Schema(description = "更新内容：字段名到值的映射（批量更新时必填）")
    private java.util.Map<String, Object> updateProperties;
    @Schema(description = "可选：方法编码")
    private String methodCode;
}
