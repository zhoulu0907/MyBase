package com.cmsr.onebase.module.flow.build.util;

import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConnectorConfigParser 单元测试
 * <p>
 * 测试连接器配置中环境配置的解析功能
 *
 * @author kanten
 * @since 2026-01-30
 */
class ConnectorConfigParserTest {

    private ConnectorConfigParser parser;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        parser = new ConnectorConfigParser(objectMapper);
    }

    // ==================== parseEnvironments 基础测试用例 ====================

    @Test
    void testParseEnvironments_withNullConfig() {
        // Given
        String nullConfig = null;

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(nullConfig);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEnvironments_withEmptyString() {
        // Given
        String emptyConfig = "";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(emptyConfig);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEnvironments_withBlankString() {
        // Given
        String blankConfig = "   ";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(blankConfig);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEnvironments_withInvalidJson() {
        // Given
        String invalidJson = "{invalid json}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(invalidJson);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEnvironments_withoutEnvironmentsKey() {
        // Given
        String config = "{\"otherKey\":\"value\"}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEnvironments_withEnvironmentsNotArray() {
        // Given
        String config = "{\"environments\":\"not an array\"}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== parseEnvironments 正常场景测试用例 ====================

    @Test
    void testParseEnvironments_withEmptyEnvironmentsArray() {
        // Given
        String config = "{\"environments\":[]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEnvironments_withSingleEnvironment() {
        // Given
        String config = "{\"environments\":[{\"envName\":\"开发环境\",\"envCode\":\"DEV\",\"envUrl\":\"https://dev.example.com\",\"authType\":\"bearer\",\"active\":true}]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        FlowConnectorEnvLiteVO env = result.get(0);
        assertEquals("开发环境", env.getEnvName());
        assertEquals("DEV", env.getEnvCode());
        assertEquals("https://dev.example.com", env.getEnvUrl());
        assertEquals("bearer", env.getAuthType());
        assertEquals(1, env.getActiveStatus());
    }

    @Test
    void testParseEnvironments_withMultipleEnvironments() {
        // Given
        String config = "{" +
                "\"environments\":[" +
                "{\"envName\":\"开发环境\",\"envCode\":\"DEV\",\"envUrl\":\"https://dev.example.com\",\"active\":true}," +
                "{\"envName\":\"测试环境\",\"envCode\":\"TEST\",\"envUrl\":\"https://test.example.com\",\"active\":true}," +
                "{\"envName\":\"生产环境\",\"envCode\":\"PROD\",\"envUrl\":\"https://prod.example.com\",\"active\":false}" +
                "]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(3, result.size());
        assertEquals("开发环境", result.get(0).getEnvName());
        assertEquals("测试环境", result.get(1).getEnvName());
        assertEquals("生产环境", result.get(2).getEnvName());
    }

    @Test
    void testParseEnvironments_withTypeCode() {
        // Given
        String config = "{\"environments\":[{\"envName\":\"开发环境\",\"envCode\":\"DEV\"}]}";
        String typeCode = "HTTP";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config, typeCode);

        // Then
        assertEquals(1, result.size());
        assertEquals("HTTP", result.get(0).getTypeCode());
    }

    // ==================== parseEnvironments 字段缺失测试用例 ====================

    @Test
    void testParseEnvironments_withPartialFields() {
        // Given
        String config = "{\"environments\":[{\"envName\":\"开发环境\"}]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        FlowConnectorEnvLiteVO env = result.get(0);
        assertEquals("开发环境", env.getEnvName());
        assertNull(env.getEnvCode());
        assertNull(env.getEnvUrl());
        assertNull(env.getAuthType());
    }

    @Test
    void testParseEnvironments_withNullFieldValues() {
        // Given
        String config = "{\"environments\":[{\"envName\":null,\"envCode\":null,\"envUrl\":null}]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        FlowConnectorEnvLiteVO env = result.get(0);
        assertNull(env.getEnvName());
        assertNull(env.getEnvCode());
        assertNull(env.getEnvUrl());
    }

    @Test
    void testParseEnvironments_withAllFields() {
        // Given
        String config = "{" +
                "\"environments\":[" +
                "{" +
                "\"envName\":\"开发环境\"," +
                "\"envCode\":\"DEV\"," +
                "\"envUrl\":\"https://dev.example.com\"," +
                "\"authType\":\"bearer\"," +
                "\"description\":\"开发环境配置\"," +
                "\"active\":true" +
                "}" +
                "]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        FlowConnectorEnvLiteVO env = result.get(0);
        assertEquals("开发环境", env.getEnvName());
        assertEquals("DEV", env.getEnvCode());
        assertEquals("https://dev.example.com", env.getEnvUrl());
        assertEquals("bearer", env.getAuthType());
        assertEquals("开发环境配置", env.getDescription());
        assertEquals(1, env.getActiveStatus());
        assertNotNull(env.getCreateTime());
    }

    // ==================== parseEnvironments active状态测试用例 ====================

    @Test
    void testParseEnvironments_withActiveTrue() {
        // Given
        String config = "{\"environments\":[{\"envName\":\"开发环境\",\"active\":true}]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getActiveStatus());
    }

    @Test
    void testParseEnvironments_withActiveFalse() {
        // Given
        String config = "{\"environments\":[{\"envName\":\"开发环境\",\"active\":false}]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getActiveStatus());
    }

    @Test
    void testParseEnvironments_withoutActiveField() {
        // Given
        String config = "{\"environments\":[{\"envName\":\"开发环境\"}]}";

        // When
        List<FlowConnectorEnvLiteVO> result = parser.parseEnvironments(config);

        // Then
        assertEquals(1, result.size());
        assertNull(result.get(0).getActiveStatus());
    }

    // ==================== parseEnvironmentSchema 测试用例 ====================

    @Test
    void testParseEnvironmentSchema_withNullConfig() {
        // Given
        String nullConfig = null;
        String envCode = "DEV";

        // When & Then
        assertThrows(ServiceException.class, () -> {
            parser.parseEnvironmentSchema(nullConfig, envCode);
        });
    }

    @Test
    void testParseEnvironmentSchema_withEmptyConfig() {
        // Given
        String emptyConfig = "";
        String envCode = "DEV";

        // When & Then
        assertThrows(ServiceException.class, () -> {
            parser.parseEnvironmentSchema(emptyConfig, envCode);
        });
    }

    @Test
    void testParseEnvironmentSchema_withoutProperties() {
        // Given
        String config = "{\"type\":\"HTTP\"}";
        String envCode = "DEV";

        // When & Then
        assertThrows(ServiceException.class, () -> {
            parser.parseEnvironmentSchema(config, envCode);
        });
    }

    @Test
    void testParseEnvironmentSchema_envCodeNotExists() {
        // Given
        String config = "{" +
                "\"properties\":{" +
                "\"DEV\":{\"type\":\"object\"}," +
                "\"TEST\":{\"type\":\"object\"}" +
                "}}";
        String envCode = "PROD";

        // When & Then
        assertThrows(ServiceException.class, () -> {
            parser.parseEnvironmentSchema(config, envCode);
        });
    }

    @Test
    void testParseEnvironmentSchema_success() {
        // Given
        String config = "{" +
                "\"type\":\"HTTP\"," +
                "\"properties\":{" +
                "\"DEV\":{" +
                "\"type\":\"object\"," +
                "\"title\":\"动作1\"," +
                "\"properties\":{" +
                "\"headers\":{\"type\":\"object\"}" +
                "}" +
                "}" +
                "}}";
        String envCode = "DEV";

        // When
        JsonNode result = parser.parseEnvironmentSchema(config, envCode);

        // Then
        assertNotNull(result);
        assertEquals("object", result.get("type").asText());
        assertEquals("动作1", result.get("title").asText());
        assertNotNull(result.get("properties"));
    }

    @Test
    void testParseEnvironmentSchema_prodEnv() {
        // Given
        String config = "{" +
                "\"type\":\"HTTP\"," +
                "\"properties\":{" +
                "\"PROD\":{" +
                "\"type\":\"object\"," +
                "\"title\":\"动作3：获取客户订单记录\"," +
                "\"x-api-meta\":{" +
                "\"path\":\"/api/v1/customers/{customerId}/orders\"," +
                "\"method\":\"GET\"" +
                "}" +
                "}" +
                "}}";
        String envCode = "PROD";

        // When
        JsonNode result = parser.parseEnvironmentSchema(config, envCode);

        // Then
        assertNotNull(result);
        assertEquals("object", result.get("type").asText());
        assertEquals("动作3：获取客户订单记录", result.get("title").asText());
        assertNotNull(result.get("x-api-meta"));
        assertEquals("/api/v1/customers/{customerId}/orders",
                result.get("x-api-meta").get("path").asText());
        assertEquals("GET", result.get("x-api-meta").get("method").asText());
    }
}
