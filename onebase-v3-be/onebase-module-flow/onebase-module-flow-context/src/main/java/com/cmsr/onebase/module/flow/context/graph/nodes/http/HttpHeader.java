package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;

/**
 * HTTP请求头定义
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class HttpHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Header名
     */
    private String name;

    /**
     * Header值（支持变量）
     */
    private String value;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否必填
     */
    private Boolean required;
}
