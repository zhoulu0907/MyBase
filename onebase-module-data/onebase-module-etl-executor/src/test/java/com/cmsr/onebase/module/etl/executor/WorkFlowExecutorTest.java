package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/9 8:29
 */
class WorkFlowExecutorTest {

    @Test
    void execute() throws Exception {
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setJdbcDriverClass("org.postgresql.Driver");
        executeRequest.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        executeRequest.setJdbcUserName("postgres");
        executeRequest.setJdbcPassword("onebase@2025");
        executeRequest.setWorkflowId(134298817148485632L);
        //executeRequest.setWorkflowId(144840848755687424L);
        WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest);
        executor.execute();
        executor.close();
    }

    @Test
    void execute2() throws Exception {
        String json = IOUtils.resourceToString("/test.json", StandardCharsets.UTF_8);
        System.out.println(json);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setJdbcDriverClass("org.postgresql.Driver");
        executeRequest.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        executeRequest.setJdbcUserName("postgres");
        executeRequest.setJdbcPassword("onebase@2025");
        executeRequest.setPreviewWorkflow(json);
        executeRequest.setPreviewNodeId("sql_502aac004e5749de9de5c03f3e0d5e8d");
        WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest);
        DataPreview preview = executor.nodePreview();
        System.out.println(preview);
        executor.close();
    }

    @Test
    void execute3() throws Exception {
        String json = IOUtils.resourceToString("/test.json", StandardCharsets.UTF_8);
        System.out.println(json);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setJdbcDriverClass("org.postgresql.Driver");
        executeRequest.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        executeRequest.setJdbcUserName("postgres");
        executeRequest.setJdbcPassword("onebase@2025");
        executeRequest.setPreviewWorkflow(json);
        executeRequest.setPreviewNodeId("sql_502aac004e5749de9de5c03f3e0d5e8d");
        WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest);
        List<ColumnDefine> columnDefines = executor.nodeColumns();
        System.out.println(columnDefines);
        executor.close();
    }
}