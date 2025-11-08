package com.cmsr.onebase.module.etl.executor.action;

import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import lombok.Setter;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @Author：huangjie
 * @Date：2025/11/8 20:30
 */
@Setter
public abstract class AbstractAction {

    protected TableEnvironment tableEnv;

    protected WorkflowGraph graph;

    protected Node node;

}
