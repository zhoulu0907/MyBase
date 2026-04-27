package com.cmsr.onebase.module.flow.component.external.connector.exception;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 连接器异常体系单元测试
 * 测试所有异常类的创建、消息格式、异常链等功能
 */
class ConnectorExceptionTest {

    @Test
    void testConnectorException_Basic() {
        // Given
        String connectorType = "EMAIL_163";
        String actionType = "SEND";
        String message = "邮件发送失败";

        // When
        ConnectorException exception = new ConnectorException(connectorType, actionType, message);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertEquals(actionType, exception.getActionType());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConnectorException_WithCause() {
        // Given
        String connectorType = "DATABASE_MYSQL";
        String actionType = "QUERY";
        String message = "数据库查询失败";
        Throwable cause = new SQLException("Connection timeout");

        // When
        ConnectorException exception = new ConnectorException(connectorType, actionType, message, cause);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertEquals(actionType, exception.getActionType());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConnectorException_GetFullMessage() {
        // Given
        String connectorType = "EMAIL_163";
        String actionType = "SEND";
        String message = "邮件发送失败";
        ConnectorException exception = new ConnectorException(connectorType, actionType, message);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertTrue(fullMessage.contains("连接器错误"));
        assertTrue(fullMessage.contains("type=" + connectorType));
        assertTrue(fullMessage.contains("action=" + actionType));
        assertTrue(fullMessage.contains(message));
    }

    @Test
    void testConnectorException_GetFullMessage_WithCause() {
        // Given
        String connectorType = "DATABASE_MYSQL";
        String actionType = "QUERY";
        String message = "数据库查询失败";
        Throwable cause = new SQLException("Connection timeout");
        ConnectorException exception = new ConnectorException(connectorType, actionType, message, cause);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertTrue(fullMessage.contains("连接器错误"));
        assertTrue(fullMessage.contains("type=" + connectorType));
        assertTrue(fullMessage.contains("action=" + actionType));
        assertTrue(fullMessage.contains(message));
    }

    @Test
    void testConnectorNotFoundException_Basic() {
        // Given
        String connectorType = "NOT_EXIST";

        // When
        ConnectorNotFoundException exception = new ConnectorNotFoundException(connectorType);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertTrue(exception.getMessage().contains(connectorType));
        assertTrue(exception.getMessage().contains("未找到"));
    }

    @Test
    void testConnectorConfigException_Basic() {
        // Given
        String connectorType = "EMAIL_163";
        String message = "配置无效：缺少host字段";

        // When
        ConnectorConfigException exception = new ConnectorConfigException(connectorType, message);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConnectorConfigException_GetFullMessage() {
        // Given
        String connectorType = "EMAIL_163";
        String message = "配置无效：缺少host字段";
        ConnectorConfigException exception = new ConnectorConfigException(connectorType, message);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertTrue(fullMessage.contains("连接器错误"));
        assertTrue(fullMessage.contains("type=" + connectorType));
        assertTrue(fullMessage.contains(message));
    }

    @Test
    void testConnectorExecutionException_Basic() {
        // Given
        String connectorType = "DATABASE_MYSQL";
        String actionType = "QUERY";
        String message = "SQL执行失败";

        // When
        ConnectorExecutionException exception = new ConnectorExecutionException(connectorType, actionType, message);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertEquals(actionType, exception.getActionType());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConnectorExecutionException_WithCause() {
        // Given
        String connectorType = "DATABASE_MYSQL";
        String actionType = "QUERY";
        String message = "SQL执行失败";
        Throwable cause = new SQLException("Syntax error");

        // When
        ConnectorExecutionException exception = new ConnectorExecutionException(connectorType, actionType, message, cause);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertEquals(actionType, exception.getActionType());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConnectorExecutionException_GetFullMessage() {
        // Given
        String connectorType = "DATABASE_MYSQL";
        String actionType = "QUERY";
        String message = "SQL执行失败";
        ConnectorExecutionException exception = new ConnectorExecutionException(connectorType, actionType, message);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertTrue(fullMessage.contains("连接器错误"));
        assertTrue(fullMessage.contains("type=" + connectorType));
        assertTrue(fullMessage.contains("action=" + actionType));
        assertTrue(fullMessage.contains(message));
    }

    @Test
    void testConnectorException_Chaining() {
        // Given
        SQLException rootCause = new SQLException("Connection timeout");
        ConnectorExecutionException midException = new ConnectorExecutionException(
                "DATABASE_MYSQL", "QUERY", "数据库查询失败", rootCause);
        ConnectorException topException = new ConnectorException(
                "WORKFLOW", "EXECUTE", "工作流执行失败", midException);

        // When
        Throwable cause = topException.getCause();

        // Then
        assertEquals(midException, cause);
        assertEquals(rootCause, cause.getCause());
    }

    @Test
    void testConnectorException_NullConnectorType() {
        // Given
        String actionType = "SEND";
        String message = "发送失败";

        // When
        ConnectorException exception = new ConnectorException(null, actionType, message);

        // Then
        assertNull(exception.getConnectorType());
        assertEquals(actionType, exception.getActionType());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConnectorException_NullActionType() {
        // Given
        String connectorType = "EMAIL_163";
        String message = "发送失败";

        // When
        ConnectorException exception = new ConnectorException(connectorType, null, message);

        // Then
        assertEquals(connectorType, exception.getConnectorType());
        assertNull(exception.getActionType());
        assertEquals(message, exception.getMessage());
    }
}
