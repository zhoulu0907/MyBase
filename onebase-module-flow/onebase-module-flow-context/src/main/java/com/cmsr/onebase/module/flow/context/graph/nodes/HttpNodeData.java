package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * HTTP 请求节点数据模型
 * 支持动态配置的 HTTP 请求节点，用于在流程中调用外部 API
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NodeType("api_http")
public class HttpNodeData extends NodeData implements Serializable {

    /**
     * 请求 URL
     * 支持变量替换，如: http://api.com/users/${userId}
     */
    private String url;

    /**
     * HTTP 请求方法
     * 支持: GET, POST, PUT, DELETE, PATCH
     */
    private String method;

    /**
     * 请求头列表
     * 每个 Header 包含 key 和 value
     */
    private List<Header> headers;

    /**
     * 请求体类型
     * 支持: JSON (application/json), FORM (application/x-www-form-urlencoded), RAW (text/plain)
     */
    private String bodyType;

    /**
     * 请求体内容
     * 支持变量替换，如: {"name": "${userName}", "age": ${userAge}}
     */
    private String bodyContent;

    /**
     * 超时时间（毫秒）
     * 默认: 5000ms
     */
    private Integer timeout;

    /**
     * 重试次数
     * 默认: 0（不重试）
     */
    private Integer retry;

    /**
     * 请求头内部类
     */
    @Data
    public static class Header implements Serializable {
        /**
         * 请求头名称
         * 如: Content-Type, Authorization
         */
        private String key;

        /**
         * 请求头值
         * 支持变量替换，如: Bearer ${token}
         */
        private String value;
    }
}