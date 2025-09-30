package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.SortItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:01
 */
@Data
public class DataQueryMultipleNodeData extends NodeData {

    private Long mainEntityId;

    private Long subEntityId;

    private Integer maxCount;

    private List<ConditionItem> filterCondition;

    private List<SortItem> sortBy;

    public Integer getMaxCountWithDefault(int defaultMaxCount) {
        return maxCount == null ? defaultMaxCount : maxCount;
    }

}
