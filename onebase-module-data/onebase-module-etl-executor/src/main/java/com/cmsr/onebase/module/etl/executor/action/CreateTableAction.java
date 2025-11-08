package com.cmsr.onebase.module.etl.executor.action;

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

    void createTable();

}
