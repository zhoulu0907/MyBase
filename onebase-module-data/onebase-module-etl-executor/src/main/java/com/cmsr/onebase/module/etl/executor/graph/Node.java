package com.cmsr.onebase.module.etl.executor.graph;

import lombok.Data;

@Data
public class Node<T extends NodeConfig> {

    protected String id;

    protected String type;

    protected T config;

    protected NodeOutput output;

}
