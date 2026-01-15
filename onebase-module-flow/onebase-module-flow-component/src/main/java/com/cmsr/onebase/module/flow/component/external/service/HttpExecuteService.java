package com.cmsr.onebase.module.flow.component.external.service;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
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

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 共享的 HttpClient 实例
     * HttpClient 是线程安全的，可以在多个请求之间重用
     * 使用连接池提高性能
     */
    private volatile HttpClient sharedHttpClient;

    /**
     * 获取或创建共享的 HttpClient
     * 使用双重检查锁定确保线程安全
     */
    private HttpClient getSharedHttpClient() {
        if (sharedHttpClient == null) {
            synchronized (this) {
                if (sharedHttpClient == null) {
                    sharedHttpClient = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(10))  // 默认连接超时 10 秒
                            .build();
                    log.info("创建共享 HttpClient 实例");
                }
            }
        }
        return sharedHttpClient;
    }

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

        // 1. SSRF 防护校验（只验证一次）
        validateUrl(request.getUrl());

        // 2. 获取 HttpClient（使用共享实例）
        HttpClient client = getSharedHttpClient();

        // 3. 尝试发送请求（支持重试）
        Exception lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                HttpServiceResponse response = executeRequest(client, request, attempt + 1, maxAttempts);
                long duration = System.currentTimeMillis() - startTime;
                response.setDuration(duration);
                return response;
            } catch (Exception e) {
                lastException = e;

                // 增强的超时日志
                String errorType = getExceptionType(e);
                log.warn("HTTP 请求失败（第 {} 次尝试） - 方法: {}, URL: {}, " +
                                "超时配置: {}ms, 异常类型: {}, 异常: {}",
                        attempt + 1, request.getMethod(), request.getUrl(),
                        request.getTimeout(), errorType, e.getMessage());

                // 检查是否应该重试
                if (attempt < maxAttempts - 1 && shouldRetry(e)) {
                    long backoffTime = calculateBackoff(attempt);
                    log.info("等待 {}ms 后进行第 {} 次重试...", backoffTime, attempt + 2);
                    Thread.sleep(backoffTime);
                } else {
                    // 不重试或已达到最大重试次数
                    break;
                }
            }
        }

        // 所有尝试都失败
        long totalDuration = System.currentTimeMillis() - startTime;
        log.error("HTTP 请求最终失败（尝试了 {} 次） - 方法: {}, URL: {}, 总耗时: {}ms",
                maxAttempts, request.getMethod(), request.getUrl(), totalDuration);
        throw lastException;
    }

    /**
     * 获取异常类型描述
     */
    private String getExceptionType(Exception e) {
        if (e instanceof java.net.http.HttpTimeoutException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof java.net.ConnectException) {
            return "超时异常";
        } else if (e instanceof ConnectorConfigException) {
            return "配置异常";
        } else if (e instanceof RuntimeException && e.getMessage().contains("服务器错误")) {
            return "HTTP 错误";
        } else {
            return "未知异常";
        }
    }

    /**
     * 执行单次 HTTP 请求
     */
    private HttpServiceResponse executeRequest(HttpClient client, HttpRequest request,
            int attempt, int maxAttempts) throws Exception {
        // 构建请求
        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.getUrl()))
                .timeout(Duration.ofMillis(request.getTimeout()));

        // 设置请求方法
        String method = request.getMethod().toUpperCase();
        switch (method) {
            case "GET" -> builder.GET();
            case "POST" -> builder.POST(buildBodyPublisher(request));
            case "PUT" -> builder.PUT(buildBodyPublisher(request));
            case "DELETE" -> builder.DELETE();
            case "PATCH" -> {
                builder.method("PATCH", buildBodyPublisher(request));
            }
            default -> throw new ConnectorConfigException("http", "不支持的 HTTP 方法: " + method);
        }

        // 设置请求头
        if (request.getHeaders() != null) {
            for (HttpNodeData.Header header : request.getHeaders()) {
                builder.header(header.getKey(), header.getValue());
            }
        }

        // 发送请求
        java.net.http.HttpRequest httpRequest = builder.build();
        HttpResponse<String> jdkResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // 构建响应对象
        HttpServiceResponse serviceResponse = new HttpServiceResponse();
        serviceResponse.setStatusCode(jdkResponse.statusCode());
        serviceResponse.setHeaders(jdkResponse.headers().map());
        serviceResponse.setRawBody(jdkResponse.body());

        // 解析响应体
        serviceResponse.setBody(parseResponseBody(jdkResponse.body(), jdkResponse.headers()));

        // 设置错误信息（如果是错误响应）
        int statusCode = jdkResponse.statusCode();
        if (statusCode >= 400) {
            String errorType = getErrorType(statusCode);
            String errorDetail = extractErrorDetail(jdkResponse.body());
            serviceResponse.setErrorMessage(String.format("%s: %s", errorType, errorDetail));
            log.warn("HTTP 请求返回错误 - 方法: {}, URL: {}, 状态码: {}, 错误: {}",
                    method, request.getUrl(), statusCode, serviceResponse.getErrorMessage());
        }

        // 检查是否需要重试（基于 HTTP 状态码）
        if (shouldRetryByStatusCode(statusCode) && maxAttempts > 1) {
            throw new RuntimeException("HTTP " + statusCode + " - 服务器错误，需要重试");
        }

        log.info("HTTP 请求成功 - 方法: {}, URL: {}, 状态码: {}, 尝试次数: {}/{}, 耗时: {}ms",
                method, request.getUrl(), statusCode, attempt, maxAttempts,
                serviceResponse.getDuration() != null ? serviceResponse.getDuration() : 0);

        return serviceResponse;
    }

    /**
     * 判断是否应该重试（基于异常类型）
     */
    private boolean shouldRetry(Exception e) {
        // 网络相关异常可以重试
        if (e instanceof java.net.ConnectException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof java.io.InterruptedIOException) {
            return true;
        }

        // HTTP 请求失败
        if (e instanceof java.net.http.HttpTimeoutException) {
            return true;
        }

        // 其他异常不重试
        return false;
    }

    /**
     * 判断是否应该重试（基于 HTTP 状态码）
     */
    private boolean shouldRetryByStatusCode(int statusCode) {
        // 5xx 服务器错误可以重试
        // 429 Too Many Requests 可以重试
        // 4xx 客户端错误不重试（除了 429）
        return statusCode >= 500 || statusCode == 429;
    }

    /**
     * 计算退避时间（指数退避）
     * 第1次重试: 100ms
     * 第2次重试: 200ms
     * 第3次重试: 400ms
     * ...
     */
    private long calculateBackoff(int attempt) {
        return Math.min(100 * (1L << attempt), 5000);  // 最大 5 秒
    }

    /**
     * SSRF 防护校验
     * 禁止访问内网地址
     *
     * @param url 请求 URL
     * @throws ConnectorConfigException 如果 URL 为内网地址
     */
    private void validateUrl(String url) throws ConnectorConfigException {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();

            if (host == null) {
                throw new ConnectorConfigException("http", "URL 格式无效: " + url);
            }

            // 检查是否为内网地址
            if (isPrivateIP(host) || isLocalhost(host)) {
                throw new ConnectorConfigException("http",
                        "禁止访问内网地址或 localhost: " + host);
            }
        } catch (IllegalArgumentException e) {
            throw new ConnectorConfigException("http", "URL 格式无效: " + url);
        }
    }

    /**
     * 检查是否为内网 IP
     */
    private boolean isPrivateIP(String host) {
        // 提取 IP 地址（去除端口号）
        String ip = host.split(":")[0];

        // IPv4 私有地址段
        // 10.0.0.0 - 10.255.255.255
        // 172.16.0.0 - 172.31.255.255
        // 192.168.0.0 - 192.168.255.255
        String ipv4Pattern = "^(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3})$";
        return ip.matches(ipv4Pattern);
    }

    /**
     * 检查是否为 localhost
     */
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
     * 解析响应体
     * 尝试解析为 JSON，失败则返回原始字符串
     */
    private Object parseResponseBody(String rawBody, java.net.http.HttpHeaders headers) {
        // 1. 检查 Content-Type
        List<String> contentTypes = headers.map().getOrDefault("Content-Type", List.of());
        if (contentTypes.isEmpty()) {
            return rawBody;
        }

        String contentType = contentTypes.get(0).toLowerCase();
        if (!contentType.contains("application/json")) {
            return rawBody;  // 非 JSON，直接返回字符串
        }

        // 2. 尝试解析 JSON
        try {
            return objectMapper.readValue(rawBody, Object.class);
        } catch (Exception e) {
            log.warn("Failed to parse JSON body, return raw string: {}", e.getMessage());
            return rawBody;
        }
    }

    /**
     * 获取错误类型描述
     */
    private String getErrorType(int statusCode) {
        if (statusCode >= 400 && statusCode < 500) {
            return "客户端错误";
        } else if (statusCode >= 500 && statusCode < 600) {
            return "服务器错误";
        } else if (statusCode == 429) {
            return "请求过于频繁";
        } else {
            return "未知错误";
        }
    }

    /**
     * 提取错误详情
     * 优先从 JSON 响应中提取 message 字段
     */
    private String extractErrorDetail(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "无响应体";
        }

        // 尝试解析 JSON 并提取 message 或 error 字段
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

        // 如果响应体很长，截取前 100 个字符
        if (responseBody.length() > 100) {
            return responseBody.substring(0, 100) + "...";
        }
        return responseBody;
    }
}