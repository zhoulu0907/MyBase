package com.cmsr.onebase.module.etl.executor;

import org.junit.jupiter.api.Test;

/**
 * @Author：huangjie
 * @Date：2025/11/9 8:29
 */
class WorkFlowExecutorTest {

    @Test
    void execute() throws Exception {
        InputArgs inputArgs = new InputArgs();
        inputArgs.setJdbcDriverClass("org.postgresql.Driver");
        inputArgs.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        inputArgs.setJdbcUserName("postgres");
        inputArgs.setJdbcPassword("onebase@2025");
        inputArgs.setWorkflowId(131802582152183808L);
        WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs);
        executor.execute();
        executor.close();
    }

}