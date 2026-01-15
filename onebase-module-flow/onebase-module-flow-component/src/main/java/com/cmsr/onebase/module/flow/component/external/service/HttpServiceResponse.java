package com.cmsr.onebase.module.flow.component.external.service;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * HTTP 响应封装
 * 包含完整的 HTTP 响应信息
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Data
public class HttpServiceResponse implements Serializable {

    /**
     * HTTP 状态码
     * 示例: 200, 404, 500
     */
    private Integer statusCode;

    /**
     * 响应头
     * Key: 响应头名称
     * Value: 响应头值列表（因为可能有多个同名 header）
     * 示例: {"Content-Type": ["application/json"], "Set-Cookie": ["session=abc", "token=xyz"]}
     */
    private Map<String, List<String>> headers;

    /**
     * 响应体（解析后）
     * - 如果是 JSON，解析为 Map 或 List
     * - 否则为原始字符串
     */
    private Object body;

    /**
     * 原始响应体
     * 始终为字符串，未经解析
     */
    private String rawBody;

    /**
     * 请求耗时（毫秒）
     */
    private Long duration;

    /**
     * 错误信息（如果请求失败）
     */
    private String errorMessage;

    /**
     * 是否成功
     * 基于 HTTP 状态码判断（2xx 为成功）
     */
    public boolean isSuccess() {
        return statusCode != null && statusCode >= 200 && statusCode < 300;
    }
}