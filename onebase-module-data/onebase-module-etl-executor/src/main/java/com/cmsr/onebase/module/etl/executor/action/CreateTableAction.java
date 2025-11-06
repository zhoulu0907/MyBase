package com.cmsr.onebase.module.etl.executor.action;

import org.apache.flink.table.api.TableEnvironment;

/**
 * @Author：huangjie
 * @Date：2025/11/6 14:35
 */
public interface CreateTableAction {

    void createTable(TableEnvironment tableEnv);

}
