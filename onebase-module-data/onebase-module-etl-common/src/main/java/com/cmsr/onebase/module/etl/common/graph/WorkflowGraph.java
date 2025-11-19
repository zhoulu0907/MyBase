package com.cmsr.onebase.module.etl.common.graph;

import lombok.Data;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class WorkflowGraph {

    private List<Node> nodes;

    private List<Edge> edges;

    private DefaultDirectedGraph<Node, Edge> directedGraph;

    public void init() {
        directedGraph = new DefaultDirectedGraph<>(Edge.class);
        for (Node node : nodes) {
            directedGraph.addVertex(node);
        }
        for (Edge edge : edges) {
            String sourceNodeId = edge.getSourceNodeId();
            String targetNodeId = edge.getTargetNodeId();
            Node sourceNode = findNodeById(sourceNodeId);
            Node targetNode = findNodeById(targetNodeId);
            directedGraph.addEdge(sourceNode, targetNode, edge);
        }
    }


    public Node findIncomingNode(Node node) {
        for (Edge edge : edges) {
            if (edge.getTargetNodeId().equals(node.getId())) {
                return findNodeById(edge.getSourceNodeId());
            }
        }
        throw new IllegalArgumentException("Source node not found for target node: " + node.getId());
    }

    public List<Node> iterateNodes() {
        List<Node> result = new ArrayList<>();
        TopologicalOrderIterator breadthFirstIterator = new TopologicalOrderIterator<>(directedGraph);
        while (breadthFirstIterator.hasNext()) {
            result.add((Node) breadthFirstIterator.next());
        }
        return result;
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
