package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScriptNodeData extends NodeData implements Serializable {

    /**
     * 数据库补充
     */
    private String script;

    private String instanceUuid;

    private String actionUuid;

    private String title;

    private List<ConditionItem> inputParameterFields;

    private String outputParameter;

    private String inputSchema;

    private String outputSchema;

}
