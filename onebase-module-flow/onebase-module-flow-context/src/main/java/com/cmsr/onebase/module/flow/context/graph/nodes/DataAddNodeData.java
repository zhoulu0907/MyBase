package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/29 22:10
 */
@Data
public class DataAddNodeData extends NodeData implements Serializable {

    /**
     * mainEntity
     * subEntity
     */
    private String addType;

    private String mainEntityName;

    private String subEntityName;

    private Boolean batchType;

    private String dataNodeId;

    private List<ConditionItem> fields;
}
