package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:01
 */
@Data
public class JsonGraphNode {

    private String id;

    private String type;

    private NodeData data;

    private List<JsonGraphNode> blocks;

}
