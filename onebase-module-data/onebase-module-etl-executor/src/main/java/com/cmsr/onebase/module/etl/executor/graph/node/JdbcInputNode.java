package com.cmsr.onebase.module.etl.executor.graph.node;

import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import lombok.Data;

@Data
public class JdbcInputNode extends Node {

    private JdbcInputConfig config;

}
