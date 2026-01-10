package com.cmsr.onebase.module.flow.component.external.connector.impl;

import com.cmsr.onebase.module.flow.component.external.connector.ConnectorTestBase;
import com.cmsr.onebase.module.flow.component.external.connector.exception.ConnectorConfigException;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DatabaseMysqlConnector单元测试
 * 测试MySQL数据库连接器的配置验证、数据库操作等功能
 */
class DatabaseMysqlConnectorTest extends ConnectorTestBase {

    private final DatabaseMysqlConnector connector = new DatabaseMysqlConnector();

    @Test
    void testGetConnectorInfo() {
        // When & Then
        assertEquals("DATABASE_MYSQL", connector.getConnectorType());
        assertEquals("MySQL数据库连接器", connector.getConnectorName());
        assertEquals("MySQL数据库连接器，支持查询和更新操作", connector.getConnectorDescription());
    }

    @Test
    void testValidateConfig_Valid() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("jdbcUrl", "jdbc:mysql://localhost:3306/test");
        config.put("username", "root");
        config.put("password", "password");

        // When
        boolean isValid = connector.validateConfig(config);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateConfig_MissingJdbcUrl() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("username", "root");
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
        config.put("jdbcUrl", "jdbc:mysql://localhost:3306/test");
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
        config.put("jdbcUrl", "jdbc:mysql://localhost:3306/test");
        config.put("username", "root");

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
    void testExecute_Query() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "QUERY");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "SELECT * FROM users WHERE id = ?");
        inputData.put("parameters", List.of(1));
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);

        // Setup mock behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(2);
        when(mockMetaData.getColumnName(1)).thenReturn("id");
        when(mockMetaData.getColumnName(2)).thenReturn("name");
        when(mockResultSet.next()).thenReturn(true, false); // One row
        when(mockResultSet.getObject(1)).thenReturn(1);
        when(mockResultSet.getObject(2)).thenReturn("Test User");

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When
            Map<String, Object> result = connector.execute("QUERY", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("查询成功", result.get("message"));
            assertNotNull(result.get("data"));
            assertNotNull(result.get("rowCount"));

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
            assertEquals(1, data.size());
            assertEquals(1, data.get(0).get("id"));
            assertEquals("Test User", data.get(0).get("name"));
        }
    }

    @Test
    void testExecute_Update() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "UPDATE");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "UPDATE users SET name = ? WHERE id = ?");
        inputData.put("parameters", List.of("New Name", 1));
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(5); // 5 rows affected

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When
            Map<String, Object> result = connector.execute("UPDATE", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("更新成功", result.get("message"));
            assertEquals(5, result.get("affectedRows"));
        }
    }

    @Test
    void testExecute_Insert() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "INSERT");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "INSERT INTO users (name) VALUES (?)");
        inputData.put("parameters", List.of("Test User"));
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockGeneratedKeys = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getObject(1)).thenReturn(100);

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When
            Map<String, Object> result = connector.execute("INSERT", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("插入成功", result.get("message"));
            assertEquals(1, result.get("affectedRows"));
            assertEquals(100, result.get("generatedKey"));
        }
    }

    @Test
    void testExecute_Delete() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "DELETE");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "DELETE FROM users WHERE id = ?");
        inputData.put("parameters", List.of(1));
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(3); // 3 rows deleted

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When
            Map<String, Object> result = connector.execute("DELETE", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("删除成功", result.get("message"));
            assertEquals(3, result.get("affectedRows"));
        }
    }

    @Test
    void testExecute_DefaultOperationType() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        // operationType not specified, should default to QUERY
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "SELECT 1");
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(1);
        when(mockMetaData.getColumnName(1)).thenReturn("1");
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getObject(1)).thenReturn(1);

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When
            Map<String, Object> result = connector.execute("QUERY", config);

            // Then
            assertExecutionSuccess(result);
            assertEquals("查询成功", result.get("message"));
        }
    }

    @Test
    void testExecute_UnsupportedOperationType() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "UNSUPPORTED");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "SELECT 1");
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When & Then
            Exception exception = assertThrows(Exception.class, () -> {
                connector.execute("UNSUPPORTED", config);
            });

            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("不支持的数据库操作类型") ||
                       exception.getCause().getMessage().contains("不支持的数据库操作类型"));
        }
    }

    @Test
    void testExecute_EmptySql() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "QUERY");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "   "); // Empty SQL
        config.put("inputData", inputData);

        // Mock JDBC objects
        Connection mockConnection = mock(Connection.class);

        // Mock DriverManager
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // When & Then
            Exception exception = assertThrows(Exception.class, () -> {
                connector.execute("QUERY", config);
            });

            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("SQL语句不能为空") ||
                       exception.getCause().getMessage().contains("SQL语句不能为空"));
        }
    }

    @Test
    void testExecute_ConnectionFailure() throws Exception {
        // Given
        Map<String, Object> config = buildValidDbConfig();
        config.put("operationType", "QUERY");
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("sql", "SELECT 1");
        config.put("inputData", inputData);

        // Mock DriverManager to throw exception
        try (var mockedDriverManager = mockStatic(java.sql.DriverManager.class)) {
            SQLException sqlException = new SQLException("Connection refused");
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenThrow(sqlException);

            // When & Then
            Exception exception = assertThrows(Exception.class, () -> {
                connector.execute("QUERY", config);
            });

            assertNotNull(exception);
        }
    }

    @Test
    void testExecute_InvalidConfig() {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("jdbcUrl", "jdbc:mysql://localhost:3306/test");
        // Missing username and password

        // When & Then
        assertThrows(ConnectorConfigException.class, () -> {
            connector.execute("QUERY", config);
        });
    }

    @Test
    void testExecute_NullConfig() {
        // When & Then
        assertThrows(ConnectorConfigException.class, () -> {
            connector.execute("QUERY", null);
        });
    }

    /**
     * 构建有效的数据库配置
     */
    private Map<String, Object> buildValidDbConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("jdbcUrl", "jdbc:mysql://localhost:3306/test");
        config.put("username", "root");
        config.put("password", "password");
        return config;
    }
}
