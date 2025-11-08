package com.cmsr.onebase.module.etl.executor.graph;

import lombok.Data;

@Data
public class Node {

    private String id;

    private String type;

    private NodeOutput output;

}
