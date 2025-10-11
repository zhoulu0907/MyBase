package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:14
 */
@Data
public class DataCalcNodeData extends NodeData {

    private List<ConditionItem> calRules;

}
