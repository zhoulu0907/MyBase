package com.cmsr.onebase.module.flow.component.external.connector.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.cmsr.onebase.module.flow.component.external.connector.ConnectorTestBase;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SmsAliConnector单元测试
 * 测试阿里云短信连接器的配置验证、短信发送等功能
 */
class SmsAliConnectorTest extends ConnectorTestBase {

    private final SmsAliConnector connector = new SmsAliConnector();

    @Test
    void testGetConnectorInfo() {
        // When & Then
        assertEquals("SMS_ALI", connector.getConnectorType());
        assertEquals("阿里云短信连接器", connector.getConnectorName());
        assertEquals("阿里云短信发送连接器，支持模板短信和验证码短信", connector.getConnectorDescription());
    }

    @Test
    void testValidateConfig_Valid() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("accessKey", "testAccessKey");
        config.put("secretKey", "testSecretKey");
        config.put("regionId", "cn-hangzhou");
        config.put("signName", "测试签名");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateConfig_MissingAccessKey() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("secretKey", "testSecretKey");
        config.put("regionId", "cn-hangzhou");
        config.put("signName", "测试签名");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_MissingSecretKey() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("accessKey", "testAccessKey");
        config.put("regionId", "cn-hangzhou");
        config.put("signName", "测试签名");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_MissingRegionId() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("accessKey", "testAccessKey");
        config.put("secretKey", "testSecretKey");
        config.put("signName", "测试签名");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_MissingSignName() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("accessKey", "testAccessKey");
        config.put("secretKey", "testSecretKey");
        config.put("regionId", "cn-hangzhou");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_NullConfig() {
        // When
        boolean isValid = connector.validateConfig(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testExecute_SendSms() throws Exception {
        // Given
        Map<String, Object> config = buildValidSmsConfig();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("phoneNumbers", "13800138000");
        inputData.put("templateCode", "SMS_123456789");
        inputData.put("templateParam", Map.of("code", "123456"));
        config.put("inputData", inputData);

        // Mock Aliyun Client and Response
        try (MockedConstruction<Client> mockedClientConstruction = mockConstruction(Client.class, (mock, context) -> {
            // Create mock response
            SendSmsResponseBody responseBody = mock(SendSmsResponseBody.class);
            when(responseBody.getBizId()).thenReturn("1234567890");
            when(responseBody.getCode()).thenReturn("OK");

            SendSmsResponse response = mock(SendSmsResponse.class);
            when(response.getBody()).thenReturn(responseBody);

            // Mock sendSmsWithOptions to return our mock response
            when(mock.sendSmsWithOptions(any(SendSmsRequest.class), any()))
                    .thenReturn(response);
        })) {

            // When
            Map<String, Object> result = connector.execute("SEND", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("短信发送成功", result.get("message"));
            assertEquals("1234567890", result.get("bizId"));
            assertEquals("OK", result.get("code"));
        }
    }

    @Test
    void testExecute_SendSms_MultiplePhoneNumbers() throws Exception {
        // Given
        Map<String, Object> config = buildValidSmsConfig();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("phoneNumbers", List.of("13800138000", "13900139000"));
        inputData.put("templateCode", "SMS_123456789");
        inputData.put("templateParam", Map.of("code", "123456"));
        config.put("inputData", inputData);

        // Mock Aliyun Client and Response
        try (MockedConstruction<Client> mockedClientConstruction = mockConstruction(Client.class, (mock, context) -> {
            SendSmsResponseBody responseBody = mock(SendSmsResponseBody.class);
            when(responseBody.getBizId()).thenReturn("1234567890");
            when(responseBody.getCode()).thenReturn("OK");

            SendSmsResponse response = mock(SendSmsResponse.class);
            when(response.getBody()).thenReturn(responseBody);

            when(mock.sendSmsWithOptions(any(SendSmsRequest.class), any()))
                    .thenReturn(response);
        })) {

            // When
            Map<String, Object> result = connector.execute("SEND", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("短信发送成功", result.get("message"));
            assertEquals("1234567890", result.get("bizId"));
            assertEquals("OK", result.get("code"));
        }
    }

    @Test
    void testExecute_InvalidConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("accessKey", "testAccessKey");
        // Missing required fields

        // When & Then
        assertThrows(ConnectorConfigException.class, () -> {
            connector.execute("SEND", config);
        });
    }

    @Test
    void testExecute_NullConfig() {
        // When & Then
        assertThrows(ConnectorConfigException.class, () -> {
            connector.execute("SEND", null);
        });
    }

    @Test
    void testExecute_SmsSendingFailure() throws Exception {
        // Given
        Map<String, Object> config = buildValidSmsConfig();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("phoneNumbers", "13800138000");
        inputData.put("templateCode", "SMS_123456789");
        inputData.put("templateParam", Map.of("code", "123456"));
        config.put("inputData", inputData);

        // Mock Aliyun Client to throw exception
        try (MockedConstruction<Client> mockedClientConstruction = mockConstruction(Client.class, (mock, context) -> {
            when(mock.sendSmsWithOptions(any(SendSmsRequest.class), any()))
                    .thenThrow(new RuntimeException("Network error"));
        })) {

            // When & Then
            Exception exception = assertThrows(Exception.class, () -> {
                connector.execute("SEND", config);
            });

            assertNotNull(exception);
        }
    }

    @Test
    void testExecute_NullResponse() throws Exception {
        // Given
        Map<String, Object> config = buildValidSmsConfig();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("phoneNumbers", "13800138000");
        inputData.put("templateCode", "SMS_123456789");
        inputData.put("templateParam", Map.of("code", "123456"));
        config.put("inputData", inputData);

        // Mock Aliyun Client to return null body
        try (MockedConstruction<Client> mockedClientConstruction = mockConstruction(Client.class, (mock, context) -> {
            SendSmsResponse response = mock(SendSmsResponse.class);
            when(response.getBody()).thenReturn(null);

            when(mock.sendSmsWithOptions(any(SendSmsRequest.class), any()))
                    .thenReturn(response);
        })) {

            // When & Then
            Exception exception = assertThrows(Exception.class, () -> {
                connector.execute("SEND", config);
            });

            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("短信发送失败，响应为空") ||
                       exception.getCause().getMessage().contains("短信发送失败，响应为空"));
        }
    }

    @Test
    void testExecute_MissingInputData() throws Exception {
        // Given
        Map<String, Object> config = buildValidSmsConfig();
        // inputData is missing

        // Mock Aliyun Client and Response
        try (MockedConstruction<Client> mockedClientConstruction = mockConstruction(Client.class, (mock, context) -> {
            SendSmsResponseBody responseBody = mock(SendSmsResponseBody.class);
            when(responseBody.getBizId()).thenReturn("1234567890");
            when(responseBody.getCode()).thenReturn("OK");

            SendSmsResponse response = mock(SendSmsResponse.class);
            when(response.getBody()).thenReturn(responseBody);

            when(mock.sendSmsWithOptions(any(SendSmsRequest.class), any()))
                    .thenReturn(response);
        })) {

            // When - should still work with empty inputData
            Map<String, Object> result = connector.execute("SEND", config);

            // Then
            assertExecutionSuccess(result);
        }
    }

    /**
     * 构建有效的短信配置
     */
    private Map<String, Object> buildValidSmsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("accessKey", "testAccessKey");
        config.put("secretKey", "testSecretKey");
        config.put("regionId", "cn-hangzhou");
        config.put("signName", "测试签名");
        return config;
    }
}
