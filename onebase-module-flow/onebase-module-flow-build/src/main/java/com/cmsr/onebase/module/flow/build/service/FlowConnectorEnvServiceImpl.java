package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.build.util.ConnectorConfigParser;
import com.cmsr.onebase.module.flow.build.vo.EnvConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.SaveEnvironmentConfigReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接器环境配置Service实现
 *
 * @author onebase
 * @since 2026-03-20
 */
@Slf4j
@Service
public class FlowConnectorEnvServiceImpl implements FlowConnectorEnvService {

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Autowired
    private FlowNodeConfigRepository flowNodeConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConnectorConfigParser connectorConfigParser;

    @Override
    public List<FlowConnectorEnvLiteVO> getEnvironments(Long connectorId) {
        log.info("getEnvironments start, connectorId: {}", connectorId);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}, returning empty list", connectorId);
            return new ArrayList<>();
        }

        // 2. 获取 config 字段
        String configJson = connector.getConfig();
        log.info("Connector config content, connectorId: {}, config: {}, configLength: {}",
                connectorId, configJson, configJson != null ? configJson.length() : 0);

        if (StringUtils.isBlank(configJson)) {
            log.info("Connector config is empty, connectorId: {}", connectorId);
            return new ArrayList<>();
        }

        // 3. 解析环境配置
        List<FlowConnectorEnvLiteVO> environments = connectorConfigParser.parseEnvironments(
                configJson, connector.getTypeCode());

        log.info("getEnvironments success, connectorId: {}, count: {}, typeCode: {}",
                connectorId, environments.size(), connector.getTypeCode());
        return environments;
    }

    @Override
    public EnvironmentConfigVO getEnvironmentConfig(Long connectorId, String envName) {
        log.info("getEnvironmentConfig start, connectorId: {}, envName: {}", connectorId, envName);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 获取 config 字段
        String config = connector.getConfig();
        if (StringUtils.isBlank(config)) {
            log.warn("Connector config is empty, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_CONFIG_NOT_EXISTS, envName);
        }

        // 3. 使用 Parser 提取环境 Schema
        JsonNode envSchema = connectorConfigParser.parseEnvironmentSchema(config, envName);

        // 4. 封装 VO
        EnvironmentConfigVO vo = new EnvironmentConfigVO();
        vo.setSchema(envSchema);
        vo.setEnvCode(envName);
        vo.setTypeCode(connector.getTypeCode());

        log.info("getEnvironmentConfig success, connectorId: {}, envName: {}", connectorId, envName);
        return vo;
    }

    @Override
    public EnvConfigTemplateVO getEnvConfigTemplate(Long connectorId) {
        log.info("getEnvConfigTemplate start, connectorId: {}", connectorId);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 获取连接器类型
        String typeCode = connector.getTypeCode();
        log.info("Connector typeCode: {}", typeCode);

        // 3. 查询节点配置模板（使用 findByNodeCode）
        FlowNodeConfigDO nodeConfig = flowNodeConfigRepository.findByNodeCode(typeCode);
        if (nodeConfig == null) {
            log.warn("Node config not found for typeCode: {}", typeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXISTS, typeCode);
        }

        // 4. 提取并解析 conn_config
        String connConfig = nodeConfig.getConnConfig();
        if (StringUtils.isBlank(connConfig)) {
            log.warn("conn_config is empty for typeCode: {}", typeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXISTS, typeCode);
        }

        JsonNode schema;
        try {
            schema = objectMapper.readTree(connConfig);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse conn_config for typeCode: {}", typeCode, e);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_INVALID, typeCode);
        }

        // 5. 封装返回
        EnvConfigTemplateVO vo = new EnvConfigTemplateVO();
        vo.setSchema(schema);
        log.info("getEnvConfigTemplate success, connectorId: {}, typeCode: {}", connectorId, typeCode);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveEnvironmentConfig(Long connectorId, SaveEnvironmentConfigReqVO reqVO) {
        log.info("saveEnvironmentConfig start, connectorId: {}", connectorId);

        // 1. 查询并验证连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 解析或创建根配置
        ObjectNode rootConfig = parseOrCreateRootConfig(connector.getConfig());
        ObjectNode properties = rootConfig.withObject("properties");

        // 3. 从请求中提取环境名称
        String envName = extractEnvNameFromConfig(reqVO.getConfig());

        // 4. 检查环境是否已存在
        if (properties.has(envName)) {
            log.warn("Environment already exists, connectorId: {}, envName: {}", connectorId, envName);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_ALREADY_EXISTS);
        }

        // 5. 添加新环境配置
        properties.set(envName, reqVO.getConfig());

        // 6. 更新元数据版本
        updateMetadataVersion(rootConfig);

        // 7. 保存到数据库
        connector.setConfig(toJsonString(rootConfig));
        connectorRepository.updateById(connector);

        log.info("saveEnvironmentConfig success, connectorId: {}, envName: {}", connectorId, envName);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEnvironmentConfig(Long connectorId, SaveEnvironmentConfigReqVO reqVO) {
        log.info("updateEnvironmentConfig start, connectorId: {}", connectorId);

        // 1. 查询并验证连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 解析根配置
        ObjectNode rootConfig = parseOrCreateRootConfig(connector.getConfig());
        ObjectNode properties = rootConfig.withObject("properties");

        // 3. 从请求中提取环境名称
        String envName = extractEnvNameFromConfig(reqVO.getConfig());

        // 4. 检查环境是否存在（编辑保存必须存在）
        if (!properties.has(envName)) {
            log.warn("Environment not exists, connectorId: {}, envName: {}", connectorId, envName);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_NOT_EXISTS, envName);
        }

        // 5. 替换已有环境配置
        properties.set(envName, reqVO.getConfig());

        // 6. 更新元数据版本
        updateMetadataVersion(rootConfig);

        // 7. 保存到数据库
        connector.setConfig(toJsonString(rootConfig));
        connectorRepository.updateById(connector);

        log.info("updateEnvironmentConfig success, connectorId: {}, envName: {}", connectorId, envName);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableEnvironment(Long connectorId, String envName) {
        log.info("enableEnvironment start, connectorId: {}, envName: {}", connectorId, envName);

        // 1. 查询并验证连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 解析根配置
        ObjectNode rootConfig = parseOrCreateRootConfig(connector.getConfig());
        ObjectNode properties = rootConfig.withObject("properties");

        // 3. 如果envName不为空，校验环境是否存在
        if (StringUtils.isNotBlank(envName)) {
            if (!properties.has(envName)) {
                log.warn("Environment not exists, connectorId: {}, envName: {}", connectorId, envName);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_NOT_EXISTS, envName);
            }
            // 设置启用环境
            rootConfig.put("enableEnvName", envName);
        } else {
            // 取消启用（设置为null）
            rootConfig.putNull("enableEnvName");
        }

        // 4. 更新元数据版本
        updateMetadataVersion(rootConfig);

        // 5. 保存到数据库
        connector.setConfig(toJsonString(rootConfig));
        connectorRepository.updateById(connector);

        log.info("enableEnvironment success, connectorId: {}, envName: {}", connectorId, envName);
        return Boolean.TRUE;
    }

    @Override
    public String getEnabledEnvName(Long connectorId) {
        log.info("getEnabledEnvName start, connectorId: {}", connectorId);

        // 1. 查询并验证连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 获取 config 字段
        String config = connector.getConfig();
        if (StringUtils.isBlank(config)) {
            log.info("Connector config is empty, return null, connectorId: {}", connectorId);
            return null;
        }

        // 3. 解析 JSON 并获取 enableEnvName
        JsonNode root = JsonUtils.parseTree(config);
        JsonNode enableEnvNameNode = root.get("enableEnvName");

        if (enableEnvNameNode == null || enableEnvNameNode.isNull()) {
            log.info("enableEnvName not found or is null, connectorId: {}", connectorId);
            return null;
        }

        String enableEnvName = enableEnvNameNode.asText();
        log.info("getEnabledEnvName success, connectorId: {}, enableEnvName: {}", connectorId, enableEnvName);
        return enableEnvName;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 解析或创建根配置对象
     *
     * @param configJson 现有配置JSON，可能为空
     * @return 根配置对象，包含 properties 和 _metadata 节点
     */
    private ObjectNode parseOrCreateRootConfig(String configJson) {
        ObjectNode rootConfig;
        if (StringUtils.isBlank(configJson)) {
            rootConfig = objectMapper.createObjectNode();
            rootConfig.putObject("properties");
            rootConfig.putObject("_metadata");
        } else {
            try {
                rootConfig = (ObjectNode) objectMapper.readTree(configJson);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse config JSON", e);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
            }
        }
        return rootConfig;
    }

    /**
     * 从配置节点中提取环境名称
     * <p>
     * 配置格式: {"envMode": "...", "envConfig": {"basicInfo": {"envName": "..."}}}
     *
     * @param configNode 配置节点
     * @return 环境名称
     */
    private String extractEnvNameFromConfig(JsonNode configNode) {
        if (configNode == null || !configNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        JsonNode envConfigNode = configNode.get("envConfig");
        if (envConfigNode == null || !envConfigNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        JsonNode basicInfoNode = envConfigNode.get("basicInfo");
        if (basicInfoNode == null || !basicInfoNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        JsonNode envNameNode = basicInfoNode.get("envName");
        if (envNameNode == null || envNameNode.isNull()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        return envNameNode.asText();
    }

    /**
     * 更新配置元数据版本号
     *
     * @param rootConfig 根配置对象
     */
    private void updateMetadataVersion(ObjectNode rootConfig) {
        ObjectNode metadata = rootConfig.withObject("_metadata");
        int currentVersion = metadata.has("version") ? metadata.get("version").asInt() : 0;
        metadata.put("version", currentVersion + 1);
        metadata.put("updatedAt", Instant.now().toString());
    }

    /**
     * 将 JsonNode 序列化为 JSON 字符串
     *
     * @param node JSON 节点
     * @return JSON 字符串
     */
    private String toJsonString(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize JSON", e);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }
    }
}