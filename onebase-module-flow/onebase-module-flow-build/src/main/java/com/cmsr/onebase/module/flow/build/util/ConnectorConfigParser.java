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

            // 从 properties 字段获取环境配置
            JsonNode propertiesNode = root.get("properties");

            if (propertiesNode == null) {
                log.warn("config 中未找到 properties 字段，可用字段: {}", fields);
                return new ArrayList<>();
            }

            if (!propertiesNode.isObject()) {
                log.warn("properties 字段不是对象类型，实际类型: {}", propertiesNode.getNodeType());
                return new ArrayList<>();
            }

            log.info("找到 properties 对象，字段数: {}", propertiesNode.size());

            List<FlowConnectorEnvLiteVO> result = new ArrayList<>();
            // 遍历 properties 中的每个环境配置（DEV、TEST、PROD 等）
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
            return new ArrayList<>();
        }
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

            // 新格式：envSchemaNode 包含 envMode 和 envConfig
            // 从 envConfig 中提取 basicInfo 和 authInfo
            JsonNode envConfigNode = envSchemaNode.get("envConfig");
            if (envConfigNode == null || !envConfigNode.isObject()) {
                log.warn("envConfig node is null or not object, envCode: {}", envCode);
                // 尝试旧格式（Formily Schema）
                return parseLegacyEnvSchemaNode(envSchemaNode, envCode, typeCode);
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
     * 从旧版 Formily Schema 节点解析环境配置信息（兼容旧格式）
     *
     * @param envSchemaNode 环境的 Formily Schema 节点
     * @param envCode      环境编码
     * @param typeCode     连接器类型编号
     * @return 环境配置VO，解析失败返回null
     */
    private FlowConnectorEnvLiteVO parseLegacyEnvSchemaNode(JsonNode envSchemaNode, String envCode, String typeCode) {
        try {
            FlowConnectorEnvLiteVO vo = new FlowConnectorEnvLiteVO();

            // 使用环境编码作为环境名称
            vo.setEnvCode(envCode);

            // 从 x-api-meta 提取信息
            JsonNode apiMetaNode = envSchemaNode.get("x-api-meta");
            if (apiMetaNode != null && !apiMetaNode.isNull()) {
                // 提取 API 路径作为环境 URL
                JsonNode pathNode = apiMetaNode.get("path");
                if (pathNode != null && !pathNode.isNull()) {
                    vo.setEnvUrl(pathNode.asText());
                }

                // 提取方法描述作为描述
                JsonNode methodNode = apiMetaNode.get("method");
                JsonNode summaryNode = apiMetaNode.get("summary");
                if (summaryNode != null && !summaryNode.isNull()) {
                    String desc = summaryNode.asText();
                    if (methodNode != null && !methodNode.isNull()) {
                        desc = methodNode.asText() + " " + desc;
                    }
                    vo.setDescription(desc);
                } else if (methodNode != null && !methodNode.isNull()) {
                    vo.setDescription(methodNode.asText());
                }
            }

            // 从 title 字段提取环境名称（如果没有则使用 envCode）
            JsonNode titleNode = envSchemaNode.get("title");
            if (titleNode != null && !titleNode.isNull()) {
                vo.setEnvName(titleNode.asText());
            } else {
                vo.setEnvName(envCode);
            }

            // 从 description 字段提取描述（如果没有则从 x-api-meta 提取）
            JsonNode descNode = envSchemaNode.get("description");
            if (descNode != null && !descNode.isNull() && vo.getDescription() == null) {
                vo.setDescription(descNode.asText());
            }

            // 设置连接器类型编号
            vo.setTypeCode(typeCode);

            // 设置默认值
            vo.setCreateTime(LocalDateTime.now());
            vo.setActiveStatus(1); // 默认启用

            return vo;

        } catch (Exception e) {
            log.error("解析旧版环境 Schema 节点失败，envCode: {}", envCode, e);
            return null;
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
