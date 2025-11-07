package com.cmsr.onebase.module.etl.executor.graph;

import lombok.Data;

import java.util.List;

@Data
public class WorkflowGraph {

    private List<Node> nodes;

    private List<Edge> edges;

}
