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


    /**
     * 更新类型
     * "updateType": "mainTable"
     * "updateType": "subTable",
     */
    private String updateType;

    private String mainTableName;

    private String subTableName;


    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<Conditions> filterCondition;

    /**
     * 数据透传给API接口，不需要转换类型，因此不需要补充fieldType
     */
    private List<ConditionItem> fields;

}
