package com.cmsr.onebase.module.flow.context.graph.nodes.logic;

import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:40
 */
@Data
@NodeType("switchCondition")
public class SwitchConditionNodeData extends NodeData implements Serializable {

    private List<Case> cases = new ArrayList<>();

    private String defaultId;

    public void addCase(String id, List<Conditions> filterCondition) {
        Case aCase = new Case();
        aCase.setId(id);
        aCase.setFilterCondition(filterCondition);
        cases.add(aCase);
    }

    @Data
    public static class Case implements Serializable {

        private String id;

        private List<Conditions> filterCondition;
    }

}
