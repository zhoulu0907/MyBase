package com.cmsr.onebase.module.metadata.core.semantic.vo;

import java.util.List;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询条件 VO
 *
 * <p>用于分页查询场景，动态接收任意顶层键值对（字段名/连接器名），
 * 并将其存储在 {@code data} 中。</p>
 */
@Data
@Schema(description = "指定分页查询条件 VO")
public class SemanticPageConditionVO {
    @Schema(description = "目标表名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tableName;

    @Schema(description = "分页查询条件")
    private SemanticConditionDTO semanticConditionDTO;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "分页大小")
    private Integer pageSize;

    @Schema(description = "多字段排序规则")
    private List<SemanticSortRuleDTO> sortBy;
}
