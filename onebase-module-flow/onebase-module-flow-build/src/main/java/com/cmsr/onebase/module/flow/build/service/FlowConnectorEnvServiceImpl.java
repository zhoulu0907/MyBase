package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.flow.build.vo.EnvConfigTemplateVO;
import com.cmsr.onebase.module.flow.build.vo.EnvironmentConfigVO;
import com.cmsr.onebase.module.flow.build.vo.FlowConnectorEnvLiteVO;
import com.cmsr.onebase.module.flow.build.vo.SaveEnvironmentConfigReqVO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorEnvRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 连接器环境配置Service实现
 * <p>
 * 环境配置存储在独立的 flow_connector_env 表中，不再使用 flow_connector.config JSON
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
    private FlowConnectorEnvRepository envRepository;

    @Autowired
    private FlowNodeConfigRepository flowNodeConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<FlowConnectorEnvLiteVO> getEnvironments(Long connectorId) {
        log.info("getEnvironments start, connectorId: {}", connectorId);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}, returning empty list", connectorId);
            return List.of();
        }

        // 2. 通过 typeCode 查询环境配置列表
        String typeCode = connector.getTypeCode();
        List<FlowConnectorEnvDO> envList = envRepository.selectByTypeCode(typeCode);

        // 3. 转换为 VO
        List<FlowConnectorEnvLiteVO> result = envList.stream()
                .map(this::convertToLiteVO)
                .collect(Collectors.toList());

        log.info("getEnvironments success, connectorId: {}, count: {}, typeCode: {}",
                connectorId, result.size(), typeCode);
        return result;
    }

    @Override
    public EnvironmentConfigVO getEnvironmentConfig(Long connectorId, String envCode) {
        log.info("getEnvironmentConfig start, connectorId: {}, envCode: {}", connectorId, envCode);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 通过 typeCode 和 envCode 查询环境配置
        String typeCode = connector.getTypeCode();
        FlowConnectorEnvDO envDO = envRepository.selectByTypeCodeAndEnvCode(typeCode, envCode);
        if (envDO == null) {
            log.warn("Environment not found, connectorId: {}, envCode: {}", connectorId, envCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_CONFIG_NOT_EXISTS, envCode);
        }

        // 3. 封装 VO
        EnvironmentConfigVO vo = new EnvironmentConfigVO();
        vo.setEnvCode(envDO.getEnvCode());
        vo.setTypeCode(typeCode);

        // 解析 config 字段作为 schema
        if (StringUtils.isNotBlank(envDO.getConfig())) {
            try {
                JsonNode schema = objectMapper.readTree(envDO.getConfig());
                vo.setSchema(schema);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse config JSON, envId: {}", envDO.getId(), e);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
            }
        }

        log.info("getEnvironmentConfig success, connectorId: {}, envCode: {}", connectorId, envCode);
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

        // 3. 查询节点配置模板
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

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        String typeCode = connector.getTypeCode();

        // 2. 从请求中提取环境编码，如果没有则自动生成
        String envCode = extractOrGenerateEnvCode(reqVO.getConfig(), connector.getConnectorName());

        // 3. 检查环境编码是否已存在
        if (envRepository.existsByTypeAndEnvCode(typeCode, envCode, null)) {
            log.warn("Environment already exists, connectorId: {}, envCode: {}", connectorId, envCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_ALREADY_EXISTS);
        }

        // 4. 创建环境配置实体
        FlowConnectorEnvDO envDO = new FlowConnectorEnvDO();
        envDO.setEnvUuid(UUID.randomUUID().toString().replace("-", ""));
        envDO.setEnvCode(envCode);
        envDO.setEnvName(envCode + "环境配置");
        envDO.setTypeCode(typeCode);
        envDO.setConfig(toJsonString(reqVO.getConfig()));
        envDO.setActiveStatus(1);
        envDO.setSortOrder(0);

        // 5. 保存到数据库
        envRepository.save(envDO);

        log.info("saveEnvironmentConfig success, connectorId: {}, envId: {}, envCode: {}", connectorId, envDO.getId(), envCode);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEnvironmentConfig(Long connectorId, SaveEnvironmentConfigReqVO reqVO) {
        log.info("updateEnvironmentConfig start, connectorId: {}", connectorId);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        String typeCode = connector.getTypeCode();

        // 2. 从请求中提取环境编码
        String envCode = extractEnvCodeFromConfig(reqVO.getConfig());

        // 3. 查询现有环境配置
        FlowConnectorEnvDO envDO = envRepository.selectByTypeCodeAndEnvCode(typeCode, envCode);
        if (envDO == null) {
            log.warn("Environment not exists, connectorId: {}, envCode: {}", connectorId, envCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_NOT_EXISTS, envCode);
        }

        // 4. 更新配置
        envDO.setConfig(toJsonString(reqVO.getConfig()));
        envRepository.updateById(envDO);

        log.info("updateEnvironmentConfig success, connectorId: {}, envId: {}, envCode: {}", connectorId, envDO.getId(), envCode);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableEnvironment(Long connectorId, String envCode) {
        log.info("enableEnvironment start, connectorId: {}, envCode: {}", connectorId, envCode);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        String typeCode = connector.getTypeCode();

        // 2. 如果envCode不为空，校验环境是否存在
        if (StringUtils.isNotBlank(envCode)) {
            FlowConnectorEnvDO envDO = envRepository.selectByTypeCodeAndEnvCode(typeCode, envCode);
            if (envDO == null) {
                log.warn("Environment not exists, connectorId: {}, envCode: {}", connectorId, envCode);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_NOT_EXISTS, envCode);
            }
            // 更新 connector 的 envUuid
            connector.setEnvUuid(envDO.getEnvUuid());
        } else {
            // 取消启用
            connector.setEnvUuid(null);
        }

        // 3. 保存到数据库
        connectorRepository.updateById(connector);

        log.info("enableEnvironment success, connectorId: {}, envCode: {}", connectorId, envCode);
        return Boolean.TRUE;
    }

    @Override
    public FlowConnectorEnvLiteVO getEnabledEnv(Long connectorId) {
        log.info("getEnabledEnv start, connectorId: {}", connectorId);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 获取 envUuid
        String envUuid = connector.getEnvUuid();
        if (StringUtils.isBlank(envUuid)) {
            log.info("No enabled environment, connectorId: {}", connectorId);
            return null;
        }

        // 3. 查询环境配置
        FlowConnectorEnvDO envDO = envRepository.selectByEnvUuid(envUuid);
        if (envDO == null) {
            log.info("Enabled environment not found, envUuid: {}", envUuid);
            return null;
        }

        // 4. 转换为 VO 返回
        FlowConnectorEnvLiteVO vo = convertToLiteVO(envDO);
        log.info("getEnabledEnv success, connectorId: {}, envCode: {}", connectorId, vo.getEnvCode());
        return vo;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换为精简 VO
     */
    private FlowConnectorEnvLiteVO convertToLiteVO(FlowConnectorEnvDO envDO) {
        FlowConnectorEnvLiteVO vo = new FlowConnectorEnvLiteVO();
        vo.setId(envDO.getId());
        vo.setEnvUuid(envDO.getEnvUuid());
        vo.setEnvName(envDO.getEnvName());
        vo.setEnvCode(envDO.getEnvCode());
        vo.setTypeCode(envDO.getTypeCode());
        vo.setEnvUrl(envDO.getEnvUrl());
        vo.setAuthType(envDO.getAuthType());
        vo.setDescription(envDO.getDescription());
        vo.setActiveStatus(envDO.getActiveStatus());
        vo.setCreateTime(envDO.getCreateTime());
        return vo;
    }

    /**
     * 从配置节点中提取环境编码，如果不存在则使用默认值
     *
     * @param configNode 配置节点
     * @param defaultName 默认环境编码（实例名称）
     * @return 环境编码
     */
    private String extractOrGenerateEnvCode(JsonNode configNode, String defaultName) {
        if (configNode == null || !configNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        JsonNode envConfigNode = configNode.get("envConfig");
        if (envConfigNode == null || !envConfigNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        JsonNode basicInfoNode = envConfigNode.get("basicInfo");
        if (basicInfoNode == null || !basicInfoNode.isObject()) {
            // basicInfo 不存在时，自动创建并设置默认环境编码
            com.fasterxml.jackson.databind.node.ObjectNode envConfigObj = (com.fasterxml.jackson.databind.node.ObjectNode) envConfigNode;
            com.fasterxml.jackson.databind.node.ObjectNode newBasicInfo = envConfigObj.putObject("basicInfo");
            String generatedCode = StringUtils.isNotBlank(defaultName) ? defaultName : "DEFAULT";
            newBasicInfo.put("envCode", generatedCode);
            log.info("Auto generated envCode: {}", generatedCode);
            return generatedCode;
        }

        JsonNode envCodeNode = basicInfoNode.get("envCode");
        if (envCodeNode == null || envCodeNode.isNull() || StringUtils.isBlank(envCodeNode.asText())) {
            // envCode 为空时，自动设置默认值
            String generatedCode = StringUtils.isNotBlank(defaultName) ? defaultName : "DEFAULT";
            ((com.fasterxml.jackson.databind.node.ObjectNode) basicInfoNode).put("envCode", generatedCode);
            log.info("Auto generated envCode: {}", generatedCode);
            return generatedCode;
        }

        return envCodeNode.asText();
    }

    /**
     * 从配置节点中提取环境编码
     *
     * @param configNode 配置节点
     * @return 环境编码
     */
    private String extractEnvCodeFromConfig(JsonNode configNode) {
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

        JsonNode envCodeNode = basicInfoNode.get("envCode");
        if (envCodeNode == null || envCodeNode.isNull()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ENV_CONFIG);
        }

        return envCodeNode.asText();
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
