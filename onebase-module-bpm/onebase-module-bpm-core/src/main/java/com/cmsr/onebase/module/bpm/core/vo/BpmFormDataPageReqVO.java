package com.cmsr.onebase.module.bpm.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticSortRuleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 获取列表信息请求VO
 *
 * @author liyang
 * @date 2025-12-17
 */
@Data
public class BpmFormDataPageReqVO extends PageParam {
    @Schema(description = "实体表名", example = "sog_main")
    @NotBlank(message = "实体表名不能为空")
    private String tableName;

    @NotNull(message = "菜单ID不能为空")
    @Schema(description = "菜单ID", example = "1111")
    private Long menuId;

    @Schema(description = "业务数据包装")
    private Map<String, Object> data;

    @Schema(description = "多字段排序规则")
    private List<SemanticSortRuleDTO> sortBy;

    @Schema(description = "过滤条件（使用语义条件DTO）")
    private SemanticConditionDTO filters;
}
