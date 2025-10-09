package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.jexl3.JexlExpression;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:46
 */
@Data
public class StartEntityNodeData extends NodeData {

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
    @JsonIgnore
    private JexlExpression compiledExpression;
}
