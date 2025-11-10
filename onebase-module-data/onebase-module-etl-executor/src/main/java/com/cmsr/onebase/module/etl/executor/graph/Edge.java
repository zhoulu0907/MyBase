package com.cmsr.onebase.module.etl.executor.graph;

import lombok.Data;

@Data
public class Edge {

    private String sourceNodeId;

    private String targetNodeId;
}
