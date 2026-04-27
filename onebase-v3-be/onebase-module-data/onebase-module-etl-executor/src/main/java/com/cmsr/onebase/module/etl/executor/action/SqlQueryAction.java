package com.cmsr.onebase.module.etl.executor.action;

import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

/**
 * 执行查询语句，如
 * select `name`, sum(`sale`) as sum_sale from sales group by `name`
 *
 * @Author：huangjie
 * @Date：2025/11/7 13:37
 */
public interface SqlQueryAction {

    Table sqlQuery(TableEnvironment tableEnv, WorkflowGraph graph);

}
