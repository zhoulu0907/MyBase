package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:05
 */
@Data
public class StartFormNodeData extends NodeData {

    private String triggerUserType;

    private String triggerUserValue;

    private Long pageId;

    private Long fieldId;

    private List<String> triggerEvents;

    private List<Conditions> filterCondition;

}
