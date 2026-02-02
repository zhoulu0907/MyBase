package com.cmsr.onebase.module.flow.core.util;

import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper.ValidationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ActionConfigHelper 单元测试
 * <p>
 * 测试动作配置的解析、查询、管理和校验功能
 *
 * @author kanten
 * @since 2026-01-29
 */
class ActionConfigHelperTest {

    private ActionConfigHelper helper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        helper = new ActionConfigHelper(objectMapper);
    }

    // ==================== parseActionConfig 测试用例 ====================

    @Test
    void testParseActionConfig_withEmptyString() {
        // Given
        String emptyConfig = "";

        // When
        JsonNode result = helper.parseActionConfig(emptyConfig);

        // Then
        assertNotNull(result);
        assertTrue(result.isObject());
        assertFalse(result.has("actions"));
    }

    @Test
    void testParseActionConfig_withNull() {
        // Given
        String nullConfig = null;

        // When
        JsonNode result = helper.parseActionConfig(nullConfig);

        // Then
        assertNotNull(result);
        assertTrue(result.isObject());
        assertFalse(result.has("actions"));
    }

    @Test
    void testParseActionConfig_withValidJson() {
        // Given
        String validJson = "{\"actions\":[]}";

        // When
        JsonNode result = helper.parseActionConfig(validJson);

        // Then
        assertNotNull(result);
        assertTrue(result.has("actions"));
        assertTrue(result.get("actions").isArray());
    }

    @Test
    void testParseActionConfig_withInvalidJson() {
        // Given
        String invalidJson = "{invalid json}";

        // When
        JsonNode result = helper.parseActionConfig(invalidJson);

        // Then
        assertNotNull(result);
        assertTrue(result.isObject());
    }

    // ==================== generateActionId 测试用例 ====================

    @Test
    void testGenerateActionId() {
        // When
        String actionId = helper.generateActionId();

        // Then
        assertNotNull(actionId);
        assertTrue(actionId.startsWith("action-"));
        assertEquals(15, actionId.length()); // "action-" + 8位UUID
    }

    @Test
    void testGenerateActionId_unique() {
        // When
        String id1 = helper.generateActionId();
        String id2 = helper.generateActionId();

        // Then
        assertNotEquals(id1, id2);
    }

    // ==================== getActions 测试用例 ====================

    @Test
    void testGetActions_fromEmptyConfig() {
        // Given
        String emptyConfig = "{}";

        // When
        List<JsonNode> actions = helper.getActions(emptyConfig);

        // Then
        assertNotNull(actions);
        assertTrue(actions.isEmpty());
    }

    @Test
    void testGetActions_fromConfigWithActions() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\"},{\"actionId\":\"action-456\"}]}";

        // When
        List<JsonNode> actions = helper.getActions(config);

        // Then
        assertEquals(2, actions.size());
        assertEquals("action-123", actions.get(0).get("actionId").asText());
        assertEquals("action-456", actions.get(1).get("actionId").asText());
    }

    @Test
    void testGetActions_withInvalidJson() {
        // Given
        String invalidConfig = "{invalid}";

        // When
        List<JsonNode> actions = helper.getActions(invalidConfig);

        // Then
        assertNotNull(actions);
        assertTrue(actions.isEmpty());
    }

    // ==================== addAction 测试用例 ====================

    @Test
    void testAddAction() {
        // Given
        String config = "{\"actions\":[]}";
        ObjectNode newAction = objectMapper.createObjectNode();
        newAction.put("actionId", "action-new");
        newAction.put("name", "新动作");

        // When
        String result = helper.addAction(config, newAction);

        // Then
        assertTrue(result.contains("\"actionId\":\"action-new\""));
        assertTrue(result.contains("\"name\":\"新动作\""));
        assertTrue(result.contains("\"_metadata\""));
    }

    @Test
    void testAddAction_toEmptyConfig() {
        // Given
        String config = "{}";
        ObjectNode newAction = objectMapper.createObjectNode();
        newAction.put("actionId", "action-new");

        // When
        String result = helper.addAction(config, newAction);

        // Then
        assertTrue(result.contains("\"actions\":"));
        assertTrue(result.contains("\"actionId\":\"action-new\""));
    }

    // ==================== findAction 测试用例 ====================

    @Test
    void testFindAction() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\",\"name\":\"动作1\"},{\"actionId\":\"action-456\",\"name\":\"动作2\"}]}";

        // When
        JsonNode result = helper.findAction(config, "action-123");

        // Then
        assertNotNull(result);
        assertEquals("action-123", result.get("actionId").asText());
        assertEquals("动作1", result.get("name").asText());
    }

    @Test
    void testFindAction_notFound() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\"}]}";

        // When
        JsonNode result = helper.findAction(config, "action-not-exist");

        // Then
        assertNull(result);
    }

    @Test
    void testFindAction_withInvalidJson() {
        // Given
        String invalidConfig = "{invalid}";

        // When
        JsonNode result = helper.findAction(invalidConfig, "action-123");

        // Then
        assertNull(result);
    }

    // ==================== updateAction 测试用例 ====================

    @Test
    void testUpdateAction() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\",\"name\":\"旧名称\"}]}";
        ObjectNode updatedAction = objectMapper.createObjectNode();
        updatedAction.put("actionId", "action-123");
        updatedAction.put("name", "新名称");

        // When
        String result = helper.updateAction(config, "action-123", updatedAction);

        // Then
        assertTrue(result.contains("\"name\":\"新名称\""));
        assertFalse(result.contains("\"name\":\"旧名称\""));
        assertTrue(result.contains("\"_metadata\""));
    }

    @Test
    void testUpdateAction_notFound() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\"}]}";
        ObjectNode updatedAction = objectMapper.createObjectNode();
        updatedAction.put("actionId", "action-not-exist");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            helper.updateAction(config, "action-not-exist", updatedAction);
        });
    }

    // ==================== removeAction 测试用例 ====================

    @Test
    void testRemoveAction() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\"},{\"actionId\":\"action-456\"}]}";

        // When
        String result = helper.removeAction(config, "action-123");

        // Then
        assertFalse(result.contains("\"actionId\":\"action-123\""));
        assertTrue(result.contains("\"actionId\":\"action-456\""));
        assertTrue(result.contains("\"_metadata\""));
    }

    @Test
    void testRemoveAction_notFound() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\"}]}";

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            helper.removeAction(config, "action-not-exist");
        });
    }

    // ==================== validateActionCompleteness 测试用例 ====================

    @Test
    void testValidateActionCompleteness_withEmptyAction() {
        // Given
        ObjectNode action = objectMapper.createObjectNode();

        // When
        ValidationResult result = helper.validateActionCompleteness(action);

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
    void testValidateActionCompleteness_withCompleteAction() {
        // Given
        ObjectNode action = createCompleteAction();

        // When
        ValidationResult result = helper.validateActionCompleteness(action);

        // Then
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testValidateActionCompleteness_missingBasicInfo() {
        // Given
        ObjectNode action = objectMapper.createObjectNode();
        action.set("入参配置", objectMapper.createObjectNode());
        action.set("出参配置", objectMapper.createObjectNode());
        action.set("调试配置", objectMapper.createObjectNode());

        // When
        ValidationResult result = helper.validateActionCompleteness(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请填写基础信息"));
    }

    @Test
    void testValidateActionCompleteness_withEmptyBasicInfo() {
        // Given
        ObjectNode action = objectMapper.createObjectNode();
        action.set("基础信息", objectMapper.createObjectNode());
        action.set("入参配置", objectMapper.createObjectNode());
        action.set("出参配置", objectMapper.createObjectNode());
        action.set("调试配置", objectMapper.createObjectNode());

        // When
        ValidationResult result = helper.validateActionCompleteness(action);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("请填写基础信息"));
    }

    // ==================== metadata update 测试用例 ====================

    @Test
    void testMetadataUpdate_afterAddAction() {
        // Given
        String config = "{\"actions\":[]}";
        ObjectNode newAction = objectMapper.createObjectNode();
        newAction.put("actionId", "action-new");

        // When
        String result = helper.addAction(config, newAction);

        // Then
        assertTrue(result.contains("\"_metadata\""));
        assertTrue(result.contains("\"updatedAt\""));
        assertTrue(result.contains("\"version\":1"));
    }

    @Test
    void testMetadataUpdate_afterUpdateAction() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\",\"name\":\"旧名称\"}],\"_metadata\":{\"version\":5}}";
        ObjectNode updatedAction = objectMapper.createObjectNode();
        updatedAction.put("actionId", "action-123");
        updatedAction.put("name", "新名称");

        // When
        String result = helper.updateAction(config, "action-123", updatedAction);

        // Then
        assertTrue(result.contains("\"_metadata\""));
        assertTrue(result.contains("\"version\":6"));
    }

    @Test
    void testMetadataUpdate_afterRemoveAction() {
        // Given
        String config = "{\"actions\":[{\"actionId\":\"action-123\"}],\"_metadata\":{\"version\":2}}";

        // When
        String result = helper.removeAction(config, "action-123");

        // Then
        assertTrue(result.contains("\"_metadata\""));
        assertTrue(result.contains("\"version\":3"));
    }

    // ==================== 辅助方法 ====================

    private ObjectNode createCompleteAction() {
        ObjectNode action = objectMapper.createObjectNode();

        // 基础信息
        ObjectNode basicInfo = objectMapper.createObjectNode();
        basicInfo.put("name", "测试动作");
        basicInfo.put("httpMethod", "GET");
        action.set("基础信息", basicInfo);

        // 入参配置
        ObjectNode inputConfig = objectMapper.createObjectNode();
        inputConfig.put("pathParams", "[]");
        action.set("入参配置", inputConfig);

        // 出参配置
        ObjectNode outputConfig = objectMapper.createObjectNode();
        outputConfig.put("responseMapping", "[]");
        action.set("出参配置", outputConfig);

        // 调试配置
        ObjectNode debugConfig = objectMapper.createObjectNode();
        debugConfig.put("testParams", "{}");
        action.set("调试配置", debugConfig);

        return action;
    }
}
