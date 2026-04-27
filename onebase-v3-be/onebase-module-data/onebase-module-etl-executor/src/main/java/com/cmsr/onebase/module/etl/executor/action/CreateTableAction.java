package com.cmsr.onebase.module.etl.executor.action;

import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import org.apache.flink.table.api.TableEnvironment;

/**
 * 创建表
 * <p>
 * 1、使用 tableEnv.createTable
 * 2、使用 CREATE TABLE MyTable ( ) WITH ( ) 语句
 *
 * @Author：huangjie
 * @Date：2025/11/6 14:35
 */
public interface CreateTableAction {

    void createTable(TableEnvironment tableEnv, WorkflowGraph graph);

}
