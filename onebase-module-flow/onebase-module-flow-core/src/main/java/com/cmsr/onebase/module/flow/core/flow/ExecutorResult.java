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

    private Map<String, Object> outputParams;
}
