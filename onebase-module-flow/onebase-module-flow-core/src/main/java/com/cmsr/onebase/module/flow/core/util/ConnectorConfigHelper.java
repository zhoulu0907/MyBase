package com.cmsr.onebase.module.flow.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 连接器配置辅助类
 * <p>
 * 用于操作 flow_connector.config JSON 字段中的动作配置
 * <p>
 * 核心功能：
 * - 从 JSON 配置中提取/查找/添加/更新/删除动作
 * - 校验动作是否可以发布（完整性检查）
 *
 * @author onebase
 * @since 2026-01-26
 */
@Slf4j
public class ConnectorConfigHelper {

    private static final String ACTIONS_KEY = "动作配置";
    private static final String ACTION_ID_KEY = "actionId";
    private static final String STATUS_KEY = "status";
    private static final String VERSION_KEY = "version";

    private final ObjectMapper objectMapper;

    public ConnectorConfigHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 从配置 JSON 中提取动作列表
     *
     * @param configJson 配置JSON字符串
     * @return 动作列表
     */
    public List<JsonNode> getActions(String configJson) {
        List<JsonNode> actions = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(configJson);
            JsonNode actionsNode = root.get(ACTIONS_KEY);

            if (actionsNode != null && actionsNode.isArray()) {
                ArrayNode array = (ArrayNode) actionsNode;
                Iterator<JsonNode> elements = array.elements();
                while (elements.hasNext()) {
                    actions.add(elements.next());
                }
            }
        } catch (JsonProcessingException e) {
            log.error("解析动作配置失败", e);
        }
        return actions;
    }

    /**
     * 从配置 JSON 中查找指定动作
     *
     * @param configJson 配置JSON字符串
     * @param actionId   动作ID
     * @return 动作配置，不存在返回 null
     */
    public JsonNode findAction(String configJson, String actionId) {
        try {
            JsonNode root = objectMapper.readTree(configJson);
            JsonNode actionsNode = root.get(ACTIONS_KEY);

            if (actionsNode != null && actionsNode.isArray()) {
                ArrayNode array = (ArrayNode) actionsNode;
                for (JsonNode action : array) {
                    if (actionId.equals(action.get(ACTION_ID_KEY).asText())) {
                        return action;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("查找动作配置失败, actionId={}", actionId, e);
        }
        return null;
    }

    /**
     * 添加新动作到配置
     *
     * @param configJson 配置JSON字符串
     * @param newAction  新动作配置
     * @return 更新后的配置JSON字符串
     */
    public String addAction(String configJson, JsonNode newAction) {
        try {
            JsonNode root = objectMapper.readTree(configJson);
            ObjectNode objectRoot = (ObjectNode) root;

            JsonNode actionsNode = root.get(ACTIONS_KEY);
            ArrayNode actionsArray;

            if (actionsNode == null) {
                actionsArray = objectRoot.putArray(ACTIONS_KEY);
            } else {
                actionsArray = (ArrayNode) actionsNode;
            }

            actionsArray.add(newAction);
            return objectMapper.writeValueAsString(objectRoot);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置更新失败", e);
        }
    }

    /**
     * 更新指定动作配置
     *
     * @param configJson    配置JSON字符串
     * @param actionId      动作ID
     * @param updatedAction 更新后的动作配置
     * @return 更新后的配置JSON字符串
     */
    public String updateAction(String configJson, String actionId, JsonNode updatedAction) {
        try {
            JsonNode root = objectMapper.readTree(configJson);
            ObjectNode objectRoot = (ObjectNode) root;
            JsonNode actionsNode = root.get(ACTIONS_KEY);

            if (actionsNode != null && actionsNode.isArray()) {
                ArrayNode array = (ArrayNode) actionsNode;
                for (int i = 0; i < array.size(); i++) {
                    JsonNode action = array.get(i);
                    if (actionId.equals(action.get(ACTION_ID_KEY).asText())) {
                        array.set(i, updatedAction);
                        return objectMapper.writeValueAsString(objectRoot);
                    }
                }
            }

            throw new RuntimeException("动作不存在: " + actionId);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置更新失败", e);
        }
    }

    /**
     * 删除指定动作
     *
     * @param configJson 配置JSON字符串
     * @param actionId   动作ID
     * @return 更新后的配置JSON字符串
     */
    public String removeAction(String configJson, String actionId) {
        try {
            JsonNode root = objectMapper.readTree(configJson);
            ObjectNode objectRoot = (ObjectNode) root;
            JsonNode actionsNode = root.get(ACTIONS_KEY);

            if (actionsNode != null && actionsNode.isArray()) {
                ArrayNode array = (ArrayNode) actionsNode;
                for (int i = 0; i < array.size(); i++) {
                    JsonNode action = array.get(i);
                    if (actionId.equals(action.get(ACTION_ID_KEY).asText())) {
                        array.remove(i);
                        return objectMapper.writeValueAsString(objectRoot);
                    }
                }
            }

            throw new RuntimeException("动作不存在: " + actionId);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置更新失败", e);
        }
    }

    /**
     * 生成新的动作ID
     *
     * @return 动作ID
     */
    public String generateActionId() {
        return "action-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 验证动作是否可以发布
     * <p>
     * 校验规则：
     * 1. 基础信息不能为空
     * 2. 入参配置不能为空
     * 3. 出参配置不能为空
     * 4. 调试配置不能为空
     *
     * @param action 动作配置
     * @return 验证结果
     */
    public ValidationResult validateForPublish(JsonNode action) {
        List<String> errors = new ArrayList<>();

        // 验证基础信息
        JsonNode basicInfo = action.get("基础信息");
        if (basicInfo == null || basicInfo.isEmpty()) {
            errors.add("请填写基础信息");
        }

        // 验证入参配置
        JsonNode inputConfig = action.get("入参配置");
        if (inputConfig == null || inputConfig.isEmpty()) {
            errors.add("请配置入参");
        }

        // 验证出参配置
        JsonNode outputConfig = action.get("出参配置");
        if (outputConfig == null || outputConfig.isEmpty()) {
            errors.add("请配置出参");
        }

        // 验证调试配置
        JsonNode debugConfig = action.get("调试配置");
        if (debugConfig == null || debugConfig.isEmpty()) {
            errors.add("请填写调试配置");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}
