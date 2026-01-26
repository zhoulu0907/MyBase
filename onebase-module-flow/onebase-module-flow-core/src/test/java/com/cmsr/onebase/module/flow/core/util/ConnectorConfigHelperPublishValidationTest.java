package com.cmsr.onebase.module.flow.core.util;

import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConnectorConfigHelper 发布校验单元测试
 * <p>
 * 测试动作发布前的完整性校验逻辑
 */
class ConnectorConfigHelperPublishValidationTest {

    private ConnectorConfigHelper helper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        helper = new ConnectorConfigHelper(objectMapper);
    }

    @Test
    void testValidateForPublish_CompleteAction_Passes() {
        // Given - 完整的动作配置
        ObjectNode action = objectMapper.createObjectNode();
        action.set("基础信息", createCompleteBasicInfo());
        action.set("入参配置", createCompleteInputConfig());
        action.set("出参配置", createCompleteOutputConfig());
        action.set("调试配置", createCompleteDebugConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testValidateForPublish_MissingBasicInfo_Fails() {
        // Given - 缺少基础信息
        ObjectNode action = objectMapper.createObjectNode();
        action.set("入参配置", createCompleteInputConfig());
        action.set("出参配置", createCompleteOutputConfig());
        action.set("调试配置", createCompleteDebugConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请填写基础信息"));
    }

    @Test
    void testValidateForPublish_EmptyBasicInfo_Fails() {
        // Given - 基础信息为空对象
        ObjectNode action = objectMapper.createObjectNode();
        action.set("基础信息", objectMapper.createObjectNode());
        action.set("入参配置", createCompleteInputConfig());
        action.set("出参配置", createCompleteOutputConfig());
        action.set("调试配置", createCompleteDebugConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请填写基础信息"));
    }

    @Test
    void testValidateForPublish_MissingInputConfig_Fails() {
        // Given - 缺少入参配置
        ObjectNode action = objectMapper.createObjectNode();
        action.set("基础信息", createCompleteBasicInfo());
        action.set("出参配置", createCompleteOutputConfig());
        action.set("调试配置", createCompleteDebugConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请配置入参"));
    }

    @Test
    void testValidateForPublish_MissingOutputConfig_Fails() {
        // Given - 缺少出参配置
        ObjectNode action = objectMapper.createObjectNode();
        action.set("基础信息", createCompleteBasicInfo());
        action.set("入参配置", createCompleteInputConfig());
        action.set("调试配置", createCompleteDebugConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请配置出参"));
    }

    @Test
    void testValidateForPublish_MissingDebugConfig_Fails() {
        // Given - 缺少调试配置
        ObjectNode action = objectMapper.createObjectNode();
        action.set("基础信息", createCompleteBasicInfo());
        action.set("入参配置", createCompleteInputConfig());
        action.set("出参配置", createCompleteOutputConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请填写调试配置"));
    }

    @Test
    void testValidateForPublish_AllMissing_FailsWithMultipleErrors() {
        // Given - 所有配置都缺失
        ObjectNode action = objectMapper.createObjectNode();

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        List<String> errors = result.getErrors();
        assertEquals(4, errors.size());
        assertTrue(errors.contains("请填写基础信息"));
        assertTrue(errors.contains("请配置入参"));
        assertTrue(errors.contains("请配置出参"));
        assertTrue(errors.contains("请填写调试配置"));
    }

    @Test
    void testValidateForPublish_MultipleErrors_FailsWithAllErrors() {
        // Given - 缺少基础信息和出参配置
        ObjectNode action = objectMapper.createObjectNode();
        action.set("入参配置", createCompleteInputConfig());
        action.set("调试配置", createCompleteDebugConfig());

        // When
        ValidationResult result = helper.validateForPublish(action);

        // Then
        assertFalse(result.isValid());
        List<String> errors = result.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains("请填写基础信息"));
        assertTrue(errors.contains("请配置出参"));
    }

    // ==================== 辅助方法 ====================

    private JsonNode createCompleteBasicInfo() {
        ObjectNode basicInfo = objectMapper.createObjectNode();
        basicInfo.put("httpMethod", "GET");
        basicInfo.put("path", "/api/users");
        return basicInfo;
    }

    private JsonNode createCompleteInputConfig() {
        ObjectNode inputConfig = objectMapper.createObjectNode();
        inputConfig.put("pathParams", "[]");
        inputConfig.put("queryParams", "[]");
        return inputConfig;
    }

    private JsonNode createCompleteOutputConfig() {
        ObjectNode outputConfig = objectMapper.createObjectNode();
        outputConfig.put("responseMapping", "[]");
        return outputConfig;
    }

    private JsonNode createCompleteDebugConfig() {
        ObjectNode debugConfig = objectMapper.createObjectNode();
        debugConfig.put("testParams", "{}");
        return debugConfig;
    }
}
