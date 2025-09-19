package com.cmsr.onebase.module.flow.core.graph.data;

import com.cmsr.onebase.module.flow.core.rule.ConditionItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:46
 */
@Data
public class StartEntityNodeData {

    private Long processId;

    private Long entityId;

    private List<String> triggerEvents;

    private List<Long> triggerFieldIds;

    private List<ConditionItem> filterCondition;

    /**
     * 缓存的表达式
     */
    private Serializable compiledExpression;
}
