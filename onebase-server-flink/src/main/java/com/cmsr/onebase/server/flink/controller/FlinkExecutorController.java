package com.cmsr.onebase.server.flink.controller;

import com.cmsr.onebase.module.etl.executor.DataPreview;
import com.cmsr.onebase.module.etl.executor.InputArgs;
import com.cmsr.onebase.module.etl.executor.WorkFlowExecutor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/10/17 14:47
 */
@Setter
@RequestMapping("/flink")
@RestController
public class FlinkExecutorController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/execute")
    public ResponseEntity<Map> runFlow(@RequestBody InputArgs inputArgs) {
        try {
            WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs, dataSource);
            executor.execute();
            return ResponseEntity.ok(Map.of("result", "success"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("result", "fail", "message", e.getMessage()));
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<Object> preview(@RequestBody InputArgs inputArgs) {
        try {
            WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs, dataSource);
            DataPreview preview = executor.preview();
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("result", "fail", "message", e.getMessage()));
        }
    }

}
