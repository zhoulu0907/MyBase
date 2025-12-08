package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/9 13:46
 */
@Data
public class StartEntityNodeData extends NodeData implements Serializable {

    /**
     * 应用ID，后补充
     */
    private Long applicationId;
    /**
     * 流程ID，后补充
     */
    private Long processId;

    private String tableName;

    private List<String> triggerEvents;

    /**
     * 过滤条件
     */
    private List<Conditions> filterCondition;

}
