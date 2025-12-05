package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 8:58
 */
@Data
public class DataDeleteeNodeData extends NodeData implements Serializable {

    private String mainEntityName;

    private String subEntityName;

    /**
     * mainEntity
     * subEntity
     */
    private String dataType;

    /**
     * all
     * condition
     */
    private String filterType;

    private List<Conditions> filterCondition;
}
