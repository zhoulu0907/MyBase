package com.cmsr.onebase.module.flow.core.flow;

import lombok.Data;

import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/10/11 16:11
 */
@Data
public class ExecutorResult {

    private boolean success;

    private String code;

    private String message;

    private Exception cause;

    private boolean executionEnd;

    private String executionUuid;

    private String executionEndNodeType;

    private String executionEndNodeTag;

    private Map<String, Object> outputParams;

    public static ExecutorResult error(String message) {
        ExecutorResult result = new ExecutorResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static ExecutorResult error(String message, Exception cause) {
        ExecutorResult result = new ExecutorResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setCause(cause);
        return result;
    }
}
