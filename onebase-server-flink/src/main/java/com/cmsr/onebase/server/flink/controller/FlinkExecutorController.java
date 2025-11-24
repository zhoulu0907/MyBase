package com.cmsr.onebase.server.flink.controller;

import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
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
import java.util.List;
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
    public ResponseEntity<Map> execute(@RequestBody ExecuteRequest executeRequest) throws JsonProcessingException {
        try (WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest, dataSource)) {
            executor.execute();
            Map result = Map.of("result", "success");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map result = Map.of("result", "fail", "message", ExceptionUtils.getStackTrace(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping(path = "/preview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> preview(@RequestBody ExecuteRequest executeRequest) {
        try (WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest, dataSource)) {
            DataPreview preview = executor.nodePreview();
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            Map result = Map.of("result", "fail", "message", ExceptionUtils.getRootCauseMessage(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping(path = "/columns", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> columns(@RequestBody ExecuteRequest executeRequest) {
        try (WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest, dataSource)) {
            List<ColumnDefine> columns = executor.nodeColumns();
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            Map result = Map.of("result", "fail", "message", ExceptionUtils.getRootCauseMessage(e));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


}
