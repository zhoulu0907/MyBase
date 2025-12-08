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
     * mainTable
     * subTable
     */
    private String dataType;

    private String mainTableName;

    private String subTableName;

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

}
