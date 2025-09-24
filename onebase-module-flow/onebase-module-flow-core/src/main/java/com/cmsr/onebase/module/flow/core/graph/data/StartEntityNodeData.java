package com.cmsr.onebase.module.flow.core.graph.data;

import com.cmsr.onebase.framework.express.ConditionItem;
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

    /**
     * 过滤条件
     */
    private List<ConditionItem> filterCondition;

    /**
     * 过滤条件缓存的表达式
     */
    private Serializable compiledExpression;
}
