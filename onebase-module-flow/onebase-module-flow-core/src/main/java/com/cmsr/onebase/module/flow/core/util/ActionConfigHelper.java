package com.cmsr.onebase.module.flow.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 动作配置辅助类
 * <p>
 * 用于操作动作配置 JSON 数据的解析、查询和管理
 * <p>
 * 核心功能：
 * - 解析动作配置 JSON
 * - 查询、添加、更新、删除动作
 * - 生成动作 ID
 * - 校验动作完整性
 * - 更新元数据
 *
 * @author kanten
 * @since 2026-01-29
 */
@Slf4j
@Component
public class ActionConfigHelper {

    private static final String ACTIONS_KEY = "actions";
    private static final String ACTION_ID_KEY = "actionId";
    private static final String METADATA_KEY = "_metadata";
    private static final String UPDATED_AT_KEY = "updatedAt";
    private static final String VERSION_KEY = "version";

    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     *
     * @param ObjectMapper Jackson JSON 映射器
     */
    public ActionConfigHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 解析动作配置 JSON
     * <p>
     * 支持空字符串和 null 输入，返回空的 JsonNode
     *
     * @param configJson 配置 JSON 字符串
     * @return 解析后的 JsonNode，解析失败返回空节点
     */
    public JsonNode parseActionConfig(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return objectMapper.createObjectNode();
        }

        try {
            return objectMapper.readTree(configJson);
        } catch (JsonProcessingException e) {
            log.error("解析动作配置失败", e);
            return objectMapper.createObjectNode();
        }
    }

    /**
     * 从配置中获取动作列表
     *
     * @param configJson 配置 JSON 字符串
     * @return 动作列表
     */
    public List<JsonNode> getActions(String configJson) {
        List<JsonNode> actions = new ArrayList<>();
        if (configJson == null || configJson.trim().isEmpty()) {
            log.debug("动作配置为空，返回空列表");
            return actions;
        }
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
     * 查找指定动作
     *
     * @param configJson 配置 JSON 字符串
     * @param actionId   动作 ID
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
     * @param configJson 配置 JSON 字符串
     * @param newAction  新动作配置
     * @return 更新后的配置 JSON 字符串
     */
    public String addAction(String configJson, JsonNode newAction) {
        try {
            JsonNode root = parseActionConfig(configJson);
            ObjectNode objectRoot = (ObjectNode) root;

            JsonNode actionsNode = root.get(ACTIONS_KEY);
            ArrayNode actionsArray;

            if (actionsNode == null) {
                actionsArray = objectRoot.putArray(ACTIONS_KEY);
            } else {
                actionsArray = (ArrayNode) actionsNode;
            }

            actionsArray.add(newAction);

            // 更新元数据
            updateMetadata(objectRoot);

            return objectMapper.writeValueAsString(objectRoot);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("配置更新失败", e);
        }
    }

    /**
     * 更新指定动作配置
     *
     * @param configJson    配置 JSON 字符串
     * @param actionId      动作 ID
     * @param updatedAction 更新后的动作配置
     * @return 更新后的配置 JSON 字符串
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

                        // 更新元数据
                        updateMetadata(objectRoot);

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
     * @param configJson 配置 JSON 字符串
     * @param actionId   动作 ID
     * @return 更新后的配置 JSON 字符串
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

                        // 更新元数据
                        updateMetadata(objectRoot);

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
     * 生成新的动作 ID
     * <p>
     * 格式：action-{8位UUID}
     *
     * @return 动作 ID
     */
    public String generateActionId() {
        return "action-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 校验动作完整性
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
    public ValidationResult validateActionCompleteness(JsonNode action) {
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
     * 更新元数据
     * <p>
     * 私有方法，用于更新配置的元数据信息
     * - 更新时间戳
     * - 递增版本号
     *
     * @param root 根节点
     */
    private void updateMetadata(ObjectNode root) {
        ObjectNode metadata = root.has(METADATA_KEY) && root.get(METADATA_KEY).isObject()
            ? (ObjectNode) root.get(METADATA_KEY)
            : root.putObject(METADATA_KEY);

        // 更新时间戳
        metadata.put(UPDATED_AT_KEY, Instant.now().toString());

        // 递增版本号
        int currentVersion = metadata.has(VERSION_KEY) ? metadata.get(VERSION_KEY).asInt() : 0;
        metadata.put(VERSION_KEY, currentVersion + 1);
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
