package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.SqlConfig;
import com.cmsr.onebase.module.etl.executor.action.SqlQueryAction;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

@Slf4j
@ToString(callSuper = true)
public class SqlNode extends Node<SqlConfig> implements SqlQueryAction {

    @Override
    public Table sqlQuery(TableEnvironment tableEnv, WorkflowGraph graph) {
        log.info("执行SQL: {}", this.config.getSqlValue());
        return tableEnv.sqlQuery(this.config.getSqlValue());
    }
}
