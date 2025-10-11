package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:40
 */
@Data
public class IfBlockNodeData extends NodeData {

    private boolean value;

}
