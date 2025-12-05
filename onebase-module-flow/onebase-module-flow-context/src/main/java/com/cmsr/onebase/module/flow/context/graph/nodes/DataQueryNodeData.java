package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.condition.SortItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:07
 */
@Data
public class DataQueryNodeData extends NodeData implements Serializable {

    /**
     * mainEntity
     * subEntity
     */
    private String dataType;

    private Long mainEntityId;

    private String mainEntityName;

    private Long subEntityId;

    private String subEntityName;

    /**
     * all
     * condition
     */
    private String filterType;

    private List<Conditions> filterCondition;

    private List<SortItem> sortBy;

}
