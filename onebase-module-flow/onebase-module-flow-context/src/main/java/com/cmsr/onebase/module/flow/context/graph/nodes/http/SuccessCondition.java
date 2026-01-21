package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;

/**
 * 成功条件定义
 *
 * <p>用于定义HTTP请求成功的条件
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class SuccessCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功条件表达式（JSONPath）
     */
    private String expression;

    /**
     * 期望的HTTP状态码
     */
    private Integer expectStatusCode;

    /**
     * 响应体路径
     */
    private String responseBodyPath;

    /**
     * 描述
     */
    private String description;
}
