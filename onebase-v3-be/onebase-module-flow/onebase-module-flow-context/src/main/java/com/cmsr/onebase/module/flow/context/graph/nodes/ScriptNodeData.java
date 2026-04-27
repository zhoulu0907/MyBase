package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@NodeType("javascript")
public class ScriptNodeData extends NodeData implements Serializable {

    /**
     * 数据库补充
     */
    private String script;

    private String instanceUuid;

    // TODO: 临时的兼容策略，由于连接器暂未实现版本发布
    private Long actionId;

    private String actionUuid;

    private String title;

    private List<ConditionItem> inputParameterFields;

    private String outputParameter;

    private String inputSchema;

    private String outputSchema;

}
