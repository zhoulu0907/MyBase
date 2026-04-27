package com.cmsr.onebase.module.flow.component.external.connector.impl;

import com.cmsr.onebase.module.flow.component.external.connector.ConnectorTestBase;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorExecutionException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Email163Connector单元测试
 * 测试163邮箱连接器的配置验证、邮件发送等功能
 */
class Email163ConnectorTest extends ConnectorTestBase {

    private final Email163Connector connector = new Email163Connector();

    @Test
    void testGetConnectorInfo() {
        // When & Then
        assertEquals("EMAIL_163", connector.getConnectorType());
        assertEquals("163邮箱连接器", connector.getConnectorName());
        assertEquals("163邮箱发送连接器，支持文本和HTML邮件发送", connector.getConnectorDescription());
    }

    @Test
    void testValidateConfig_Valid() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", "smtp.163.com");
        config.put("smtpPort", "465");
        config.put("username", "test@163.com");
        config.put("password", "password");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateConfig_MissingHost() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("smtpPort", "465");
        config.put("username", "test@163.com");
        config.put("password", "password");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_MissingPort() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", "smtp.163.com");
        config.put("username", "test@163.com");
        config.put("password", "password");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_MissingUsername() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", "smtp.163.com");
        config.put("smtpPort", "465");
        config.put("password", "password");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateConfig_MissingPassword() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", "smtp.163.com");
        config.put("smtpPort", "465");
        config.put("username", "test@163.com");

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
    void testValidateConfig_EmptyConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testExecute_SendEmail() throws Exception {
        // Given
        Map<String, Object> config = buildValidEmailConfig();

        // Mock Transport.send to avoid real email sending
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any())).thenAnswer(invocation -> {
                // Do nothing - void method
                return null;
            });

            // When
            Map<String, Object> result = connector.execute("SEND", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("邮件发送成功", result.get("message"));
            mockedTransport.verify(() -> Transport.send(any()), times(1));
        }
    }

    @Test
    void testExecute_InvalidConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", "smtp.163.com");
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
    void testExecute_EmailSendingFailure() throws Exception {
        // Given
        Map<String, Object> config = buildValidEmailConfig();

        // Mock Transport.send to throw exception
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any()))
                    .thenThrow(new MessagingException("Connection refused"));

            // When
            Exception exception = assertThrows(Exception.class, () -> {
                connector.execute("SEND", config);
            });

            // Then
            assertNotNull(exception);
            assertTrue(exception instanceof ConnectorExecutionException);
            assertTrue(exception.getMessage().contains("邮件发送失败"));
        }
    }

    @Test
    void testExecute_MissingInputData() throws Exception {
        // Given
        Map<String, Object> config = buildValidEmailConfig();
        // inputData is missing

        // Mock Transport.send
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any())).thenAnswer(invocation -> {
                // Do nothing - void method
                return null;
            });

            // When - should still work with empty inputData
            Map<String, Object> result = connector.execute("SEND", config);

            // Then
            assertExecutionSuccess(result);
        }
    }

    @Test
    void testExecute_WithInputData() throws Exception {
        // Given
        Map<String, Object> config = buildValidEmailConfig();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("to", "recipient@example.com");
        inputData.put("subject", "Test Subject");
        inputData.put("text", "Test Content");
        config.put("inputData", inputData);

        // Mock Transport.send
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any())).thenAnswer(invocation -> {
                // Do nothing - void method
                return null;
            });

            // When
            Map<String, Object> result = connector.execute("SEND", config);

            // Then
            assertExecutionSuccess(result);
            mockedTransport.verify(() -> Transport.send(any()), times(1));
        }
    }

    /**
     * 构建有效的邮件配置
     */
    private Map<String, Object> buildValidEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("smtpHost", "smtp.163.com");
        config.put("smtpPort", "465");
        config.put("username", "test@163.com");
        config.put("password", "password");
        config.put("from", "test@163.com");
        return config;
    }
}
