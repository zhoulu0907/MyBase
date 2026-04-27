package com.cmsr.onebase.module.bpm.core.dto.edge;

import com.cmsr.onebase.module.bpm.core.dto.edge.condition.BpmConditionItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 边配置信息
 *
 * @author liyang
 * @date 2025-12-26
 */
@Data
public class EdgeExtDTO {
    /**
     * 节点名称
     */
    private String name;

    /**
     * 优先级
     */
    @NotNull(message = "优先级不能为空")
    private Integer priority;

    /**
     * 是否默认分支
     */
    @NotNull(message = "是否默认分支不能为空")
    @JsonProperty("isDefault")
    private Boolean isDefault;

    /**
     * 条件表达式
     */
    private List<List<BpmConditionItem>> condition;
}
