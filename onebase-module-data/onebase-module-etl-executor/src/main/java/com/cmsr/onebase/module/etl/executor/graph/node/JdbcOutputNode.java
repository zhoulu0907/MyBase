package com.cmsr.onebase.module.etl.executor.graph.node;

import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcOutputConfig;
import lombok.Data;

import java.util.List;

@Data
public class JdbcOutputNode extends Node {

    private List<String> sourceNodeIds;

    private JdbcOutputConfig config;

}
