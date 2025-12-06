package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:10
 */
@Data
public class DataUpdateNodeData extends NodeData implements Serializable {

    private String mainEntityName;

    private String subEntityName;

    /**
     * 更新类型
     * "updateType": "mainEntity"
     * "updateType": "subEntity",
     */
    private String updateType;

    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<Conditions> filterCondition;

    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<ConditionItem> fields;

}
