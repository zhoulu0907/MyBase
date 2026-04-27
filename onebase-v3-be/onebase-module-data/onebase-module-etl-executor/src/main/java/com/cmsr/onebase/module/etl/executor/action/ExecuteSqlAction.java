package com.cmsr.onebase.module.etl.executor.action;

import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

/**
 * 执行 SQL 语句，如
 * insert into sales_top (`name`,`year`,`sale`) select `name`,0,`sum_sale` from table2
 *
 * @Author：huangjie
 * @Date：2025/11/6 14:37
 */
public interface ExecuteSqlAction {

    TableResult executeSql(TableEnvironment tableEnv, WorkflowGraph graph);

}
