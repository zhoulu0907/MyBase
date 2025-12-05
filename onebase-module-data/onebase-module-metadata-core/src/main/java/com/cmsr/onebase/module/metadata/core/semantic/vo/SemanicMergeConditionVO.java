package com.cmsr.onebase.module.metadata.core.semantic.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
