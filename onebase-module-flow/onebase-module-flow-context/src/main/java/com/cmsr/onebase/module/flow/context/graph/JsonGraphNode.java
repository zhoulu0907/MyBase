package com.cmsr.onebase.module.flow.context.graph;

import com.cmsr.onebase.module.flow.context.graph.nodes.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:01
 */
@Data
public class JsonGraphNode implements Serializable {

    private String id;

    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type",
            visible = true,
            defaultImpl = NodeData.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DataAddNodeData.class, name = "dataAdd"),
            @JsonSubTypes.Type(value = DataCalcNodeData.class, name = "dataCalc"),
            @JsonSubTypes.Type(value = DataDeleteeNodeData.class, name = "dataDelete"),
            @JsonSubTypes.Type(value = DataQueryMultipleNodeData.class, name = "dataQueryMultiple"),
            @JsonSubTypes.Type(value = DataQueryNodeData.class, name = "dataQuery"),
            @JsonSubTypes.Type(value = DataUpdateNodeData.class, name = "dataUpdate"),
            @JsonSubTypes.Type(value = EndNodeData.class, name = "end"),
            @JsonSubTypes.Type(value = IfBlockNodeData.class, name = "ifBlock"),
            @JsonSubTypes.Type(value = IfCaseNodeData.class, name = "ifCase"),
            @JsonSubTypes.Type(value = LoopNodeData.class, name = "loop"),
            @JsonSubTypes.Type(value = ModalNodeData.class, name = "modal"),
            @JsonSubTypes.Type(value = NavigateNodeData.class, name = "navigate"),
            @JsonSubTypes.Type(value = RefreshNodeData.class, name = "refresh"),
            @JsonSubTypes.Type(value = ScriptNodeData.class, name = "javascript"),
            @JsonSubTypes.Type(value = StartDateFieldNodeData.class, name = "startDateField"),
            @JsonSubTypes.Type(value = StartEntityNodeData.class, name = "startEntity"),
            @JsonSubTypes.Type(value = StartFormNodeData.class, name = "startForm"),
            @JsonSubTypes.Type(value = StartTimeNodeData.class, name = "startTime"),
            @JsonSubTypes.Type(value = SwitchCaseNodeData.class, name = "switchCase"),
            @JsonSubTypes.Type(value = SwitchConditionNodeData.class, name = "switchCondition")
    })
    private NodeData data;

    private List<JsonGraphNode> blocks;

}
