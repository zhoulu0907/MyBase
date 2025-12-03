package com.cmsr.onebase.server.flink.controller;

import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.executor.WorkFlowExecutor;
import com.cmsr.onebase.server.flink.OneBaseServerFlinkApplication;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author：huangjie
 * @Date：2025/12/3 14:32
 */

@SpringBootTest(classes = OneBaseServerFlinkApplication.class)
class FlinkExecutorControllerTest {

    @Autowired
    private FlinkExecutorController flinkExecutorController;

    @Autowired
    private DataSource dataSource;

    @Test
    void preview001() throws Exception {
        String json = IOUtils.resourceToString("/workflow.json", StandardCharsets.UTF_8);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setPreviewWorkflow(json);
        executeRequest.setPreviewNodeId("sql_7577ef7b741a4b3085bdaadf7702b094");
        WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest, dataSource);
        DataPreview preview = executor.nodePreview();
        System.out.println(preview);
    }

    @Test
    void preview() throws IOException {
        String json = IOUtils.resourceToString("/workflow.json", StandardCharsets.UTF_8);
        //System.out.println( json);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setPreviewWorkflow(json);
        executeRequest.setPreviewNodeId("sql_7577ef7b741a4b3085bdaadf7702b094");
        ResponseEntity<Object> preview = flinkExecutorController.preview(executeRequest);
        System.out.println(preview);
    }
}