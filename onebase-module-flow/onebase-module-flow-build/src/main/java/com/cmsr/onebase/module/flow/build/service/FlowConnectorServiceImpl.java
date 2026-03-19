package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorActionRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorEnvRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorActionDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.ConnectorActionStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.cmsr.onebase.module.flow.core.util.ActionNameGenerator;
import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.cmsr.onebase.module.flow.component.external.service.HttpRequest;
import com.cmsr.onebase.module.flow.component.external.service.HttpServiceResponse;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.mybatisflex.core.paginate.Page;

@Slf4j
@Setter
@Service
public class FlowConnectorServiceImpl implements FlowConnectorService {

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Autowired
    private FlowConnectorEnvRepository connectorEnvRepository;

    @Autowired
    private FlowNodeConfigRepository flowNodeConfigRepository;

    @Autowired
    private FlowConnectorActionRepository actionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final ConnectorConfigHelper configHelper = new ConnectorConfigHelper(new ObjectMapper());
    private final ActionNameGenerator nameGenerator = new ActionNameGenerator();

    @Autowired
    private ActionConfigHelper actionConfigHelper;

    @Autowired
    private com.cmsr.onebase.module.flow.component.external.service.HttpExecuteService httpExecuteService;

    @Override
    public PageResult<FlowConnectorLiteVO> pageConnectors(PageConnectorReqVO pageReqVO) {
        // 自动填充 applicationId（如果未传递）
        if (pageReqVO.getApplicationId() == null) {
            pageReqVO.setApplicationId(ApplicationManager.getApplicationId());
        }

        PageResult<FlowConnectorDO> connectorPage = connectorRepository.selectConnectorPage(pageReqVO);

        List<FlowConnectorLiteVO> voList = new ArrayList<>();
        for (FlowConnectorDO connectorDO : connectorPage.getList()) {
            FlowConnectorLiteVO connectorVO = convertToLiteVO(connectorDO);
            voList.add(connectorVO);
        }
        return new PageResult<>(voList, connectorPage.getTotal());
    }

    private FlowConnectorVO convertToVO(FlowConnectorDO connectorDO) {
        FlowConnectorVO connectorVO = BeanUtils.toBean(connectorDO, FlowConnectorVO.class);
        String config = connectorDO.getConfig();
        if (StringUtils.isNotBlank(config)) {
            connectorVO.setConfig(JsonUtils.parseTree(config));
        }
        connectorVO.setConnectorVersion("1.0.0");
        return connectorVO;
    }

    @Override
    public FlowConnectorVO getConnectorDetail(Long connectorId) {
        FlowConnectorDO connectorDO = connectorRepository.getById(connectorId);
        if (connectorDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        return convertToVO(connectorDO);
    }

    @Override
    public CreateFlowConnectorRespVO createConnector(CreateFlowConnectorReqVO createVO) {
        FlowConnectorDO connectorDO = BeanUtils.toBean(createVO, FlowConnectorDO.class);
        connectorDO.setConnectorUuid(UuidUtils.getUuid());
        connectorDO.setConfig(jsonNodeToString(createVO.getConfig()));
        connectorRepository.save(connectorDO);
        return CreateFlowConnectorRespVO.builder()
                .id(connectorDO.getId())
                .connectorUuid(connectorDO.getConnectorUuid())
                .build();
    }

    @Override
    public void updateConnector(UpdateFlowConnectorReqVO updateVO) {
        // 手动校验必要字段（兼容性处理）
        if (updateVO.getId() == null) {
            throw new IllegalArgumentException("连接器ID不能为空");
        }
        if (StringUtils.isBlank(updateVO.getConnectorName())) {
            throw new IllegalArgumentException("连接器名称不能为空");
        }

        Long connectorId = updateVO.getId();
        FlowConnectorDO oldDO = connectorRepository.getById(connectorId);
        if (oldDO == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        oldDO.setConnectorName(updateVO.getConnectorName());
        oldDO.setDescription(updateVO.getDescription());
        oldDO.setConfig(JsonUtils.toJsonString(updateVO.getConfig()));
        connectorRepository.updateById(oldDO);
    }

    @Override
    public Boolean updateBaseInfo(Long connectorId, UpdateFlowConnectorReqVO updateVO) {
        log.info("updateBaseInfo start, connectorId: {}, description: {}",
                connectorId, updateVO.getDescription());

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 规范化描述（空字符串转 null）
        String newDesc = StringUtils.isBlank(updateVO.getDescription())
                ? null
                : updateVO.getDescription();
        String oldDesc = connector.getDescription();

        // 3. 比较是否发生变化
        if (Objects.equals(newDesc, oldDesc)) {
            log.info("描述未变化，跳过更新，connectorId: {}", connectorId);
            return false; // 没有变化
        }

        // 4. 执行更新
        connector.setDescription(newDesc);
        connectorRepository.updateById(connector);
        log.info("描述已更新，connectorId: {}", connectorId);
        return true; // 实际更新了
    }

    @Override
    public void deleteById(Long connectorId) {
        connectorRepository.removeById(connectorId);
    }

    @Override
    public List<FlowConnectorVO> listByType(String typeCode) {
        List<FlowConnectorDO> dos = connectorRepository.listByType(typeCode);
        return dos.stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    public PageResult<FlowConnectorLiteVO> listAll(PageParam pageParam) {
        log.info("listAll start, pageNo: {}, pageSize: {}", pageParam.getPageNo(), pageParam.getPageSize());
        Page<FlowConnectorDO> page = connectorRepository.page(
                new Page<>(pageParam.getPageNo(), pageParam.getPageSize()));
        List<FlowConnectorLiteVO> records = page.getRecords().stream()
                .map(this::convertToLiteVO)
                .toList();
        PageResult<FlowConnectorLiteVO> result = new PageResult<>(records, page.getTotalRow());
        log.info("listAll success, total: {}", result.getTotal());
        return result;
    }

    /**
     * Convert FlowConnectorDO to FlowConnectorLiteVO
     * 填充配置状态、环境信息等列表页面需要的字段
     */
    private FlowConnectorLiteVO convertToLiteVO(FlowConnectorDO connectorDO) {
        FlowConnectorLiteVO vo = BeanUtils.toBean(connectorDO, FlowConnectorLiteVO.class);

        // 配置状态：基于config字段是否有值
        String configStatus = StringUtils.isNotBlank(connectorDO.getConfig()) ? "configured" : "unconfigured";
        vo.setConfigStatus(configStatus);
        vo.setStatus(configStatus); // 前端使用的字段

        // 环境信息暂时为空，后续可能需要从其他地方获取
        vo.setEnvironment(null);

        return vo;
    }

    public String jsonNodeToString(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }
        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        }
        return JsonUtils.toJsonString(jsonNode);
    }

    @Override
    public List<String> getActionsById(Long id) {
        log.info("getActionsById start, id: {}", id);
        FlowConnectorDO connector = connectorRepository.getById(id);
        if (connector == null) {
            log.warn("Connector not found, id: {}", id);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 校验是否已配置启用环境
        validateEnableEnvConfigured(connector);

        return parseActionsFromConnector(connector, id.toString());
    }

    /**
     * 校验是否已配置启用环境
     *
     * @param connector 连接器数据对象
     */
    private void validateEnableEnvConfigured(FlowConnectorDO connector) {
        String config = connector.getConfig();
        if (StringUtils.isBlank(config)) {
            log.warn("Config is blank, enableEnvName not configured");
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENABLE_ENV_NOT_CONFIGURED);
        }

        JsonNode root = JsonUtils.parseTree(config);
        JsonNode enableEnvName = root.get("enableEnvName");

        if (enableEnvName == null || enableEnvName.isNull() || StringUtils.isBlank(enableEnvName.asText())) {
            log.warn("enableEnvName is null or blank, connector: {}", connector.getId());
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENABLE_ENV_NOT_CONFIGURED);
        }
    }

    /**
     * 从连接器对象解析动作列表（私有辅助方法，避免重复查询）
     *
     * @param connector           连接器数据对象
     * @param connectorIdentifier 连接器标识（用于日志，可能是id或uuid）
     * @return 动作名称列表
     */
    private List<String> parseActionsFromConnector(FlowConnectorDO connector, String connectorIdentifier) {
        // 1. Get actionConfig
        String actionConfig = connector.getActionConfig();
        if (StringUtils.isBlank(actionConfig)) {
            log.info("ActionConfig is blank, return empty list, connector: {}", connectorIdentifier);
            return Collections.emptyList();
        }

        // 2. Parse JSON and extract properties keys
        JsonNode root = JsonUtils.parseTree(actionConfig);
        JsonNode properties = root.get("properties");

        if (properties == null || !properties.isObject()) {
            log.error("Invalid connector action_config, properties not found or not an object, connector: {}",
                    connectorIdentifier);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }

        // 3. Extract keys while preserving order
        List<String> actions = new ArrayList<>();
        Iterator<String> fieldNames = properties.fieldNames();
        while (fieldNames.hasNext()) {
            actions.add(fieldNames.next());
        }

        log.info("parseActionsFromConnector success, connector: {}, actions: {}", connectorIdentifier, actions);
        return actions;
    }

    /**
     * 从连接器对象解析指定动作的配置值（私有辅助方法，避免重复查询）
     *
     * @param connector           连接器数据对象
     * @param actionName          动作名称
     * @param connectorIdentifier 连接器标识（用于日志，可能是id或uuid）
     * @return 动作配置值
     */
    private JsonNode parseActionValueFromConnector(FlowConnectorDO connector, String actionName,
            String connectorIdentifier) {
        // 1. Get config
        String config = connector.getConfig();
        if (StringUtils.isBlank(config)) {
            log.error("Config is blank, connector: {}", connectorIdentifier);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }

        // 2. Parse JSON and extract properties
        JsonNode root = JsonUtils.parseTree(config);
        JsonNode properties = root.get("properties");

        if (properties == null || !properties.isObject()) {
            log.error("Invalid connector config, properties not found or not an object, connector: {}",
                    connectorIdentifier);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }

        // 3. Get action value by actionName
        JsonNode actionValue = properties.get(actionName);
        if (actionValue == null || actionValue.isNull()) {
            log.warn("Action not found, connector: {}, actionName: {}", connectorIdentifier, actionName);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        log.info("parseActionValueFromConnector success, connector: {}, actionName: {}", connectorIdentifier,
                actionName);
        return actionValue;
    }

    @Override
    public void updateActiveStatus(Long id, Integer activeStatus) {
        log.info("updateActiveStatus start, id: {}, activeStatus: {}", id, activeStatus);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(id);
        if (connector == null) {
            log.warn("Connector not found, id: {}", id);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 更新状态
        connector.setActiveStatus(activeStatus);
        connectorRepository.updateById(connector);

        log.info("updateActiveStatus success, id: {}, activeStatus: {}", id, activeStatus);
    }

    @Autowired
    private com.cmsr.onebase.module.flow.build.util.ConnectorConfigParser connectorConfigParser;

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
    public ActionConfigTemplateVO getActionConfigTemplate(Long connectorId) {
        log.info("getActionConfigTemplate start, connectorId: {}", connectorId);

        // 1. 查询连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 通过 typeCode (= nodeCode) 查询节点配置
        String nodeCode = connector.getTypeCode();
        FlowNodeConfigDO nodeConfig = flowNodeConfigRepository.findByNodeCode(nodeCode);

        // 3. 校验动作配置模板存在
        if (nodeConfig == null) {
            log.warn("Node config not found for typeCode: {}", nodeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.NODE_CONFIG_NOT_EXISTS, nodeCode);
        }

        // 4. 提取并解析 action_config
        String actionConfigStr = nodeConfig.getActionConfig();
        if (StringUtils.isBlank(actionConfigStr)) {
            log.warn("action_config is empty for typeCode: {}", nodeCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_CONFIG_EMPTY);
        }

        JsonNode actionConfigSchema;
        try {
            actionConfigSchema = objectMapper.readTree(actionConfigStr);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse action_config for typeCode: {}, error: {}", nodeCode, e.getMessage());
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
        }

        // 5. 构造返回结果
        ActionConfigTemplateVO vo = new ActionConfigTemplateVO();
        vo.setSchema(actionConfigSchema);
        log.info("getActionConfigTemplate success, connectorId: {}, typeCode: {}", connectorId, nodeCode);
        return vo;
    }

    // ==================== 动作管理方法实现（已废弃，使用 FlowConnectorActionService） ====================

    @Override
    @Deprecated
    public List<ConnectorActionVO> getActionList(Long connectorId) {
        log.info("getActionList start, connectorId: {}", connectorId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从新的动作表查询
        List<FlowConnectorActionDO> actions = actionRepository.findByConnectorUuid(connector.getConnectorUuid());

        // 3. 转换为 VO
        List<ConnectorActionVO> result = new ArrayList<>();
        for (FlowConnectorActionDO action : actions) {
            ConnectorActionVO vo = ConnectorActionVO.builder()
                    .actionName(action.getActionName())
                    .description(action.getDescription())
                    .status(action.getActiveStatus() == 1 ? "published" : "offline")
                    .createTime(action.getCreateTime() != null ? action.getCreateTime().toString() : null)
                    .updateTime(action.getUpdateTime() != null ? action.getUpdateTime().toString() : null)
                    .build();
            result.add(vo);
        }

        log.info("getActionList success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    @Deprecated
    public List<ConnectorActionLiteVO> getActionInfos(Long connectorId) {
        log.info("getActionInfos start, connectorId: {}", connectorId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从新的动作表查询
        List<FlowConnectorActionDO> actions = actionRepository.findByConnectorUuid(connector.getConnectorUuid());

        // 3. 转换为 VO
        List<ConnectorActionLiteVO> result = new ArrayList<>();
        for (FlowConnectorActionDO action : actions) {
            ConnectorActionLiteVO vo = ConnectorActionLiteVO.builder()
                    .actionName(action.getActionName())
                    .description(action.getDescription())
                    .status(action.getActiveStatus() == 1 ? "published" : "offline")
                    .createTime(action.getCreateTime() != null ? action.getCreateTime().toString() : null)
                    .updateTime(action.getUpdateTime() != null ? action.getUpdateTime().toString() : null)
                    .build();
            result.add(vo);
        }

        log.info("getActionInfos success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    @Deprecated
    public ConnectorActionVO getActionDetail(Long connectorId, String actionName) {
        log.info("getActionDetail start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从新表按 actionName/actionCode 查找
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 构建返回 VO
        ConnectorActionVO vo = ConnectorActionVO.builder()
                .actionName(action.getActionName())
                .description(action.getDescription())
                .status(action.getActiveStatus() == 1 ? "published" : "offline")
                .createTime(action.getCreateTime() != null ? action.getCreateTime().toString() : null)
                .updateTime(action.getUpdateTime() != null ? action.getUpdateTime().toString() : null)
                .build();

        // 尝试解析 actionConfig 为 JsonNode
        if (StringUtils.isNotBlank(action.getActionConfig())) {
            try {
                vo.setActionConfig(objectMapper.readTree(action.getActionConfig()));
            } catch (Exception e) {
                log.warn("Failed to parse actionConfig as JsonNode", e);
            }
        }

        log.info("getActionDetail success, connectorId: {}, actionName: {}", connectorId, actionName);
        return vo;
    }

    /**
     * 从动作配置中提取描述信息
     * <p>
     * 支持 OpenAPI 格式和旧格式：
     * - OpenAPI 格式: 优先从 x-onebase.actionDescription 获取，其次从 description 获取
     * - 旧格式: 从 basic.description 获取
     *
     * @param actionNode 动作配置节点
     * @return 描述信息
     */
    private String getDescriptionFromActionConfig(JsonNode actionNode) {
        // 1. 优先从 x-onebase.actionDescription 获取（OpenAPI 格式）
        JsonNode xOnebase = actionNode.get("x-onebase");
        if (xOnebase != null && xOnebase.has("actionDescription")) {
            String desc = xOnebase.get("actionDescription").asText();
            if (StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }

        // 2. 从 description 获取（OpenAPI 格式）
        if (actionNode.has("description")) {
            String desc = actionNode.get("description").asText();
            if (StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }

        // 3. 从 basic.description 获取（旧格式，兼容）
        JsonNode basicNode = actionNode.get("basic");
        if (basicNode != null && basicNode.has("description")) {
            String desc = basicNode.get("description").asText();
            if (StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }

        return null;
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public String saveActionDraft(Long connectorId, CreateConnectorActionReqVO createVO) {
        log.info("saveActionDraft start, connectorId: {}, actionCode: {}", connectorId, createVO.getActionCode());

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 校验 actionCode 唯一性
        String actionCode = createVO.getActionCode();
        FlowConnectorActionDO existingAction = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionCode);
        if (existingAction != null) {
            throw new RuntimeException("动作编码已存在: " + actionCode);
        }

        // 3. 构建动作 DO
        FlowConnectorActionDO actionDO = new FlowConnectorActionDO();
        actionDO.setConnectorUuid(connector.getConnectorUuid());
        actionDO.setConnectorType(connector.getTypeCode());
        actionDO.setActionCode(actionCode);
        actionDO.setActionName(createVO.getActionName());
        actionDO.setDescription(createVO.getDescription());
        actionDO.setActiveStatus(0); // 草稿状态，默认禁用
        actionDO.setSortOrder(0);

        // 转换输入输出配置
        if (createVO.getInputConfig() != null) {
            actionDO.setInputSchema(createVO.getInputConfig().toString());
        }
        if (createVO.getOutputConfig() != null) {
            actionDO.setOutputSchema(createVO.getOutputConfig().toString());
        }

        // 合并基础信息和调试配置到 actionConfig
        try {
            ObjectNode configNode = objectMapper.createObjectNode();
            if (createVO.getBasicInfo() != null) {
                configNode.set("basicInfo", createVO.getBasicInfo());
            }
            if (createVO.getDebugConfig() != null) {
                configNode.set("debugConfig", createVO.getDebugConfig());
            }
            actionDO.setActionConfig(objectMapper.writeValueAsString(configNode));
        } catch (Exception e) {
            log.warn("Failed to serialize action config", e);
        }

        // 4. 保存到新表
        actionRepository.save(actionDO);

        log.info("saveActionDraft success, connectorId: {}, actionCode: {}", connectorId, actionCode);
        return actionCode;
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void updateActionDraft(Long connectorId, String actionName, UpdateConnectorActionReqVO updateVO) {
        log.info("updateActionDraft start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 更新动作配置
        if (updateVO.getActionName() != null) {
            action.setActionName(updateVO.getActionName());
        }
        if (updateVO.getDescription() != null) {
            action.setDescription(updateVO.getDescription());
        }
        if (updateVO.getInputConfig() != null) {
            action.setInputSchema(updateVO.getInputConfig().toString());
        }
        if (updateVO.getOutputConfig() != null) {
            action.setOutputSchema(updateVO.getOutputConfig().toString());
        }

        // 更新 actionConfig
        try {
            ObjectNode configNode = objectMapper.createObjectNode();
            if (updateVO.getBasicInfo() != null) {
                configNode.set("basicInfo", updateVO.getBasicInfo());
            }
            if (updateVO.getDebugConfig() != null) {
                configNode.set("debugConfig", updateVO.getDebugConfig());
            }
            action.setActionConfig(objectMapper.writeValueAsString(configNode));
        } catch (Exception e) {
            log.warn("Failed to serialize action config", e);
        }

        // 4. 保存
        actionRepository.updateById(action);

        log.info("updateActionDraft success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void publishAction(Long connectorId, String actionName) {
        log.info("publishAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 更新状态为启用
        action.setActiveStatus(1);
        actionRepository.updateById(action);

        log.info("publishAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void offlineAction(Long connectorId, String actionName) {
        log.info("offlineAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 更新状态为禁用
        action.setActiveStatus(0);
        actionRepository.updateById(action);

        log.info("offlineAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void republishAction(Long connectorId, String actionName) {
        log.info("republishAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 更新状态为启用
        action.setActiveStatus(1);
        actionRepository.updateById(action);

        log.info("republishAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public String copyAction(Long connectorId, String actionName) {
        log.info("copyAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找原动作
        FlowConnectorActionDO originalAction = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (originalAction == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 获取现有动作列表用于命名去重
        List<FlowConnectorActionDO> existingActions = actionRepository.findByConnectorUuid(connector.getConnectorUuid());
        List<String> existingNames = existingActions.stream()
                .map(FlowConnectorActionDO::getActionName)
                .collect(Collectors.toList());

        // 4. 生成唯一名称
        String newName = nameGenerator.generateCopyName(originalAction.getActionName(), existingNames);

        // 5. 复制动作配置
        FlowConnectorActionDO newAction = new FlowConnectorActionDO();
        newAction.setConnectorUuid(originalAction.getConnectorUuid());
        newAction.setConnectorType(originalAction.getConnectorType());
        newAction.setActionName(newName);
        newAction.setActionCode(originalAction.getActionCode() + "_copy");
        newAction.setDescription(originalAction.getDescription());
        newAction.setInputSchema(originalAction.getInputSchema());
        newAction.setOutputSchema(originalAction.getOutputSchema());
        newAction.setActionConfig(originalAction.getActionConfig());
        newAction.setActiveStatus(0); // 复制的动作默认禁用
        newAction.setSortOrder(0);

        // 6. 保存到新表
        actionRepository.save(newAction);

        log.info("copyAction success, connectorId: {}, originalName: {}, newName: {}",
                connectorId, originalAction.getActionName(), newName);

        return newName;
    }

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void deleteAction(Long connectorId, String actionName) {
        log.info("deleteAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 软删除
        action.setActiveStatus(0);
        actionRepository.updateById(action);

        log.info("deleteAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Deprecated
    public ActionConfigHelper.ValidationResult validateActionForPublish(Long connectorId, String actionName) {
        log.info("validateActionForPublish start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        FlowConnectorActionDO action = actionRepository.findByConnectorUuidAndCode(
                connector.getConnectorUuid(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 简单校验 - 检查必要字段是否存在
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(action.getActionName())) {
            errors.add("动作名称不能为空");
        }
        if (StringUtils.isBlank(action.getActionCode())) {
            errors.add("动作编码不能为空");
        }

        ActionConfigHelper.ValidationResult result = new ActionConfigHelper.ValidationResult(errors.isEmpty(), errors);

        log.info("validateActionForPublish success, connectorId: {}, actionName: {}, valid: {}",
                connectorId, actionName, result.isValid());
        return result;
    }

    // ==================== 辅助方法 ====================

    /**
     * 验证动作状态是否可以发布
     * <p>
     * 只有已下架(offline)状态的动作才能发布
     *
     * @param action 动作配置节点
     * @throws RuntimeException 当状态不符合时抛出异常
     */
    private void validateActionStatusForPublish(JsonNode action) {
        String currentStatus = getString(action, "status");
        if (!ConnectorActionStatusEnum.isOffline(currentStatus)) {
            throw new RuntimeException("只有下架状态的动作才能发布");
        }
    }

    /**
     * 验证动作状态是否可以下架
     * <p>
     * 只有已发布(published)状态的动作才能下架
     *
     * @param action 动作配置节点
     * @throws RuntimeException 当状态不符合时抛出异常
     */
    private void validateActionStatusForOffline(JsonNode action) {
        String status = getString(action, "status");
        if (!ConnectorActionStatusEnum.isPublished(status)) {
            throw new RuntimeException("只有已发布的动作才能下架");
        }
    }

    /**
     * 从 JsonNode 安全获取字符串值
     */
    private String getString(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }

    /**
     * 从 JsonNode 安全获取整数值
     */
    private Integer getInt(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asInt() : null;
    }

    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (StringUtils.isBlank(dateTimeStr)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveActionConfig(Long connectorId, SaveActionConfigReqVO reqVO) {
        return saveOrUpdateActionConfigInternal(connectorId, reqVO.getActionConfig(), true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActionConfig(Long connectorId, String actionName, SaveActionConfigReqVO reqVO) {
        return saveOrUpdateActionConfigInternal(connectorId, reqVO.getActionConfig(), false);
    }

    /**
     * 保存或更新动作配置的内部通用方法
     *
     * @param connectorId 连接器ID
     * @param actionConfig 动作配置
     * @param isNew 是否为新建操作（true=新建需检查不存在，false=更新需检查存在）
     * @return 保存结果
     */
    private Boolean saveOrUpdateActionConfigInternal(Long connectorId, JsonNode actionConfig, boolean isNew) {
        String operation = isNew ? "saveActionConfig" : "updateActionConfig";
        log.info("{} start, connectorId: {}", operation, connectorId);

        // 1. 查询并验证连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 解析或创建 action_config 根配置
        ObjectNode rootActionConfig = parseOrCreateActionRootConfig(connector.getActionConfig());
        ObjectNode properties = rootActionConfig.withObject("properties");

        // 3. 从配置中提取动作名称
        String actionName = extractActionCodeFromConfig(actionConfig);

        // 4. 校验动作是否存在
        if (isNew) {
            // 新建：检查是否已存在
            if (properties.has(actionName)) {
                log.warn("Action already exists, connectorId: {}, actionName: {}", connectorId, actionName);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_ALREADY_EXISTS, actionName);
            }
        } else {
            // 更新：检查是否不存在
            if (!properties.has(actionName)) {
                log.warn("Action not found for update, connectorId: {}, actionName: {}", connectorId, actionName);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
            }
        }

        // 5. 添加时间戳字段
        String currentTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ObjectNode actionConfigWithTimestamp;
        if (actionConfig.isObject()) {
            actionConfigWithTimestamp = (ObjectNode) actionConfig;
        } else {
            actionConfigWithTimestamp = objectMapper.createObjectNode();
            actionConfigWithTimestamp.setAll((ObjectNode) actionConfig);
        }

        if (isNew) {
            // 新建：设置createTime和updateTime
            actionConfigWithTimestamp.put("createTime", currentTime);
            actionConfigWithTimestamp.put("updateTime", currentTime);
        } else {
            // 更新：只更新updateTime，保留原createTime
            JsonNode existingAction = properties.get(actionName);
            String existingCreateTime = existingAction != null && existingAction.has("createTime")
                    ? existingAction.get("createTime").asText()
                    : currentTime;
            actionConfigWithTimestamp.put("createTime", existingCreateTime);
            actionConfigWithTimestamp.put("updateTime", currentTime);
        }

        // 6. 保存动作配置
        properties.set(actionName, actionConfigWithTimestamp);

        // 7. 更新元数据版本
        updateActionMetadataVersion(rootActionConfig);

        // 8. 保存到数据库
        connector.setActionConfig(toJsonString(rootActionConfig));
        connectorRepository.updateById(connector);

        log.info("{} success, connectorId: {}, actionName: {}", operation, connectorId, actionName);
        return Boolean.TRUE;
    }

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
     * 从动作配置中提取动作编码
     * <p>
     * 支持 OpenAPI 格式和旧格式：
     * - OpenAPI 格式: {"summary": "动作名称", "x-onebase": {"actionName": "xxx"}, ...}
     * - 旧格式: {"basic": {"actionName": "xxx"}, ...}
     * <p>
     * 优先级：x-onebase.actionName > summary > basic.actionName
     *
     * @param configNode 动作配置节点
     * @return 动作编码（actionName）
     */
    private String extractActionCodeFromConfig(JsonNode configNode) {
        if (configNode == null || !configNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
        }

        // 1. 优先从 x-onebase.actionName 提取（OpenAPI 格式）
        JsonNode xOnebase = configNode.get("x-onebase");
        if (xOnebase != null && xOnebase.has("actionName")) {
            String actionName = xOnebase.get("actionName").asText();
            if (StringUtils.isNotBlank(actionName)) {
                return actionName;
            }
        }

        // 2. 从 summary 提取（OpenAPI 格式）
        if (configNode.has("summary")) {
            String summary = configNode.get("summary").asText();
            if (StringUtils.isNotBlank(summary)) {
                return summary;
            }
        }

        // 3. 从 basic.actionName 提取（旧格式，兼容）
        JsonNode basicNode = configNode.get("basic");
        if (basicNode != null && basicNode.has("actionName")) {
            String actionName = basicNode.get("actionName").asText();
            if (StringUtils.isNotBlank(actionName)) {
                return actionName;
            }
        }

        throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
    }

    /**
     * 解析或创建 action_config 根配置对象
     *
     * @param actionConfigJson 现有动作配置JSON，可能为空
     * @return 根配置对象，包含 properties 和 _metadata 节点
     */
    private ObjectNode parseOrCreateActionRootConfig(String actionConfigJson) {
        ObjectNode rootConfig;
        if (StringUtils.isBlank(actionConfigJson)) {
            // 首次创建：构建标准的 Formily Schema 格式
            rootConfig = objectMapper.createObjectNode();
            rootConfig.put("type", "object");
            rootConfig.put("title", "连接器动作配置");
            rootConfig.putObject("properties");
            rootConfig.putObject("_metadata");
        } else {
            try {
                rootConfig = (ObjectNode) objectMapper.readTree(actionConfigJson);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse action_config JSON", e);
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_CONNECTOR_CONFIG);
            }
        }
        return rootConfig;
    }

    /**
     * 更新动作配置元数据版本号
     *
     * @param rootConfig 根配置对象
     */
    private void updateActionMetadataVersion(ObjectNode rootConfig) {
        ObjectNode metadata = rootConfig.withObject("_metadata");

        long currentVersion = metadata.path("version").asLong(0);
        metadata.put("version", currentVersion + 1);
        metadata.put("lastModified", LocalDateTime.now().toString());
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

    @Override
    public ExecuteHttpActionRespVO debugHttpAction(DebugHttpActionReqVO reqVO) {
        DebugHttpActionReqVO.DebugConfig debug = reqVO.getDebug();
        log.info("调试HTTP动作开始，URL: {}, Method: {}", debug.getUrl(), debug.getMethod());

        // 1. 构建debug配置节点（从ReqVO转换）
        ObjectNode debugConfig = objectMapper.createObjectNode();
        debugConfig.put("url", debug.getUrl());
        debugConfig.put("method", debug.getMethod());

        // 2. 转换requestHeaders（只保留key和fieldValue）
        if (debug.getRequestHeaders() != null && !debug.getRequestHeaders().isEmpty()) {
            ArrayNode headersArray = debugConfig.putArray("requestHeaders");
            for (HttpParamFieldVO field : debug.getRequestHeaders()) {
                ObjectNode headerNode = headersArray.addObject();
                headerNode.put("key", field.getKey());
                headerNode.put("fieldValue", field.getFieldValue());
            }
        }

        // 3. 转换queryParams（只保留key和fieldValue）
        if (debug.getQueryParams() != null && !debug.getQueryParams().isEmpty()) {
            ArrayNode queryParamsArray = debugConfig.putArray("queryParams");
            for (HttpParamFieldVO field : debug.getQueryParams()) {
                ObjectNode queryNode = queryParamsArray.addObject();
                queryNode.put("key", field.getKey());
                queryNode.put("fieldValue", field.getFieldValue());
            }
        }

        // 4. 转换pathParams（只保留key和fieldValue）
        if (debug.getPathParams() != null && !debug.getPathParams().isEmpty()) {
            ArrayNode pathParamsArray = debugConfig.putArray("pathParams");
            for (HttpParamFieldVO field : debug.getPathParams()) {
                ObjectNode pathNode = pathParamsArray.addObject();
                pathNode.put("key", field.getKey());
                pathNode.put("fieldValue", field.getFieldValue());
            }
        }

        // 5. 转换requestBody（只保留key和fieldValue）
        if (debug.getRequestBody() != null && !debug.getRequestBody().isEmpty()) {
            ArrayNode bodyArray = debugConfig.putArray("requestBody");
            for (HttpParamFieldVO field : debug.getRequestBody()) {
                ObjectNode bodyNode = bodyArray.addObject();
                bodyNode.put("key", field.getKey());
                bodyNode.put("fieldValue", field.getFieldValue());
            }
        }

        // 6. 构建HTTP请求（从debug配置获取所有参数）
        HttpRequest request = buildHttpRequest(debugConfig);

        // 7. 执行HTTP请求
        long startTime = System.currentTimeMillis();
        try {
            HttpServiceResponse response = httpExecuteService.execute(request);
            long duration = System.currentTimeMillis() - startTime;

            log.info("调试HTTP动作成功，耗时: {}ms", duration);
            return buildSuccessResponse(response, request, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("调试HTTP动作失败", e);
            return buildErrorResponse(e, request, duration);
        }
    }

    /**
     * 构建HTTP请求 - 从debug配置获取所有参数
     */
    private HttpRequest buildHttpRequest(JsonNode debugConfig) {
        HttpRequest request = new HttpRequest();

        // 1. 设置URL和方法（从debug，必填）
        String url = getString(debugConfig, "url");
        String method = getString(debugConfig, "method");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("调试配置的URL不能为空");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("调试配置的请求方法不能为空");
        }
        request.setUrl(url);
        request.setMethod(method);

        // 2. 处理路径参数（从debug配置获取）
        JsonNode debugPathParams = debugConfig.get("pathParams");
        if (debugPathParams != null && debugPathParams.isArray() && debugPathParams.size() > 0) {
            Map<String, Object> pathParams = new LinkedHashMap<>();
            for (JsonNode paramNode : debugPathParams) {
                String key = getString(paramNode, "key");
                String value = getString(paramNode, "fieldValue");
                if (key != null && value != null) {
                    pathParams.put(key, value);
                }
            }
            if (!pathParams.isEmpty()) {
                String resolvedUrl = replacePathVariables(url, pathParams);
                request.setUrl(resolvedUrl);
            }
        }

        // 3. 构建请求头（从debug配置获取）
        List<HttpNodeData.Header> headers = buildHeaders(debugConfig);
        request.setHeaders(headers);

        // 4. 构建查询参数（从debug配置获取）
        String urlWithQuery = buildUrlWithQuery(debugConfig, request.getUrl());
        request.setUrl(urlWithQuery);

        // 5. 构建请求体（从debug配置获取）
        String body = buildRequestBody(debugConfig);
        if (body != null) {
            request.setBodyContent(body);
        }

        // 6. 设置超时和重试（测试模式）
        request.setTimeout(5000);
        request.setRetry(0);

        return request;
    }

    /**
     * 构建请求头 - 从debug配置获取
     */
    private List<HttpNodeData.Header> buildHeaders(JsonNode debugConfig) {
        List<HttpNodeData.Header> headers = new ArrayList<>();

        JsonNode debugHeaders = debugConfig.get("requestHeaders");
        if (debugHeaders != null && debugHeaders.isArray()) {
            for (JsonNode headerNode : debugHeaders) {
                String key = getString(headerNode, "key");
                String value = getString(headerNode, "fieldValue");
                if (key != null && value != null) {
                    HttpNodeData.Header header = new HttpNodeData.Header();
                    header.setKey(key);
                    header.setValue(value);
                    headers.add(header);
                }
            }
        }

        return headers;
    }

    /**
     * 路径变量替换
     */
    private String replacePathVariables(String url, Map<String, Object> pathParams) {
        if (url == null || pathParams == null || pathParams.isEmpty()) {
            return url;
        }

        String result = url;
        for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = String.valueOf(entry.getValue());
            result = result.replace(placeholder, value);
        }
        return result;
    }

    /**
     * 构建带查询参数的URL（从debug配置获取）
     */
    private String buildUrlWithQuery(JsonNode debugConfig, String url) {
        if (url == null) {
            return url;
        }

        Map<String, String> queryParams = new LinkedHashMap<>();

        JsonNode debugQueryParams = debugConfig.get("queryParams");
        if (debugQueryParams != null && debugQueryParams.isArray()) {
            for (JsonNode paramNode : debugQueryParams) {
                String key = getString(paramNode, "key");
                String value = getString(paramNode, "fieldValue");
                if (key != null && value != null) {
                    queryParams.put(key, value);
                }
            }
        }

        if (queryParams.isEmpty()) {
            return url;
        }

        String query = queryParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return url + (url.contains("?") ? "&" : "?") + query;
    }

    /**
     * 构建请求体（从debug配置获取）
     */
    private String buildRequestBody(JsonNode debugConfig) {
        JsonNode debugBody = debugConfig.get("requestBody");
        if (debugBody == null || !debugBody.isArray() || debugBody.size() == 0) {
            return null;
        }

        // 构建JSON格式的请求体
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        for (JsonNode paramNode : debugBody) {
            String key = getString(paramNode, "key");
            String value = getString(paramNode, "fieldValue");
            if (key != null && value != null) {
                bodyMap.put(key, value);
            }
        }

        if (bodyMap.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(bodyMap);
        } catch (Exception e) {
            log.error("构建请求体失败", e);
            return null;
        }
    }

    /**
     * 构建成功响应
     */
    private ExecuteHttpActionRespVO buildSuccessResponse(HttpServiceResponse response, HttpRequest request, long duration) {
        return ExecuteHttpActionRespVO.builder()
                .status("success")
                .duration(duration)
                .statusCode(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody())
                .rawBody(response.getRawBody())
                .requestDetail(buildRequestDetail(request))
                .message("执行成功")
                .build();
    }

    /**
     * 构建错误响应
     */
    private ExecuteHttpActionRespVO buildErrorResponse(Exception e, HttpRequest request, long duration) {
        return ExecuteHttpActionRespVO.builder()
                .status("fail")
                .duration(duration)
                .errorMessage(e.getMessage())
                .requestDetail(buildRequestDetail(request))
                .message("执行失败: " + e.getMessage())
                .build();
    }

    /**
     * 构建请求详情
     */
    private ExecuteHttpActionRespVO.RequestDetail buildRequestDetail(HttpRequest request) {
        Map<String, String> headers = new HashMap<>();
        if (request.getHeaders() != null) {
            for (HttpNodeData.Header header : request.getHeaders()) {
                headers.put(header.getKey(), header.getValue());
            }
        }

        return ExecuteHttpActionRespVO.RequestDetail.builder()
                .url(request.getUrl())
                .method(request.getMethod())
                .headers(headers)
                .body(request.getBodyContent())
                .build();
    }
}
