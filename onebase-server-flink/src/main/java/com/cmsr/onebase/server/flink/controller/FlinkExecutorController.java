package com.cmsr.onebase.server.flink.controller;

import com.cmsr.onebase.module.etl.executor.DataPreview;
import com.cmsr.onebase.module.etl.executor.InputArgs;
import com.cmsr.onebase.module.etl.executor.WorkFlowExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(path = "/execute", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> execute(@RequestBody InputArgs inputArgs) throws JsonProcessingException {
        try (WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs, dataSource)) {
            executor.execute();
            Map result = Map.of("result", "success");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map result = Map.of("result", "fail", "message", ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping(path = "/preview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> preview(@RequestBody InputArgs inputArgs) {
        try (WorkFlowExecutor executor = new WorkFlowExecutor(inputArgs, dataSource)) {
            DataPreview preview = executor.preview();
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            Map result = Map.of("result", "fail", "message", ExceptionUtils.getRootCauseMessage(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


}
