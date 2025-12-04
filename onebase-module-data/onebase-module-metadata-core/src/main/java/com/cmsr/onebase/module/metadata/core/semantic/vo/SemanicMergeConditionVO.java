package com.cmsr.onebase.module.metadata.core.semantic.vo;

import java.util.HashMap;
import java.util.Map;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合并条件 VO
 *
 * <p>用于合并场景，动态接收任意顶层键值对（字段名/连接器名），
 * 并将其存储在 {@code data} 中。</p>
 */
@Data
@Schema(description = "指定修改带条件 VO")
public class SemanicMergeConditionVO {
    @Schema(description = "方法编码")
    private String methodCode;
    @Schema(description = "目标表名")
    private String tableName;
    @Schema(description = "触发链路id")
    private String traceId;
    @Schema(description = "合并数据")
    private Map<String, Object> data = new HashMap<>();
    @Schema(description = "合并条件")
    private SemanticConditionDTO semanticConditionDTO;
}
