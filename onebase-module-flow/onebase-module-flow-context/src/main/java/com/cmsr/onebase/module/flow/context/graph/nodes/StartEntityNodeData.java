package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

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

    /**
     * 过滤条件
     */
    private List<Conditions> filterCondition;

}
