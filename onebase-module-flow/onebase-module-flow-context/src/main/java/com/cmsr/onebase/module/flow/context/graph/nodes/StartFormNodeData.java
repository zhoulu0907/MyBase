package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 11:05
 */
@Data
public class StartFormNodeData extends NodeData implements Serializable {

    /**
     * 应用ID，!!!后补充!!!
     */
    private Long applicationId;
    /**
     * 流程ID，!!!后补充!!!
     */
    private Long processId;




    private String triggerRange;

    private List<String> recordTriggerEvents;

    private String fieldTriggerEvents;

    private String pageUuid;

    private Boolean isChildTriggerAllowed;

    private List<Conditions> filterCondition;

}
