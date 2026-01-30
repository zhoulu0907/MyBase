package com.cmsr.onebase.module.flow.build.util;

import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            return new ArrayList<>();
        }

        try {
            JsonNode root = objectMapper.readTree(configJson);
            JsonNode environmentsNode = root.get(ENVIRONMENTS_KEY);

            if (environmentsNode == null || !environmentsNode.isArray()) {
                log.debug("config 中未找到 environments 数组节点");
                return new ArrayList<>();
            }

            List<FlowConnectorEnvLiteVO> result = new ArrayList<>();
            for (JsonNode envNode : environmentsNode) {
                FlowConnectorEnvLiteVO vo = parseEnvironmentNode(envNode, typeCode);
                if (vo != null) {
                    result.add(vo);
                }
            }

            return result;

        } catch (JsonProcessingException e) {
            log.error("解析环境配置失败", e);
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
}
