package com.cmsr.onebase.module.etl.common.graph;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class WorkflowGraph {

    private List<Node> nodes;

    private List<Edge> edges;

    public Node findIncomingNode(Node node) {
        for (Edge edge : edges) {
            if (edge.getTargetNodeId().equals(node.getId())) {
                return findNodeById(edge.getSourceNodeId());
            }
        }
        throw new IllegalArgumentException("Source node not found for target node: " + node.getId());
    }

    private Node findNodeById(String nodeId) {
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

    public List<Node> findStartNodes() {
        Set<String> sourceNodeIds = new HashSet<>();
        for (Edge edge : this.edges) {
            sourceNodeIds.add(edge.getSourceNodeId());
        }
        for (Edge edge : this.edges) {
            sourceNodeIds.remove(edge.getTargetNodeId());
        }
        return sourceNodeIds.stream().map(this::findNodeById).toList();
    }

    public List<Node> findEndNodes() {
        Set<String> targetNodeIds = new HashSet<>();
        for (Edge edge : this.edges) {
            targetNodeIds.add(edge.getTargetNodeId());
        }
        for (Edge edge : this.edges) {
            targetNodeIds.remove(edge.getSourceNodeId());
        }
        return targetNodeIds.stream().map(this::findNodeById).toList();
    }
}
