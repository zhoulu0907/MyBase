package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:40
 */
@Data
public class SwitchCaseNodeData extends NodeData implements Serializable {

    private List<Conditions> filterCondition;

}
