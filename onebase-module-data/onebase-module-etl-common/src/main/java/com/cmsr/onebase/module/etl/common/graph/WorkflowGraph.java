package com.cmsr.onebase.module.etl.common.graph;

import lombok.Data;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class WorkflowGraph {

    private List<Node> nodes = new ArrayList<>();

    private List<Edge> edges = new ArrayList<>();

    private DefaultDirectedGraph<Node, Edge> directedGraph;

    private DefaultDirectedGraph<Node, Edge> directedGraph() {
        init();
        return directedGraph;
    }

    public void init() {
        if (directedGraph != null) {
            return;
        }
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
        TopologicalOrderIterator breadthFirstIterator = new TopologicalOrderIterator<>(directedGraph());
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
        Set<Node> startNodes = new HashSet<>();
        startNodes.addAll(findStartNodes());
        Set<Node> endNodes = Set.of(findNodeById(endNodeId));

        AllDirectedPaths<Node, Edge> allPathsFinder = new AllDirectedPaths<>(directedGraph());
        List<GraphPath<Node, Edge>> allPaths = allPathsFinder.getAllPaths(
                startNodes,
                endNodes,
                true,
                directedGraph().vertexSet().size()
        );
        WorkflowGraph subgraph = new WorkflowGraph();
        for (GraphPath<Node, Edge> path : allPaths) {
            path.getVertexList().forEach(node -> subgraph.addNode(node));
            path.getEdgeList().forEach(edge -> subgraph.addEdge(edge));
        }
        subgraph.init();
        return subgraph;
    }


    public List<Node> findStartNodes() {
        List<Node> sourceNodes = new ArrayList<>();
        for (Node node : this.nodes) {
            int inDegreeOf = directedGraph().inDegreeOf(node);
            if (inDegreeOf == 0) {
                sourceNodes.add(node);
            }
        }
        return sourceNodes;
    }

    public List<Node> findEndNodes() {
        List<Node> targetNodes = new ArrayList<>();
        for (Node node : this.nodes) {
            int outDegreeOf = directedGraph().outDegreeOf(node);
            if (outDegreeOf == 0) {
                targetNodes.add(node);
            }
        }
        return targetNodes;
    }

    private void addNode(Node node) {
        for (Node n : nodes) {
            if (n.getId().equals(node.getId())) {
                return;
            }
        }
        this.nodes.add(node);
    }

    private void addEdge(Edge edge) {
        for (Edge e : edges) {
            if (e.getSourceNodeId().equals(edge.getSourceNodeId()) && e.getTargetNodeId().equals(edge.getTargetNodeId())) {
                return;
            }
        }
        this.edges.add(edge);
    }

}
