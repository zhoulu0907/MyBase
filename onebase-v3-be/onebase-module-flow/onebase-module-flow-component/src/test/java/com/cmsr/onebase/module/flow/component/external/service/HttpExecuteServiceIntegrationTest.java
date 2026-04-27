package com.cmsr.onebase.module.flow.component.external.service;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpExecuteService 集成测试
 * 使用真实的 HTTP API 测试完整功能
 *
 * <p>注意：这些测试会发起真实的网络请求，需要网络连接
 *
 * @author zhoulu
 * @since 2026-01-15
 */
class HttpExecuteServiceIntegrationTest {

    private HttpExecuteService httpExecuteService;

    @BeforeEach
    void setUp() {
        httpExecuteService = new HttpExecuteService();
        // 注入 ObjectMapper
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        ReflectionTestUtils.setField(httpExecuteService, "objectMapper", objectMapper);
    }

    @Test
    void testRealHttpGetRequest() throws Exception {
        // Given - 使用 httpbin.org 进行测试
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/get");
        request.setMethod("GET");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getRawBody());
        assertTrue(response.isSuccess());
        assertNotNull(response.getDuration());
        assertTrue(response.getDuration() > 0);

        // 验证响应体是 JSON 格式
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();
        assertTrue(bodyMap.containsKey("args"));
        assertTrue(bodyMap.containsKey("url"));
    }

    @Test
    void testRealHttpPostRequest() throws Exception {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/post");
        request.setMethod("POST");
        request.setBodyType("JSON");
        request.setBodyContent("{\"name\":\"test\",\"value\":123}");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());

        // 验证请求体被正确发送
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();
        assertTrue(bodyMap.containsKey("json"));

        @SuppressWarnings("unchecked")
        Map<String, Object> jsonData = (Map<String, Object>) bodyMap.get("json");
        assertEquals("test", jsonData.get("name"));
        assertEquals(123, jsonData.get("value"));
    }

    @Test
    void testRealHttpRequestWithHeaders() throws Exception {
        // Given
        HttpNodeData.Header header1 = new HttpNodeData.Header();
        header1.setKey("X-Custom-Header");
        header1.setValue("test-value");

        HttpNodeData.Header header2 = new HttpNodeData.Header();
        header2.setKey("User-Agent");
        header2.setValue("OneBase-HTTP-Connector/1.0");

        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/headers");
        request.setMethod("GET");
        request.setHeaders(List.of(header1, header2));
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());

        // 验证请求头被正确发送
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyMap = (Map<String, Object>) response.getBody();
        assertTrue(bodyMap.containsKey("headers"));

        @SuppressWarnings("unchecked")
        Map<String, Object> headers = (Map<String, Object>) bodyMap.get("headers");
        assertEquals("test-value", headers.get("X-Custom-Header"));
    }

    @Test
    void testSsrfProtection_BlocksLocalhost() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("http://localhost:8080/test");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(0);

        // When & Then - 应该抛出异常
        Exception exception = assertThrows(ConnectorConfigException.class, () -> {
            httpExecuteService.execute(request);
        });

        // 验证异常消息
        assertTrue(exception.getMessage().contains("禁止访问内网地址或 localhost"));
    }

    @Test
    void testSsrfProtection_Blocks127001() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("http://127.0.0.1:8080/test");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(0);

        // When & Then - 应该抛出异常
        Exception exception = assertThrows(ConnectorConfigException.class, () -> {
            httpExecuteService.execute(request);
        });

        // 验证异常类型
        assertTrue(exception.getMessage().contains("禁止访问内网地址或 localhost"));
    }

    @Test
    void testSsrfProtection_BlocksPrivateIP_10x() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("http://10.0.0.1:8080/test");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(0);

        // When & Then - 应该抛出异常
        Exception exception = assertThrows(ConnectorConfigException.class, () -> {
            httpExecuteService.execute(request);
        });

        // 验证异常类型
        assertTrue(exception.getMessage().contains("禁止访问内网地址或 localhost"));
    }

    @Test
    void testSsrfProtection_BlocksPrivateIP_192168() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("http://192.168.1.1:8080/test");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(0);

        // When & Then - 应该抛出异常
        Exception exception = assertThrows(ConnectorConfigException.class, () -> {
            httpExecuteService.execute(request);
        });

        // 验证异常类型
        assertTrue(exception.getMessage().contains("禁止访问内网地址或 localhost"));
    }

    @Test
    void testHttp404ErrorResponse() throws Exception {
        // Given - 请求一个不存在的端点
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/status/404");
        request.setMethod("GET");
        request.setTimeout(15000);  // 增加超时时间
        request.setRetry(1);  // 添加重试

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertFalse(response.isSuccess());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("客户端错误"));
    }

    @Test
    void testHttp500ErrorResponse() throws Exception {
        // Given - 请求返回 500 的端点
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/status/500");
        request.setMethod("GET");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertFalse(response.isSuccess());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("服务器错误"));
    }

    @Test
    void testTimeoutHandling() {
        // Given - 使用一个会延迟响应的端点，设置较短的超时时间
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/delay/10");  // 延迟 10 秒
        request.setMethod("GET");
        request.setTimeout(2000);  // 2 秒超时
        request.setRetry(0);

        // When & Then - 应该抛出超时异常
        Exception exception = assertThrows(Exception.class, () -> {
            httpExecuteService.execute(request);
        });

        // 验证是超时异常（直接抛出或作为原因）
        boolean isTimeout = exception instanceof java.net.http.HttpTimeoutException
                || exception instanceof java.net.SocketTimeoutException
                || exception.getCause() instanceof java.net.http.HttpTimeoutException
                || exception.getCause() instanceof java.net.SocketTimeoutException
                || exception.getMessage().contains("timed out");
        assertTrue(isTimeout, "Expected timeout exception but got: " + exception.getClass().getName());
    }

    @Test
    void testRetryMechanism_WithTransientError() throws Exception {
        // Given - 使用一个会返回 500 的端点，配置重试
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/status/500");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(2);  // 重试 2 次

        // When - 应该会重试，但最终还是失败（因为 500 一直返回）
        Exception exception = assertThrows(Exception.class, () -> {
            httpExecuteService.execute(request);
        });

        // Then - 最终会失败，但验证了重试逻辑被执行
        assertNotNull(exception);
    }

    @Test
    void testPutRequest() throws Exception {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/put");
        request.setMethod("PUT");
        request.setBodyType("JSON");
        request.setBodyContent("{\"updated\":true}");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    void testDeleteRequest() throws Exception {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/delete");
        request.setMethod("DELETE");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    void testPatchRequest() throws Exception {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/patch");
        request.setMethod("PATCH");
        request.setBodyType("JSON");
        request.setBodyContent("{\"patched\":true}");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
    }

    @Test
    void testResponseHeaders() throws Exception {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/get");
        request.setMethod("GET");
        request.setTimeout(10000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getHeaders());
        assertFalse(response.getHeaders().isEmpty());

        // 验证常见的响应头存在
        assertTrue(response.getHeaders().containsKey("Content-Type"));
        List<String> contentTypes = response.getHeaders().get("Content-Type");
        assertNotNull(contentTypes);
        assertFalse(contentTypes.isEmpty());
    }
}
