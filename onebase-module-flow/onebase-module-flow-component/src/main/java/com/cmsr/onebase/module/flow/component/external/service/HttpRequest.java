package com.cmsr.onebase.module.flow.component.external.service;

import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * HTTP 请求封装
 * 用于在 HttpExecuteService 中传递请求参数
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Data
public class HttpRequest implements Serializable {

    /**
     * 请求 URL（已解析变量）
     */
    private String url;

    /**
     * HTTP 请求方法
     * GET, POST, PUT, DELETE, PATCH
     */
    private String method;

    /**
     * 请求头（已解析变量）
     */
    private List<HttpNodeData.Header> headers;

    /**
     * 请求体类型
     * JSON, FORM, RAW
     */
    private String bodyType;

    /**
     * 请求体内容（已解析变量）
     */
    private String bodyContent;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retry;

    /**
     * 额外的查询参数
     * 用于动态添加到 URL 的 query string
     */
    private Map<String, String> queryParams;
}