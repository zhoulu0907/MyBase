package com.cmsr.onebase.module.etl.executor.graph;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowGraph {

    private List<Node> nodes;

    private List<Edge> edges;

    public Node getSourceNode(Node node) {
        for (Edge edge : edges) {
            if (edge.getTargetNodeId().equals(node.getId())) {
                return getNodeById(edge.getSourceNodeId());
            }
        }
        throw new IllegalArgumentException("Source node not found for target node: " + node.getId());
    }

    private Node getNodeById(String nodeId) {
        for (Node node : nodes) {
            if (node.getId().equals(nodeId)) {
                return node;
            }
        }
        throw new IllegalArgumentException("Node not found: " + nodeId);
    }
}
