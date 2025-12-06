package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.condition.SortItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:01
 */
@Data
public class DataQueryMultipleNodeData extends NodeData implements Serializable {

    /**
     * mainEntity
     * subEntity
     */
    private String dataType;

    private String mainEntityName;

    private String subEntityName;

    private Integer maxCount;

    /**
     * all
     * condition
     */
    private String filterType;

    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<Conditions> filterCondition;

    private List<SortItem> sortBy;

    public Integer getMaxCountWithDefault(int defaultMaxCount) {
        return maxCount == null ? defaultMaxCount : maxCount;
    }

}
