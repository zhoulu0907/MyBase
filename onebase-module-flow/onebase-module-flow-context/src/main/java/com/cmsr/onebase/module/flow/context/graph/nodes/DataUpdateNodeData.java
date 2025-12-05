package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
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

    private Long mainEntityId;

    private String mainEntityName;

    private Long subEntityId;

    private String subEntityName;

    /**
     * 更新类型
     * "updateType": "mainEntity"
     * "updateType": "subEntity",
     */
    private String updateType;

    private List<Conditions> filterCondition;

    private List<ConditionItem> fields;

}
