package com.cmsr.onebase.module.flow.context.graph;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:06
 */
@Getter
@Setter
public class JsonGraph {

    private List<JsonGraphNode> nodes;

    public JsonGraphNode getStartNode() {
        JsonGraphNode jsonGraphNode = nodes.get(0);
        if (!jsonGraphNode.getType().contains("start")) {
            throw new IllegalArgumentException("第一个节点必须是开始节点");
        }
        return jsonGraphNode;
    }

    public Map<String, NodeData> getNodeData() {
        Map<String, NodeData> result = new HashMap<>();
        recursiveNode(result, nodes);
        return result;
    }

    private void recursiveNode(Map<String, NodeData> result, List<JsonGraphNode> nodes) {
        for (JsonGraphNode node : nodes) {
            result.put(node.getId(), node.getData());
            if (node.getBlocks() != null && node.getBlocks().size() > 0) {
                recursiveNode(result, node.getBlocks());
            }
        }
    }
}
