package com.cmsr.onebase.module.flow.context.graph.nodes.logic;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:40
 */
@Data
@NodeType("ifBlock")
public class IfBlockNodeData extends NodeData implements Serializable {

    private boolean value;

}
