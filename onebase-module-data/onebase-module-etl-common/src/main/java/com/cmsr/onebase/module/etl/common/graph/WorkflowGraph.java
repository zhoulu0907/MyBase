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

    /**
     * 获取整个图从开始节点到结束节点路径上的所有节点
     * (排除游离节点，且按拓扑顺序返回)
     * 规则：
     * 1. Start 节点必须是 Input 类型，且有出度
     * 2. End 节点必须是 Output 类型
     */
    public List<Node> getNodesFromStartToEnd() {
        // 获取起点（排除孤立节点：即同时也是终点的节点，除非图只包含一个节点且允许单节点流）
        // 这里策略是：如果一个节点既是起点又是终点（即孤立节点），则视为无效路径节点排除
        Set<Node> startNodes = new HashSet<>();
        for (Node node : findStartNodes()) {
            if (isInputNode(node) && directedGraph().outDegreeOf(node) > 0) {
                startNodes.add(node);
            }
        }

        Set<Node> endNodes = new HashSet<>();
        for (Node node : findEndNodes()) {
            if (isOutputNode(node)) {
                endNodes.add(node);
            }
        }

        if (startNodes.isEmpty() || endNodes.isEmpty()) {
            return new ArrayList<>();
        }

        AllDirectedPaths<Node, Edge> allPathsFinder = new AllDirectedPaths<>(directedGraph());
        List<GraphPath<Node, Edge>> allPaths = allPathsFinder.getAllPaths(
                startNodes,
                endNodes,
                true,
                Math.max(1, directedGraph().vertexSet().size())
        );

        Set<Node> pathNodes = new HashSet<>();
        for (GraphPath<Node, Edge> path : allPaths) {
            pathNodes.addAll(path.getVertexList());
        }

        // 结合拓扑排序结果，返回有序的节点列表
        List<Node> allNodes = iterateNodes();
        List<Node> result = new ArrayList<>();
        for (Node node : allNodes) {
            if (pathNodes.contains(node)) {
                result.add(node);
            }
        }
        return result;
    }

    private boolean isInputNode(Node node) {
        return node.getType() != null && node.getType().toLowerCase().contains("input");
    }

    private boolean isOutputNode(Node node) {
        return node.getType() != null && node.getType().toLowerCase().contains("output");
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
