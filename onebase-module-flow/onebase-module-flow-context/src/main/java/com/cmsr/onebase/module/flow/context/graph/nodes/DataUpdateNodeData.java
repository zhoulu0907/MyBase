package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:10
 */
@Data
public class DataUpdateNodeData extends NodeData {

    private Long mainEntityId;

    private Long subEntityId;

    private List<ConditionItem> filterCondition;

    private List<RuleItem> fields;

}
