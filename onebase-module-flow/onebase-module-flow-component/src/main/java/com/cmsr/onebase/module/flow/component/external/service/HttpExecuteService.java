package com.cmsr.onebase.module.flow.component.external.service;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * HTTP 执行服务
 * 封装 JDK HttpClient，处理网络请求与 SSRF 校验
 *
 * <p>核心特性：
 * <ul>
 *   <li>使用 JDK 17 原生 HttpClient（不依赖 Hutool）</li>
 *   <li>SSRF 防护：禁止访问内网地址</li>
 *   <li>支持 GET/POST/PUT/DELETE/PATCH 方法</li>
 *   <li>支持 JSON/Form/Raw 请求体</li>
 *   <li>智能响应解析（自动解析 JSON）</li>
 *   <li>请求耗时统计</li>
 * </ul>
 *
 * @author zhoulu
 * @since 2026-01-15
 */
@Slf4j
@Service
public class HttpExecuteService {

    private static final HttpClient SHARED_HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 执行 HTTP 请求（带重试机制）
     *
     * @param request HTTP 请求对象
     * @return HTTP 响应对象
     * @throws Exception 请求执行异常
     */
    public HttpServiceResponse execute(HttpRequest request) throws Exception {
        long startTime = System.currentTimeMillis();
        int retryCount = request.getRetry() != null ? request.getRetry() : 0;
        int maxAttempts = retryCount + 1;

        log.debug("开始执行 HTTP 请求 - 方法: {}, URL: {}, 超时: {}ms, 重试次数: {}",
                request.getMethod(), request.getUrl(), request.getTimeout(), retryCount);

        validateUrl(request.getUrl());

        Exception lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                HttpServiceResponse response = executeRequest(SHARED_HTTP_CLIENT, request, attempt + 1, maxAttempts);
                response.setDuration(System.currentTimeMillis() - startTime);
                return response;
            } catch (Exception e) {
                lastException = e;
                log.warn("HTTP 请求失败（第 {} 次尝试） - 方法: {}, URL: {}, 超时: {}ms, 异常: {}",
                        attempt + 1, request.getMethod(), request.getUrl(), request.getTimeout(), e.getMessage());

                if (attempt < maxAttempts - 1 && shouldRetry(e)) {
                    long backoffTime = calculateBackoff(attempt);
                    log.info("等待 {}ms 后进行第 {} 次重试...", backoffTime, attempt + 2);
                    Thread.sleep(backoffTime);
                } else {
                    break;
                }
            }
        }

        long totalDuration = System.currentTimeMillis() - startTime;
        log.error("HTTP 请求最终失败（尝试了 {} 次） - 方法: {}, URL: {}, 总耗时: {}ms",
                maxAttempts, request.getMethod(), request.getUrl(), totalDuration);
        throw lastException;
    }

    /**
     * 执行单次 HTTP 请求
     */
    private HttpServiceResponse executeRequest(HttpClient client, HttpRequest request,
            int attempt, int maxAttempts) throws Exception {
        String method = request.getMethod().toUpperCase();

        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.getUrl()))
                .timeout(Duration.ofMillis(request.getTimeout()));

        setRequestMethod(builder, method, request);

        if (request.getHeaders() != null) {
            for (HttpNodeData.Header header : request.getHeaders()) {
                builder.header(header.getKey(), header.getValue());
            }
        }

        java.net.http.HttpRequest httpRequest = builder.build();
        HttpResponse<String> jdkResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = jdkResponse.statusCode();

        if (shouldRetryByStatusCode(statusCode) && maxAttempts > 1) {
            throw new RuntimeException("HTTP " + statusCode + " - 服务器错误，需要重试");
        }

        HttpServiceResponse serviceResponse = new HttpServiceResponse();
        serviceResponse.setStatusCode(statusCode);
        serviceResponse.setHeaders(jdkResponse.headers().map());
        serviceResponse.setRawBody(jdkResponse.body());
        serviceResponse.setBody(parseResponseBody(jdkResponse.body(), jdkResponse.headers()));

        if (statusCode >= 400) {
            String errorDetail = extractErrorDetail(jdkResponse.body());
            serviceResponse.setErrorMessage(getErrorType(statusCode) + ": " + errorDetail);
            log.warn("HTTP 请求返回错误 - 方法: {}, URL: {}, 状态码: {}, 错误: {}",
                    method, request.getUrl(), statusCode, serviceResponse.getErrorMessage());
        }

        log.info("HTTP 请求成功 - 方法: {}, URL: {}, 状态码: {}, 尝试次数: {}/{}",
                method, request.getUrl(), statusCode, attempt, maxAttempts);

        return serviceResponse;
    }

    private void setRequestMethod(java.net.http.HttpRequest.Builder builder, String method, HttpRequest request) {
        switch (method) {
            case "GET" -> builder.GET();
            case "POST" -> builder.POST(buildBodyPublisher(request));
            case "PUT" -> builder.PUT(buildBodyPublisher(request));
            case "DELETE" -> builder.DELETE();
            case "PATCH" -> builder.method("PATCH", buildBodyPublisher(request));
            default -> throw new ConnectorConfigException("http", "不支持的 HTTP 方法: " + method);
        }
    }

    /**
     * 判断是否应该重试（基于异常类型）
     */
    private boolean shouldRetry(Exception e) {
        return e instanceof ConnectException
                || e instanceof HttpTimeoutException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof java.io.InterruptedIOException;
    }

    /**
     * 判断是否应该重试（基于 HTTP 状态码）
     */
    private boolean shouldRetryByStatusCode(int statusCode) {
        return statusCode >= 500 || statusCode == 429;
    }

    /**
     * 计算退避时间（指数退避）
     * 第1次重试: 100ms，第2次重试: 200ms，第3次重试: 400ms，最大 5 秒
     */
    private long calculateBackoff(int attempt) {
        return Math.min(100 * (1L << attempt), 5000);
    }

    /**
     * SSRF 防护校验 - 禁止访问内网地址
     */
    private void validateUrl(String url) throws ConnectorConfigException {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();

            if (host == null) {
                throw new ConnectorConfigException("http", "URL 格式无效: " + url);
            }

            if (isPrivateIP(host) || isLocalhost(host)) {
                throw new ConnectorConfigException("http", "禁止访问内网地址或 localhost: " + host);
            }
        } catch (IllegalArgumentException e) {
            throw new ConnectorConfigException("http", "URL 格式无效: " + url);
        }
    }

    private boolean isPrivateIP(String host) {
        String ip = host.split(":")[0];
        String ipv4Pattern = "^(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3})$";
        return ip.matches(ipv4Pattern);
    }

    private boolean isLocalhost(String host) {
        return "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host)
                || "::1".equals(host)
                || "0:0:0:0:0:0:0:1".equals(host);
    }

    /**
     * 构建请求体 Publisher
     */
    private java.net.http.HttpRequest.BodyPublisher buildBodyPublisher(HttpRequest request) {
        String bodyContent = request.getBodyContent();
        if (bodyContent == null || bodyContent.isEmpty()) {
            return java.net.http.HttpRequest.BodyPublishers.noBody();
        }
        return java.net.http.HttpRequest.BodyPublishers.ofString(bodyContent);
    }

    /**
     * 解析响应体 - 尝试解析为 JSON，失败则返回原始字符串
     */
    private Object parseResponseBody(String rawBody, java.net.http.HttpHeaders headers) {
        List<String> contentTypes = headers.map().getOrDefault("Content-Type", List.of());
        if (contentTypes.isEmpty()) {
            return rawBody;
        }

        String contentType = contentTypes.get(0).toLowerCase();
        if (!contentType.contains("application/json")) {
            return rawBody;
        }

        try {
            return objectMapper.readValue(rawBody, Object.class);
        } catch (Exception e) {
            log.warn("Failed to parse JSON body, return raw string: {}", e.getMessage());
            return rawBody;
        }
    }

    private String getErrorType(int statusCode) {
        if (statusCode == 429) {
            return "请求过于频繁";
        }
        if (statusCode >= 500) {
            return "服务器错误";
        }
        if (statusCode >= 400) {
            return "客户端错误";
        }
        return "未知错误";
    }

    private String extractErrorDetail(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "无响应体";
        }

        try {
            if (responseBody.trim().startsWith("{")) {
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, Map.class);
                Object message = errorMap.get("message");
                if (message != null) {
                    return message.toString();
                }
                Object error = errorMap.get("error");
                if (error != null) {
                    return error.toString();
                }
            }
        } catch (Exception e) {
            // 忽略解析错误
        }

        return responseBody.length() > 100
                ? responseBody.substring(0, 100) + "..."
                : responseBody;
    }
}