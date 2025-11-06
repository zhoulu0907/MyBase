package com.cmsr.onebase.module.etl.executor.action;

import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

/**
 * @Author：huangjie
 * @Date：2025/11/6 14:37
 */
public interface ExecuteSqlAction {

    TableResult executeSql(TableEnvironment tableEnv);

}
