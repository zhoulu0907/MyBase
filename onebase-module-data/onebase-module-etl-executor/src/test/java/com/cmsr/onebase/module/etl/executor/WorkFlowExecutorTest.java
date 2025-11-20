package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

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
        inputArgs.setWorkflowId(143523908702208000L);
        WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs);
        executor.execute();
        executor.close();
    }

    @Test
    void execute2() throws Exception {
        String json = IOUtils.resourceToString("/test.json", StandardCharsets.UTF_8);
        System.out.println(json);
        InputArgs inputArgs = new InputArgs();
        inputArgs.setJdbcDriverClass("org.postgresql.Driver");
        inputArgs.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        inputArgs.setJdbcUserName("postgres");
        inputArgs.setJdbcPassword("onebase@2025");
        inputArgs.setPreviewWorkflow(json);
        inputArgs.setPreviewNodeId("union_7343dec4be494eea94606d3bee6ff967");
        WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs);
        DataPreview preview = executor.preview();
        System.out.println(preview);
        executor.close();
    }
}