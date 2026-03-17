package com.cmsr.onebase.server.flink.controller;

import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.executor.WorkFlowExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            String friendlyMessage = buildFriendlyErrorMessage(e);
            Map result = Map.of("result", "fail", "message", friendlyMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping(path = "/columns", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> columns(@RequestBody ExecuteRequest executeRequest) {
        try (WorkFlowExecutor executor = new WorkFlowExecutor(executeRequest, dataSource)) {
            List<ColumnDefine> columns = executor.nodeColumns();
            return ResponseEntity.ok(columns);
        } catch (Exception e) {
            String friendlyMessage = buildFriendlyErrorMessage(e);
            Map result = Map.of("result", "fail", "message", friendlyMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 构建友好的错误提示信息
     *
     * @param e 异常对象
     * @return 友好的错误提示信息
     */
    private String buildFriendlyErrorMessage(Exception e) {
        log.error("buildFriendlyErrorMessage ",e);
        String rootMessage = ExceptionUtils.getRootCauseMessage(e);
        if (rootMessage == null) {
            return "执行失败，请检查节点配置";
        }
        // 处理SQL语法错误相关的异常
        if (rootMessage.contains("SqlValidatorException") || rootMessage.contains("SqlParseException")) {
            // 列不存在
            if (rootMessage.contains("not found in any table")) {
                String columnName = extractColumnName(rootMessage, "Column '([^']+)' not found");
                if (columnName != null) {
                    return "SQL语法错误：列 '" + columnName + "' 不存在，请检查SQL语句中的字段名称是否正确";
                }
                return "SQL语法错误：引用的列不存在，请检查SQL语句中的字段名称";
            }
            // 表不存在
            if (rootMessage.contains("Table") && rootMessage.contains("not found")) {
                return "SQL语法错误：引用的表不存在，请检查SQL语句中的表名称";
            }
            // SQL语句为空或不完整
            if (rootMessage.contains("Encountered") || rootMessage.contains("parse")) {
                return "SQL语法错误：SQL语句不完整或格式错误，请检查SQL语句是否正确";
            }
            return "SQL语法错误：请检查SQL语句是否正确 - " + extractSimpleMessage(rootMessage);
        }
        // 空指针异常
        if (rootMessage.contains("NullPointerException")) {
            return "节点配置不完整，请检查所有必填项是否已正确配置";
        }
        // 其他异常返回简化的错误信息
        return extractSimpleMessage(rootMessage);
    }

    /**
     * 从错误信息中提取列名
     */
    private String extractColumnName(String message, String pattern) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(message);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * 提取简化的错误信息
     */
    private String extractSimpleMessage(String rootMessage) {
        // 移除异常类名前缀
        if (rootMessage.contains(": ")) {
            int colonIndex = rootMessage.indexOf(": ");
            return rootMessage.substring(colonIndex + 2);
        }
        return rootMessage;
    }
}
