package com.cmsr.onebase.module.etl.common.graph;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public WorkflowGraph subgraph(String endNodeId) {
        //TODO
        return this;
    }

    public List<Node> getStartNodes() {
        Set<String> sourceNodeIds = new HashSet<>();
        for (Edge edge : this.edges) {
            sourceNodeIds.add(edge.getSourceNodeId());
        }
        for (Edge edge : this.edges) {
            sourceNodeIds.remove(edge.getTargetNodeId());
        }
        return sourceNodeIds.stream().map(this::getNodeById).toList();
    }

    public List<Node> getEndNode() {
        Set<String> targetNodeIds = new HashSet<>();
        for (Edge edge : this.edges) {
            targetNodeIds.add(edge.getTargetNodeId());
        }
        for (Edge edge : this.edges) {
            targetNodeIds.remove(edge.getSourceNodeId());
        }
        return targetNodeIds.stream().map(this::getNodeById).toList();
    }
}
