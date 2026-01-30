package com.cmsr.onebase.module.flow.build.util;

import com.cmsr.onebase.framework.common.exception.ErrorCode;
import com.cmsr.onebase.framework.common.exception.ServiceException;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 连接器配置解析器
 * <p>
 * 用于解析 flow_connector.config 字段中的环境配置信息
 * <p>
 * 核心功能：
 * - 解析环境配置 JSON
 * - 提取 environments 数组
 * - 转换为 FlowConnectorEnvLiteVO 列表
 *
 * @author kanten
 * @since 2026-01-30
 */
@Slf4j
@Component
public class ConnectorConfigParser {

    private static final String ENVIRONMENTS_KEY = "environments";

    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     *
     * @param objectMapper Jackson JSON 映射器
     */
    public ConnectorConfigParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 从配置 JSON 中解析环境配置列表
     * <p>
     * 支持空字符串和 null 输入，返回空列表
     *
     * @param configJson 配置 JSON 字符串
     * @return 环境配置列表
     */
    public List<FlowConnectorEnvLiteVO> parseEnvironments(String configJson) {
        return parseEnvironments(configJson, null);
    }

    /**
     * 从配置 JSON 中解析环境配置列表
     * <p>
     * 支持空字符串和 null 输入，返回空列表
     *
     * @param configJson 配置 JSON 字符串
     * @param typeCode   连接器类型编号
     * @return 环境配置列表
     */
    public List<FlowConnectorEnvLiteVO> parseEnvironments(String configJson, String typeCode) {
        if (configJson == null || configJson.trim().isEmpty()) {
            log.debug("configJson 为空");
            return new ArrayList<>();
        }

        try {
            JsonNode root = objectMapper.readTree(configJson);

            // 获取所有字段名用于日志
            Iterator<String> fieldNames = root.fieldNames();
            StringBuilder fields = new StringBuilder();
            while (fieldNames.hasNext()) {
                if (fields.length() > 0) fields.append(", ");
                fields.append(fieldNames.next());
            }
            log.debug("解析 JSON 成功，root keys: {}", fields);

            JsonNode environmentsNode = root.get(ENVIRONMENTS_KEY);

            if (environmentsNode == null) {
                log.warn("config 中未找到 environments 字段，可用字段: {}", fields);
                return new ArrayList<>();
            }

            if (!environmentsNode.isArray()) {
                log.warn("environments 字段不是数组类型，实际类型: {}", environmentsNode.getNodeType());
                return new ArrayList<>();
            }

            log.info("找到 environments 数组，长度: {}", environmentsNode.size());

            List<FlowConnectorEnvLiteVO> result = new ArrayList<>();
            for (JsonNode envNode : environmentsNode) {
                FlowConnectorEnvLiteVO vo = parseEnvironmentNode(envNode, typeCode);
                if (vo != null) {
                    result.add(vo);
                }
            }

            log.info("成功解析 {} 个环境配置", result.size());
            return result;

        } catch (JsonProcessingException e) {
            log.error("解析环境配置失败，configJson: {}", configJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * 解析单个环境配置节点
     *
     * @param envNode  环境配置节点
     * @param typeCode 连接器类型编号
     * @return 环境配置VO，解析失败返回null
     */
    private FlowConnectorEnvLiteVO parseEnvironmentNode(JsonNode envNode, String typeCode) {
        try {
            FlowConnectorEnvLiteVO vo = new FlowConnectorEnvLiteVO();

            // 解析环境名称
            JsonNode envNameNode = envNode.get("envName");
            if (envNameNode != null && !envNameNode.isNull()) {
                vo.setEnvName(envNameNode.asText());
            }

            // 解析环境编码
            JsonNode envCodeNode = envNode.get("envCode");
            if (envCodeNode != null && !envCodeNode.isNull()) {
                vo.setEnvCode(envCodeNode.asText());
            }

            // 解析环境URL
            JsonNode envUrlNode = envNode.get("envUrl");
            if (envUrlNode != null && !envUrlNode.isNull()) {
                vo.setEnvUrl(envUrlNode.asText());
            }

            // 解析认证方式
            JsonNode authTypeNode = envNode.get("authType");
            if (authTypeNode != null && !authTypeNode.isNull()) {
                vo.setAuthType(authTypeNode.asText());
            }

            // 解析描述
            JsonNode descNode = envNode.get("description");
            if (descNode != null && !descNode.isNull()) {
                vo.setDescription(descNode.asText());
            }

            // 解析启用状态
            JsonNode activeNode = envNode.get("active");
            if (activeNode != null && !activeNode.isNull()) {
                vo.setActiveStatus(activeNode.asBoolean() ? 1 : 0);
            }

            // 设置连接器类型编号
            vo.setTypeCode(typeCode);

            // 设置默认值
            vo.setCreateTime(LocalDateTime.now());

            return vo;

        } catch (Exception e) {
            log.error("解析环境配置节点失败", e);
            return null;
        }
    }

    /**
     * 从配置 JSON 中解析指定环境的配置 Schema
     * <p>
     * 从 flow_connector.config 的 properties 中提取 properties[envCode] 的值
     *
     * @param configJson 配置 JSON 字符串
     * @param envCode    环境编码（如 DEV、TEST、PROD）
     * @return 该环境的配置 Schema（JsonNode）
     * @throws ServiceException 如果环境配置不存在
     */
    public JsonNode parseEnvironmentSchema(String configJson, String envCode) {
        if (configJson == null || configJson.trim().isEmpty()) {
            throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envCode=" + envCode));
        }

        try {
            JsonNode root = objectMapper.readTree(configJson);

            // 获取 properties 节点
            JsonNode propertiesNode = root.get("properties");
            if (propertiesNode == null || !propertiesNode.isObject()) {
                log.warn("config 中未找到 properties 字段或其不是对象类型");
                throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envCode=" + envCode));
            }

            // 获取指定环境的配置
            JsonNode envSchemaNode = propertiesNode.get(envCode);
            if (envSchemaNode == null || envSchemaNode.isNull()) {
                log.warn("环境配置不存在，envCode: {}, 可用环境: {}", envCode,
                        getFieldNames(propertiesNode));
                throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envCode=" + envCode));
            }

            log.info("成功解析环境配置，envCode: {}", envCode);
            return envSchemaNode;

        } catch (JsonProcessingException e) {
            log.error("解析配置 JSON 失败，configJson: {}", configJson, e);
            throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envCode=" + envCode));
        }
    }

    /**
     * 获取 JsonNode 的所有字段名，用于错误提示
     *
     * @param node JsonNode 节点
     * @return 字段名列表
     */
    private List<String> getFieldNames(JsonNode node) {
        List<String> names = new ArrayList<>();
        Iterator<String> it = node.fieldNames();
        while (it.hasNext()) {
            names.add(it.next());
        }
        return names;
    }
}
