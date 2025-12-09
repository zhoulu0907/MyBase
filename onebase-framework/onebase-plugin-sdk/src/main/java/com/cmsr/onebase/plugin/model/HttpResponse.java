package com.cmsr.onebase.plugin.model;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP响应
 * <p>
 * 封装HTTP响应信息，供自定义HTTP处理器返回结果。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public class HttpResponse {

    /**
     * HTTP状态码
     */
    private int status;

    /**
     * 响应头
     */
    private Map<String, String> headers;

    /**
     * 响应体
     */
    private Object body;

    /**
     * Content-Type
     */
    private String contentType;

    public HttpResponse() {
        this.status = 200;
        this.headers = new HashMap<>();
        this.contentType = "application/json";
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功响应
     *
     * @return HTTP响应
     */
    public static HttpResponse ok() {
        return new HttpResponse();
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param body 响应体
     * @return HTTP响应
     */
    public static HttpResponse ok(Object body) {
        HttpResponse response = new HttpResponse();
        response.setBody(body);
        return response;
    }

    /**
     * 创建错误响应
     *
     * @param status  状态码
     * @param message 错误信息
     * @return HTTP响应
     */
    public static HttpResponse error(int status, String message) {
        HttpResponse response = new HttpResponse();
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("code", status);
        error.put("message", message);
        response.setBody(error);
        return response;
    }

    /**
     * 创建400错误响应
     *
     * @param message 错误信息
     * @return HTTP响应
     */
    public static HttpResponse badRequest(String message) {
        return error(400, message);
    }

    /**
     * 创建401错误响应
     *
     * @param message 错误信息
     * @return HTTP响应
     */
    public static HttpResponse unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 创建403错误响应
     *
     * @param message 错误信息
     * @return HTTP响应
     */
    public static HttpResponse forbidden(String message) {
        return error(403, message);
    }

    /**
     * 创建404错误响应
     *
     * @param message 错误信息
     * @return HTTP响应
     */
    public static HttpResponse notFound(String message) {
        return error(404, message);
    }

    /**
     * 创建500错误响应
     *
     * @param message 错误信息
     * @return HTTP响应
     */
    public static HttpResponse serverError(String message) {
        return error(500, message);
    }

    /**
     * 创建重定向响应
     *
     * @param url 重定向URL
     * @return HTTP响应
     */
    public static HttpResponse redirect(String url) {
        HttpResponse response = new HttpResponse();
        response.setStatus(302);
        response.addHeader("Location", url);
        return response;
    }

    // ==================== 链式设置方法 ====================

    /**
     * 设置状态码
     *
     * @param status 状态码
     * @return this
     */
    public HttpResponse status(int status) {
        this.status = status;
        return this;
    }

    /**
     * 设置响应体
     *
     * @param body 响应体
     * @return this
     */
    public HttpResponse body(Object body) {
        this.body = body;
        return this;
    }

    /**
     * 添加响应头
     *
     * @param name  响应头名称
     * @param value 响应头值
     * @return this
     */
    public HttpResponse header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * 设置Content-Type
     *
     * @param contentType Content-Type
     * @return this
     */
    public HttpResponse contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 设置为JSON类型
     *
     * @return this
     */
    public HttpResponse asJson() {
        this.contentType = "application/json";
        return this;
    }

    /**
     * 设置为文本类型
     *
     * @return this
     */
    public HttpResponse asText() {
        this.contentType = "text/plain";
        return this;
    }

    /**
     * 设置为HTML类型
     *
     * @return this
     */
    public HttpResponse asHtml() {
        this.contentType = "text/html";
        return this;
    }

    // ==================== 常规方法 ====================

    /**
     * 添加响应头
     *
     * @param name  响应头名称
     * @param value 响应头值
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    // ==================== Getter/Setter ====================

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
