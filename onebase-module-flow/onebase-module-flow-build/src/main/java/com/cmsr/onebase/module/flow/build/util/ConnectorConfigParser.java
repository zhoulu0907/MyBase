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
import java.util.Collections;
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
     * <p>
     * 从 flow_connector.config 的 properties 字段中解析环境配置
     *
     * @param configJson 配置 JSON 字符串
     * @param typeCode   连接器类型编号
     * @return 环境配置列表
     */
    public List<FlowConnectorEnvLiteVO> parseEnvironments(String configJson, String typeCode) {
        if (configJson == null || configJson.trim().isEmpty()) {
            log.debug("configJson 为空");
            return Collections.emptyList();
        }

        try {
            JsonNode root = objectMapper.readTree(configJson);
            log.debug("解析 JSON 成功，root keys: {}", collectFieldNames(root));

            // 从 properties 字段获取环境配置
            JsonNode propertiesNode = root.get("properties");
            if (propertiesNode == null) {
                log.warn("config 中未找到 properties 字段，可用字段: {}", collectFieldNames(root));
                return Collections.emptyList();
            }

            if (!propertiesNode.isObject()) {
                log.warn("properties 字段不是对象类型，实际类型: {}", propertiesNode.getNodeType());
                return Collections.emptyList();
            }

            log.info("找到 properties 对象，字段数: {}", propertiesNode.size());

            // 遍历 properties 中的每个环境配置（DEV、TEST、PROD 等）
            List<FlowConnectorEnvLiteVO> result = new ArrayList<>();
            Iterator<String> envCodes = propertiesNode.fieldNames();
            while (envCodes.hasNext()) {
                String envCode = envCodes.next();
                JsonNode envSchemaNode = propertiesNode.get(envCode);

                // 从 Formily Schema 中提取环境信息
                FlowConnectorEnvLiteVO vo = parseEnvSchemaNode(envSchemaNode, envCode, typeCode);
                if (vo != null) {
                    result.add(vo);
                }
            }

            log.info("成功解析 {} 个环境配置", result.size());
            return result;

        } catch (JsonProcessingException e) {
            log.error("解析环境配置失败，configJson: {}", configJson, e);
            return Collections.emptyList();
        }
    }

    /**
     * 收集 JsonNode 的所有字段名用于日志
     *
     * @param node JSON 节点
     * @return 逗号分隔的字段名字符串
     */
    private String collectFieldNames(JsonNode node) {
        Iterator<String> fieldNames = node.fieldNames();
        StringBuilder sb = new StringBuilder();
        while (fieldNames.hasNext()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(fieldNames.next());
        }
        return sb.toString();
    }

    /**
     * 从环境配置节点解析环境配置信息
     * <p>
     * 新格式：从 envConfig.basicInfo 和 envConfig.authInfo 中提取
     *
     * @param envSchemaNode 环境配置节点（properties[envName] 的值）
     * @param envCode      环境编码（作为 properties 的 key）
     * @param typeCode     连接器类型编号
     * @return 环境配置VO，解析失败返回null
     */
    private FlowConnectorEnvLiteVO parseEnvSchemaNode(JsonNode envSchemaNode, String envCode, String typeCode) {
        try {
            FlowConnectorEnvLiteVO vo = new FlowConnectorEnvLiteVO();

            // 从 envConfig 中提取 basicInfo 和 authInfo
            JsonNode envConfigNode = envSchemaNode.get("envConfig");
            if (envConfigNode == null || !envConfigNode.isObject()) {
                log.warn("envConfig node is null or not object, envCode: {}, skipping", envCode);
                return null;
            }

            // 从 basicInfo 提取环境信息
            JsonNode basicInfoNode = envConfigNode.get("basicInfo");
            if (basicInfoNode != null && basicInfoNode.isObject()) {
                // 环境名称
                JsonNode envNameNode = basicInfoNode.get("envName");
                if (envNameNode != null && !envNameNode.isNull()) {
                    vo.setEnvName(envNameNode.asText());
                }

                // 环境编码
                JsonNode envCodeNode = basicInfoNode.get("envCode");
                if (envCodeNode != null && !envCodeNode.isNull()) {
                    vo.setEnvCode(envCodeNode.asText());
                }

                // 环境 URL
                JsonNode baseUrlNode = basicInfoNode.get("baseUrl");
                if (baseUrlNode != null && !baseUrlNode.isNull()) {
                    vo.setEnvUrl(baseUrlNode.asText());
                }
            }

            // 从 authInfo 提取认证方式
            JsonNode authInfoNode = envConfigNode.get("authInfo");
            if (authInfoNode != null && authInfoNode.isObject()) {
                JsonNode authTypeNode = authInfoNode.get("authType");
                if (authTypeNode != null && !authTypeNode.isNull()) {
                    vo.setAuthType(authTypeNode.asText());
                }
            }

            // 从 envMode 提取模式信息
            JsonNode envModeNode = envSchemaNode.get("envMode");
            if (envModeNode != null && !envModeNode.isNull()) {
                String envMode = envModeNode.asText();
                vo.setDescription("模式: " + envMode);
            }

            // 设置连接器类型编号
            vo.setTypeCode(typeCode);

            // 设置默认值
            vo.setCreateTime(LocalDateTime.now());
            vo.setActiveStatus(1); // 默认启用

            log.info("成功解析环境配置，envCode: {}, envName: {}", vo.getEnvCode(), vo.getEnvName());
            return vo;

        } catch (Exception e) {
            log.error("解析环境 Schema 节点失败，envCode: {}", envCode, e);
            return null;
        }
    }

    /**
     * 从配置 JSON 中解析指定环境的配置 Schema
     * <p>
     * 从 flow_connector.config 的 properties 中提取 properties[envName] 的值
     *
     * @param configJson 配置 JSON 字符串
     * @param envName    环境名称（如 DEV环境配置）
     * @return 该环境的配置 Schema（JsonNode）
     * @throws ServiceException 如果环境配置不存在
     */
    public JsonNode parseEnvironmentSchema(String configJson, String envName) {
        if (configJson == null || configJson.trim().isEmpty()) {
            throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envName=" + envName));
        }

        try {
            JsonNode root = objectMapper.readTree(configJson);

            // 获取 properties 节点
            JsonNode propertiesNode = root.get("properties");
            if (propertiesNode == null || !propertiesNode.isObject()) {
                log.warn("config 中未找到 properties 字段或其不是对象类型");
                throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envName=" + envName));
            }

            // 获取指定环境的配置
            JsonNode envSchemaNode = propertiesNode.get(envName);
            if (envSchemaNode == null || envSchemaNode.isNull()) {
                log.warn("环境配置不存在，envName: {}, 可用环境: {}", envName,
                        collectFieldNames(propertiesNode));
                throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envName=" + envName));
            }

            log.info("成功解析环境配置，envName: {}", envName);
            return envSchemaNode;

        } catch (JsonProcessingException e) {
            log.error("解析配置 JSON 失败，configJson: {}", configJson, e);
            throw new ServiceException(new ErrorCode(1123788, "环境配置不存在：envName=" + envName));
        }
    }
}
