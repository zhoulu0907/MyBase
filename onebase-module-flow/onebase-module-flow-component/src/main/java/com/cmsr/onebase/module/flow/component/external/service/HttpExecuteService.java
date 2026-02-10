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

    /**
     * 共享的 HttpClient 实例
     *
     * <p>设计说明：
     * - 使用 static final 确保线程安全且全局唯一
     * - 连接超时设置为 10 秒，适用于大多数场景
     * - HttpClient 本身是线程安全的，可以在多个请求之间重用
     * - 重用实例可以利用连接池，提高性能
     */
    private static final HttpClient SHARED_HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * JSON 序列化/反序列化工具
     * 用于解析 HTTP 响应中的 JSON 数据
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 执行 HTTP 请求（带重试机制）
     *
     * <p>这是服务的主入口方法，负责协调整个请求执行流程：
     * 1. 记录开始时间和计算最大尝试次数
     * 2. 进行 SSRF 防护校验
     * 3. 在循环中尝试执行请求（支持重试）
     * 4. 根据异常类型决定是否重试
     * 5. 计算并应用指数退避延迟
     * 6. 所有尝试失败后抛出最后一个异常
     *
     * <p>重试策略：
     * - 网络异常（连接超时、读取超时）会触发重试
     * - HTTP 5xx 和 429 状态码会触发重试
     * - 使用指数退避算法：100ms, 200ms, 400ms, ...（最大 5 秒）
     *
     * @param request HTTP 请求对象，包含 URL、方法、头、体等配置
     * @return HTTP 响应对象，包含状态码、头、体等信息
     * @throws Exception 当所有尝试都失败时抛出最后一个异常
     */
    public HttpServiceResponse execute(HttpRequest request) throws Exception {
        // 记录请求开始时间，用于计算总耗时
        long startTime = System.currentTimeMillis();

        // 计算最大尝试次数（重试次数 + 1）
        int retryCount = request.getRetry() != null ? request.getRetry() : 0;
        int maxAttempts = retryCount + 1;

        // 记录调试日志，方便排查问题（带追踪信息）
        log.info("[FLOW-TRACE] 开始执行HTTP请求: processId={}, traceId={}, executionUuid={}, nodeId={}, method={}, url={}, timeout={}ms, retry={}",
                request.getProcessId(), request.getTraceId(), request.getExecutionUuid(), request.getNodeId(),
                request.getMethod(), request.getUrl(), request.getTimeout(), retryCount);

        // SSRF 防护校验（只执行一次，在重试循环外）
        validateUrl(request.getUrl());

        // 尝试执行请求（支持重试）
        Exception lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                // 执行单次请求
                HttpServiceResponse response = executeRequest(SHARED_HTTP_CLIENT, request, attempt + 1, maxAttempts);

                // 设置请求总耗时
                response.setDuration(System.currentTimeMillis() - startTime);

                // 请求成功，直接返回
                return response;

            } catch (Exception e) {
                // 记录异常，用于最后抛出
                lastException = e;

                // 记录失败日志，包含详细的上下文信息
                log.warn("[FLOW-TRACE] HTTP请求失败（第{}次尝试）: processId={}, traceId={}, executionUuid={}, nodeId={}, method={}, url={}, timeout={}ms, 异异={}",
                        attempt + 1, request.getProcessId(), request.getTraceId(), request.getExecutionUuid(),
                        request.getNodeId(), request.getMethod(), request.getUrl(), request.getTimeout(), e.getMessage());

                // 判断是否应该重试
                if (attempt < maxAttempts - 1 && shouldRetry(e)) {
                    // 计算退避时间（指数退避）
                    long backoffTime = calculateBackoff(attempt);
                    log.info("[FLOW-TRACE] 等待{}ms后进行第{}次重试: processId={}, traceId={}, executionUuid={}, nodeId={}",
                            backoffTime, attempt + 2, request.getProcessId(), request.getTraceId(),
                            request.getExecutionUuid(), request.getNodeId());

                    // 等待退避时间
                    Thread.sleep(backoffTime);
                } else {
                    // 不重试或已达到最大重试次数，退出循环
                    break;
                }
            }
        }

        // 所有尝试都失败，记录错误日志并抛出异常
        long totalDuration = System.currentTimeMillis() - startTime;
        log.error("[FLOW-TRACE] HTTP请求最终失败（尝试了{}次）: processId={}, traceId={}, executionUuid={}, nodeId={}, method={}, url={}, 总耗时={}ms",
                maxAttempts, request.getProcessId(), request.getTraceId(), request.getExecutionUuid(),
                request.getNodeId(), request.getMethod(), request.getUrl(), totalDuration);
        throw lastException;
    }

    /**
     * 执行单次 HTTP 请求
     *
     * <p>该方法负责构建和发送单个 HTTP 请求：
     * 1. 构建 JDK HttpRequest 对象
     * 2. 设置 URI、超时、方法、头、体
     * 3. 发送请求并获取响应
     * 4. 检查状态码，决定是否需要重试
     * 5. 构建响应对象并解析响应体
     * 6. 处理错误情况（4xx/5xx）
     *
     * @param client HttpClient 实例
     * @param request 请求配置对象
     * @param attempt 当前尝试次数（从 1 开始）
     * @param maxAttempts 最大尝试次数
     * @return HTTP 服务响应对象
     * @throws Exception 请求执行过程中的异常
     */
    private HttpServiceResponse executeRequest(HttpClient client, HttpRequest request,
            int attempt, int maxAttempts) throws Exception {
        // 转换为大写，统一处理
        String method = request.getMethod().toUpperCase();

        // 构建 JDK HttpRequest.Builder
        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.getUrl()))                    // 设置请求 URI
                .timeout(Duration.ofMillis(request.getTimeout()));    // 设置请求超时

        // 设置请求方法（GET/POST/PUT/DELETE/PATCH）
        setRequestMethod(builder, method, request);

        // 设置请求头
        if (request.getHeaders() != null) {
            for (HttpNodeData.Header header : request.getHeaders()) {
                builder.header(header.getKey(), header.getValue());
            }
        }

        // 构建请求并发送
        java.net.http.HttpRequest httpRequest = builder.build();
        HttpResponse<String> jdkResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // 获取 HTTP 状态码
        int statusCode = jdkResponse.statusCode();

        // 检查是否需要基于状态码重试（5xx 和 429）
        if (shouldRetryByStatusCode(statusCode) && maxAttempts > 1) {
            throw new RuntimeException("HTTP " + statusCode + " - 服务器错误，需要重试");
        }

        // 构建服务响应对象
        HttpServiceResponse serviceResponse = new HttpServiceResponse();
        serviceResponse.setStatusCode(statusCode);                    // 状态码
        serviceResponse.setHeaders(jdkResponse.headers().map());     // 响应头
        serviceResponse.setRawBody(jdkResponse.body());               // 原始响应体
        serviceResponse.setBody(parseResponseBody(jdkResponse.body(), jdkResponse.headers()));  // 解析后的响应体

        // 处理错误响应（4xx 和 5xx）
        if (statusCode >= 400) {
            // 提取错误详情
            String errorDetail = extractErrorDetail(jdkResponse.body());

            // 设置错误消息：错误类型 + 错误详情
            serviceResponse.setErrorMessage(getErrorType(statusCode) + ": " + errorDetail);

            // 记录错误日志
            log.warn("HTTP 请求返回错误 - 方法: {}, URL: {}, 状态码: {}, 错误: {}",
                    method, request.getUrl(), statusCode, serviceResponse.getErrorMessage());
        }

        // 记录成功日志
        log.info("HTTP 请求成功 - 方法: {}, URL: {}, 状态码: {}, 尝试次数: {}/{}",
                method, request.getUrl(), statusCode, attempt, maxAttempts);

        return serviceResponse;
    }

    /**
     * 设置 HTTP 请求方法
     *
     * <p>根据不同的 HTTP 方法类型，设置对应的请求体处理器：
     * - GET/DELETE: 无请求体
     * - POST/PUT/PATCH: 需要请求体
     *
     * @param builder JDK HttpRequest.Builder 对象
     * @param method HTTP 方法（大写）
     * @param request 请求配置对象
     * @throws ConnectorConfigException 当方法不支持时抛出
     */
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
     *
     * <p>以下异常类型会触发重试：
     * - ConnectException: 连接被拒绝（网络问题）
     * - HttpTimeoutException: HTTP 请求超时
     * - SocketTimeoutException: Socket 超时
     * - InterruptedIOException: I/O 中断
     *
     * @param e 捕获的异常
     * @return 如果应该重试返回 true，否则返回 false
     */
    private boolean shouldRetry(Exception e) {
        return e instanceof ConnectException
                || e instanceof HttpTimeoutException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof java.io.InterruptedIOException;
    }

    /**
     * 判断是否应该重试（基于 HTTP 状态码）
     *
     * <p>以下状态码会触发重试：
     * - 5xx: 服务器错误（可能是临时故障）
     * - 429: 请求过于频繁（需要等待后重试）
     *
     * <p>注意：4xx 客户端错误不会重试（除了 429）
     *
     * @param statusCode HTTP 状态码
     * @return 如果应该重试返回 true，否则返回 false
     */
    private boolean shouldRetryByStatusCode(int statusCode) {
        return statusCode >= 500 || statusCode == 429;
    }

    /**
     * 计算退避时间（指数退避算法）
     *
     * <p>指数退避算法：
     * - 第 1 次重试: 100ms (100 * 2^0)
     * - 第 2 次重试: 200ms (100 * 2^1)
     * - 第 3 次重试: 400ms (100 * 2^2)
     * - 第 4 次重试: 800ms (100 * 2^3)
     * - 最大退避时间: 5000ms
     *
     * <p>使用位移操作 (1L << attempt) 实现 2 的 attempt 次方
     *
     * @param attempt 当前重试次数（从 0 开始）
     * @return 退避时间（毫秒）
     */
    private long calculateBackoff(int attempt) {
        return Math.min(100 * (1L << attempt), 5000);
    }

    /**
     * SSRF 防护校验
     *
     * <p>SSRF (Server-Side Request Forgery) 防护：
     * 禁止访问内网地址，防止攻击者利用服务器发起内网扫描或攻击
     *
     * <p>被阻止的地址类型：
     * - localhost: 127.0.0.1, ::1, 0:0:0:0:0:0:0:1
     * - 私有 IP:
     *   - 10.0.0.0 - 10.255.255.255 (10.x.x.x)
     *   - 172.16.0.0 - 172.31.255.255 (172.16-31.x.x)
     *   - 192.168.0.0 - 192.168.255.255 (192.168.x.x)
     *
     * @param url 待校验的 URL
     * @throws ConnectorConfigException 如果 URL 为内网地址或格式无效
     */
    private void validateUrl(String url) throws ConnectorConfigException {
        try {
            // 解析 URL，提取主机名
            URI uri = URI.create(url);
            String host = uri.getHost();

            // 检查主机名是否为 null
            if (host == null) {
                throw new ConnectorConfigException("http", "URL 格式无效: " + url);
            }

            // 检查是否为私有 IP 或 localhost
            if (isPrivateIP(host) || isLocalhost(host)) {
                throw new ConnectorConfigException("http", "禁止访问内网地址或 localhost: " + host);
            }
        } catch (IllegalArgumentException e) {
            // URL 格式无效
            throw new ConnectorConfigException("http", "URL 格式无效: " + url);
        }
    }

    /**
     * 检查是否为私有 IP 地址
     *
     * <p>使用正则表达式匹配以下私有 IP 段：
     * - 10.0.0.0 - 10.255.255.255 (10.x.x.x)
     * - 172.16.0.0 - 172.31.255.255 (172.16-31.x.x)
     * - 192.168.0.0 - 192.168.255.255 (192.168.x.x)
     *
     * @param host 主机名（可能包含端口号）
     * @return 如果是私有 IP 返回 true，否则返回 false
     */
    private boolean isPrivateIP(String host) {
        // 去除端口号（如果存在）
        String ip = host.split(":")[0];

        // 私有 IP 正则表达式
        // 10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}: 匹配 10.x.x.x
        // 192\\.168\\.\\d{1,3}\\.\\d{1,3}: 匹配 192.168.x.x
        // 172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3}: 匹配 172.16-31.x.x
        String ipv4Pattern = "^(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|172\\.(1[6-9]|2[0-9]|3[0-1])\\.\\d{1,3}\\.\\d{1,3})$";
        return ip.matches(ipv4Pattern);
    }

    /**
     * 检查是否为 localhost
     *
     * <p>匹配以下 localhost 形式：
     * - localhost（不区分大小写）
     * - 127.0.0.1
     * - ::1（IPv6）
     * - 0:0:0:0:0:0:0:1（IPv6 完整形式）
     *
     * @param host 主机名
     * @return 如果是 localhost 返回 true，否则返回 false
     */
    private boolean isLocalhost(String host) {
        return "localhost".equalsIgnoreCase(host)
                || "127.0.0.1".equals(host)
                || "::1".equals(host)
                || "0:0:0:0:0:0:0:1".equals(host);
    }

    /**
     * 构建请求体 Publisher
     *
     * <p>根据请求体内容构建 BodyPublisher：
     * - 如果请求体为 null 或空，返回 noBody()
     * - 否则返回包含请求内容的 ofString()
     *
     * @param request 请求配置对象
     * @return BodyPublisher 对象
     */
    private java.net.http.HttpRequest.BodyPublisher buildBodyPublisher(HttpRequest request) {
        String bodyContent = request.getBodyContent();

        // 如果请求体为空，返回 noBody
        if (bodyContent == null || bodyContent.isEmpty()) {
            return java.net.http.HttpRequest.BodyPublishers.noBody();
        }

        // 返回包含请求内容的 Publisher
        return java.net.http.HttpRequest.BodyPublishers.ofString(bodyContent);
    }

    /**
     * 解析响应体
     *
     * <p>智能响应解析：
     * 1. 检查 Content-Type 响应头
     * 2. 如果是 application/json，尝试解析为 JSON 对象
     * 3. 如果解析失败或不是 JSON，返回原始字符串
     *
     * <p>这样可以同时支持：
     * - JSON API: 返回解析后的 Map/List
     * - 文本 API: 返回原始字符串
     * - 二进制 API: 返回原始字符串（Base64 或其他格式）
     *
     * @param rawBody 原始响应体
     * @param headers HTTP 响应头
     * @return 解析后的响应体（JSON 对象或原始字符串）
     */
    private Object parseResponseBody(String rawBody, java.net.http.HttpHeaders headers) {
        // 获取 Content-Type 响应头
        List<String> contentTypes = headers.map().getOrDefault("Content-Type", List.of());

        // 如果没有 Content-Type，直接返回原始字符串
        if (contentTypes.isEmpty()) {
            return rawBody;
        }

        // 提取并转换为小写
        String contentType = contentTypes.get(0).toLowerCase();

        // 如果不是 JSON 类型，直接返回原始字符串
        if (!contentType.contains("application/json")) {
            return rawBody;
        }

        // 尝试解析 JSON
        try {
            return objectMapper.readValue(rawBody, Object.class);
        } catch (Exception e) {
            // JSON 解析失败，记录警告并返回原始字符串
            log.warn("Failed to parse JSON body, return raw string: {}", e.getMessage());
            return rawBody;
        }
    }

    /**
     * 获取错误类型描述
     *
     * <p>根据 HTTP 状态码返回错误类型：
     * - 429: 请求过于频繁
     * - 5xx: 服务器错误
     * - 4xx: 客户端错误
     * - 其他: 未知错误
     *
     * @param statusCode HTTP 状态码
     * @return 错误类型描述
     */
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

    /**
     * 提取错误详情
     *
     * <p>从响应体中提取错误详情：
     * 1. 如果响应体为空，返回"无响应体"
     * 2. 如果响应体是 JSON 格式，尝试提取 message 或 error 字段
     * 3. 如果提取失败，返回截断的响应体（最多 100 字符）
     *
     * <p>这样可以友好地显示各种 API 的错误信息：
     * - RESTful API 通常返回 { "message": "error description" }
     * - 某些 API 返回 { "error": "error description" }
     *
     * @param responseBody HTTP 响应体
     * @return 错误详情字符串
     */
    private String extractErrorDetail(String responseBody) {
        // 检查响应体是否为空
        if (responseBody == null || responseBody.isEmpty()) {
            return "无响应体";
        }

        try {
            // 尝试解析 JSON 并提取错误信息
            if (responseBody.trim().startsWith("{")) {
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, Map.class);

                // 优先提取 message 字段
                Object message = errorMap.get("message");
                if (message != null) {
                    return message.toString();
                }

                // 其次提取 error 字段
                Object error = errorMap.get("error");
                if (error != null) {
                    return error.toString();
                }
            }
        } catch (Exception e) {
            // JSON 解析失败，忽略
        }

        // 如果响应体很长，截取前 100 个字符
        return responseBody.length() > 100
                ? responseBody.substring(0, 100) + "..."
                : responseBody;
    }
}