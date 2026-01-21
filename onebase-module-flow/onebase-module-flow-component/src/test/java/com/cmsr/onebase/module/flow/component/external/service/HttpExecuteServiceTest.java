package com.cmsr.onebase.module.flow.component.external.service;

import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HttpExecuteService 单元测试
 * 测试 HTTP 执行服务的各种场景
 *
 * @author zhoulu
 * @since 2026-01-15
 */
class HttpExecuteServiceTest {

    private HttpExecuteService httpExecuteService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        httpExecuteService = new HttpExecuteService();
        ReflectionTestUtils.setField(httpExecuteService, "objectMapper", objectMapper);
    }

    // 注意：实际的 HTTP 请求集成测试应该使用真实的 HTTP 服务器 mock
    // 这里主要测试 SSRF 防护和响应解析逻辑

    @Test
    void testValidateUrl_PublicUrl_Success() throws Exception {
        // Given
        String publicUrl = "https://api.example.com/data";

        // When & Then - 公开 URL 应该通过验证
        assertDoesNotThrow(() -> {
            invokeValidateUrl(publicUrl);
        });
    }

    @Test
    void testValidateUrl_Localhost_ThrowsException() throws Exception {
        // Given
        String localhostUrl = "http://localhost:8080/api";

        // When & Then - localhost 应该被拒绝
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            invokeValidateUrl(localhostUrl);
        });

        // Then - 验证异常原因
        assertTrue(exception.getCause() instanceof ConnectorConfigException);
    }

    @Test
    void testValidateUrl_127001_ThrowsException() throws Exception {
        // Given
        String loopbackUrl = "http://127.0.0.1:8080/api";

        // When & Then - 127.0.0.1 应该被拒绝
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            invokeValidateUrl(loopbackUrl);
        });

        // Then - 验证异常原因
        assertTrue(exception.getCause() instanceof ConnectorConfigException);
    }

    @Test
    void testValidateUrl_PrivateIP_10x_ThrowsException() throws Exception {
        // Given
        String privateUrl = "http://10.0.0.1:8080/api";

        // When & Then - 10.x.x.x 应该被拒绝
        assertThrows(InvocationTargetException.class, () -> {
            invokeValidateUrl(privateUrl);
        });
    }

    @Test
    void testValidateUrl_PrivateIP_192168_ThrowsException() throws Exception {
        // Given
        String privateUrl = "http://192.168.1.1:8080/api";

        // When & Then - 192.168.x.x 应该被拒绝
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            invokeValidateUrl(privateUrl);
        });

        // Then - 验证异常原因
        assertTrue(exception.getCause() instanceof ConnectorConfigException);
    }

    @Test
    void testValidateUrl_PrivateIP_17216_ThrowsException() throws Exception {
        // Given
        String privateUrl = "http://172.16.0.1:8080/api";

        // When & Then - 172.16.x.x 应该被拒绝
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            invokeValidateUrl(privateUrl);
        });

        // Then - 验证异常原因
        assertTrue(exception.getCause() instanceof ConnectorConfigException);
    }

    @Test
    void testValidateUrl_InvalidUrl_ThrowsException() throws Exception {
        // Given
        String invalidUrl = "not-a-valid-url";

        // When & Then - 无效 URL 应该抛出异常
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            invokeValidateUrl(invalidUrl);
        });

        // Then - 验证异常原因
        assertTrue(exception.getCause() instanceof ConnectorConfigException);
    }

    @Test
    void testParseResponseBody_JsonContent_ReturnsMap() throws Exception {
        // Given
        String jsonBody = "{\"name\":\"John\",\"age\":30}";
        java.net.http.HttpHeaders headers = mock(java.net.http.HttpHeaders.class);
        when(headers.map()).thenReturn(Map.of("Content-Type", List.of("application/json")));
        when(objectMapper.readValue(jsonBody, Object.class)).thenReturn(Map.of("name", "John", "age", 30));

        // When
        Object result = invokeParseResponseBody(jsonBody, headers);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertEquals("John", resultMap.get("name"));
        assertEquals(30, resultMap.get("age"));
    }

    @Test
    void testParseResponseBody_TextContent_ReturnsString() throws Exception {
        // Given
        String textBody = "Plain text response";
        java.net.http.HttpHeaders headers = mock(java.net.http.HttpHeaders.class);
        when(headers.map()).thenReturn(Map.of("Content-Type", List.of("text/plain")));

        // When
        Object result = invokeParseResponseBody(textBody, headers);

        // Then
        assertNotNull(result);
        assertEquals(textBody, result);
    }

    @Test
    void testParseResponseBody_NoContentType_ReturnsString() throws Exception {
        // Given
        String body = "Response without content type";
        java.net.http.HttpHeaders headers = mock(java.net.http.HttpHeaders.class);
        when(headers.map()).thenReturn(Map.of());

        // When
        Object result = invokeParseResponseBody(body, headers);

        // Then
        assertNotNull(result);
        assertEquals(body, result);
    }

    @Test
    void testParseResponseBody_InvalidJson_ReturnsRawString() throws Exception {
        // Given
        String invalidJson = "{invalid json}";
        java.net.http.HttpHeaders headers = mock(java.net.http.HttpHeaders.class);
        when(headers.map()).thenReturn(Map.of("Content-Type", List.of("application/json")));
        when(objectMapper.readValue(invalidJson, Object.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        // When
        Object result = invokeParseResponseBody(invalidJson, headers);

        // Then
        assertNotNull(result);
        assertEquals(invalidJson, result);
    }

    // ========== 辅助方法 ==========

    private void invokeValidateUrl(String url) throws Exception {
        java.lang.reflect.Method method = HttpExecuteService.class
                .getDeclaredMethod("validateUrl", String.class);
        method.setAccessible(true);
        method.invoke(httpExecuteService, url);
    }

    private Object invokeParseResponseBody(String body, java.net.http.HttpHeaders headers) throws Exception {
        java.lang.reflect.Method method = HttpExecuteService.class
                .getDeclaredMethod("parseResponseBody", String.class, java.net.http.HttpHeaders.class);
        method.setAccessible(true);
        return method.invoke(httpExecuteService, body, headers);
    }
}
