package com.cmsr.onebase.plugin.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求
 * <p>
 * 封装HTTP请求信息，供自定义HTTP处理器使用。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public class HttpRequest {

    /**
     * 请求方法（GET, POST, PUT, DELETE等）
     */
    private String method;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 查询参数
     */
    private Map<String, List<String>> queryParams;

    /**
     * 请求头
     */
    private Map<String, List<String>> headers;

    /**
     * 路径变量
     */
    private Map<String, String> pathVariables;

    /**
     * 请求体（原始字符串）
     */
    private String body;

    /**
     * 请求体（解析后的对象）
     */
    private Object parsedBody;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    public HttpRequest() {
    }

    // ==================== 便捷获取参数方法 ====================

    /**
     * 获取单个查询参数值
     *
     * @param name 参数名
     * @return 参数值，不存在返回null
     */
    public String getQueryParam(String name) {
        if (queryParams == null) {
            return null;
        }
        List<String> values = queryParams.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    /**
     * 获取查询参数值列表
     *
     * @param name 参数名
     * @return 参数值列表
     */
    public List<String> getQueryParams(String name) {
        if (queryParams == null) {
            return Collections.emptyList();
        }
        List<String> values = queryParams.get(name);
        return values != null ? values : Collections.emptyList();
    }

    /**
     * 获取单个请求头值
     *
     * @param name 请求头名称
     * @return 请求头值，不存在返回null
     */
    public String getHeader(String name) {
        if (headers == null) {
            return null;
        }
        // 请求头名称不区分大小写
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                List<String> values = entry.getValue();
                return (values != null && !values.isEmpty()) ? values.get(0) : null;
            }
        }
        return null;
    }

    /**
     * 获取路径变量
     *
     * @param name 变量名
     * @return 变量值，不存在返回null
     */
    public String getPathVariable(String name) {
        if (pathVariables == null) {
            return null;
        }
        return pathVariables.get(name);
    }

    /**
     * 获取解析后的请求体
     *
     * @param <T> 目标类型
     * @return 请求体对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getBodyAs() {
        return (T) parsedBody;
    }

    /**
     * 获取Content-Type
     *
     * @return Content-Type值
     */
    public String getContentType() {
        return getHeader("Content-Type");
    }

    /**
     * 是否是JSON请求
     *
     * @return true表示是JSON请求
     */
    public boolean isJson() {
        String contentType = getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    // ==================== Getter/Setter ====================

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getParsedBody() {
        return parsedBody;
    }

    public void setParsedBody(Object parsedBody) {
        this.parsedBody = parsedBody;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
