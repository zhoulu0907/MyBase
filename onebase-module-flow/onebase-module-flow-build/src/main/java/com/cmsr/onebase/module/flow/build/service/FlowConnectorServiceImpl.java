package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.flow.build.vo.*;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorEnvRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeConfigRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.cmsr.onebase.module.flow.core.util.ActionNameGenerator;
import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
    private ObjectMapper objectMapper;

    private final ConnectorConfigHelper configHelper = new ConnectorConfigHelper(new ObjectMapper());
    private final ActionNameGenerator nameGenerator = new ActionNameGenerator();

    @Autowired
    private ActionConfigHelper actionConfigHelper;

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
        return parseActionsFromConnector(connector, id.toString());
    }

    /**
     * 从连接器对象解析动作列表（私有辅助方法，避免重复查询）
     *
     * @param connector           连接器数据对象
     * @param connectorIdentifier 连接器标识（用于日志，可能是id或uuid）
     * @return 动作名称列表
     */
    private List<String> parseActionsFromConnector(FlowConnectorDO connector, String connectorIdentifier) {
        // 1. Get config
        String config = connector.getConfig();
        if (StringUtils.isBlank(config)) {
            log.info("Config is blank, return empty list, connector: {}", connectorIdentifier);
            return Collections.emptyList();
        }

        // 2. Parse JSON and extract properties keys
        JsonNode root = JsonUtils.parseTree(config);
        JsonNode properties = root.get("properties");

        if (properties == null || !properties.isObject()) {
            log.error("Invalid connector config, properties not found or not an object, connector: {}",
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
    public EnvironmentConfigVO getEnvironmentConfig(Long connectorId, String envCode) {
        log.info("getEnvironmentConfig start, connectorId: {}, envCode: {}", connectorId, envCode);

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
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ENV_CONFIG_NOT_EXISTS, envCode);
        }

        // 3. 使用 Parser 提取环境 Schema
        JsonNode envSchema = connectorConfigParser.parseEnvironmentSchema(config, envCode);

        // 4. 封装 VO
        EnvironmentConfigVO vo = new EnvironmentConfigVO();
        vo.setSchema(envSchema);
        vo.setEnvCode(envCode);
        vo.setTypeCode(connector.getTypeCode());

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

    // ==================== 动作管理方法实现 ====================

    @Override
    public List<ConnectorActionVO> getActionList(Long connectorId) {
        log.info("getActionList start, connectorId: {}", connectorId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 提取动作列表 - 从 action_config 字段读取
        List<JsonNode> actions = actionConfigHelper.getActions(connector.getActionConfig());

        // 3. 转换为 VO
        List<ConnectorActionVO> result = new ArrayList<>();
        for (JsonNode action : actions) {
            ConnectorActionVO vo = ConnectorActionVO.builder()
                    .actionName(getString(action, "actionName"))
                    .description(getString(action, "description"))
                    .status(getString(action, "status"))
                    .build();
            result.add(vo);
        }

        log.info("getActionList success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    public List<ConnectorActionLiteVO> getActionInfos(Long connectorId) {
        log.info("getActionInfos start, connectorId: {}", connectorId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从 action_config.properties 对象提取动作列表
        List<ConnectorActionLiteVO> result = new ArrayList<>();
        String actionConfig = connector.getActionConfig();
        if (actionConfig == null || actionConfig.trim().isEmpty()) {
            log.info("action_config is empty, return empty list");
            return result;
        }

        try {
            JsonNode root = objectMapper.readTree(actionConfig);
            JsonNode properties = root.get("properties");
            if (properties == null || !properties.isObject()) {
                return result;
            }

            // 遍历 properties，key 作为 actionName
            Iterator<String> actionNames = properties.fieldNames();
            while (actionNames.hasNext()) {
                String actionName = actionNames.next();
                JsonNode actionNode = properties.get(actionName);
                String status = getString(actionNode, "status");

                // 从 basic.description 获取描述
                JsonNode basicNode = actionNode.get("basic");
                String description = basicNode != null ? getString(basicNode, "description") : null;

                ConnectorActionLiteVO vo = ConnectorActionLiteVO.builder()
                        .actionName(actionName)
                        .description(description)
                        .status(status)
                        .build();
                result.add(vo);
            }
        } catch (Exception e) {
            log.error("Parse action_config failed", e);
        }

        log.info("getActionInfos success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    public ConnectorActionVO getActionDetail(Long connectorId, String actionName) {
        log.info("getActionDetail start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从 action_config.properties 按 actionCode（作为动作名称key）查找
        String actionConfig = connector.getActionConfig();
        if (actionConfig == null || actionConfig.trim().isEmpty()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        try {
            JsonNode root = objectMapper.readTree(actionConfig);
            JsonNode properties = root.get("properties");
            if (properties == null || !properties.has(actionName)) {
                throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
            }

            JsonNode actionNode = properties.get(actionName);

            // 3. 从 basic 对象获取描述信息
            JsonNode basicNode = actionNode.get("basic");
            String description = basicNode != null ? getString(basicNode, "description") : null;

            // 4. 构建返回 VO - 字段映射：basic→basicInfo, request→inputConfig, response→outputConfig, debug→debugConfig
            ConnectorActionVO vo = ConnectorActionVO.builder()
                    .actionName(actionName)
                    .description(description)
                    .status(getString(actionNode, "status"))
                    .basicInfo(actionNode.get("basic"))
                    .inputConfig(actionNode.get("request"))
                    .outputConfig(actionNode.get("response"))
                    .debugConfig(actionNode.get("debug"))
                    .build();

            log.info("getActionDetail success, connectorId: {}, actionName: {}", connectorId, actionName);
            return vo;
        } catch (Exception e) {
            log.error("Parse action_config failed", e);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }
    }

    @Override
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
        if (!actionConfigHelper.isActionCodeUnique(connector.getActionConfig(), actionCode)) {
            throw new RuntimeException("动作编码已存在: " + actionCode);
        }

        // 3. 构建动作配置
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode action = mapper.createObjectNode();
        action.put("actionCode", actionCode);
        action.put("actionName", createVO.getActionName());
        action.put("description", createVO.getDescription());
        action.put("status", "2");
        action.put("version", 1);

        // 添加四步配置
        if (createVO.getBasicInfo() != null) {
            action.set("基础信息", createVO.getBasicInfo());
        }
        if (createVO.getInputConfig() != null) {
            action.set("入参配置", createVO.getInputConfig());
        }
        if (createVO.getOutputConfig() != null) {
            action.set("出参配置", createVO.getOutputConfig());
        }
        if (createVO.getDebugConfig() != null) {
            action.set("调试配置", createVO.getDebugConfig());
        }

        // 4. 添加到配置
        String updatedConfig = actionConfigHelper.addAction(connector.getActionConfig(), action);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("saveActionDraft success, connectorId: {}, actionCode: {}", connectorId, actionCode);
        return actionCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActionDraft(Long connectorId, String actionName, UpdateConnectorActionReqVO updateVO) {
        log.info("updateActionDraft start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 更新动作配置
        ObjectNode mutableAction = (ObjectNode) action;
        if (updateVO.getActionName() != null) {
            mutableAction.put("actionName", updateVO.getActionName());
        }
        if (updateVO.getDescription() != null) {
            mutableAction.put("description", updateVO.getDescription());
        }
        if (updateVO.getBasicInfo() != null) {
            mutableAction.set("基础信息", updateVO.getBasicInfo());
        }
        if (updateVO.getInputConfig() != null) {
            mutableAction.set("入参配置", updateVO.getInputConfig());
        }
        if (updateVO.getOutputConfig() != null) {
            mutableAction.set("出参配置", updateVO.getOutputConfig());
        }
        if (updateVO.getDebugConfig() != null) {
            mutableAction.set("调试配置", updateVO.getDebugConfig());
        }

        // 4. 保存配置
        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionName, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("updateActionDraft success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishAction(Long connectorId, String actionName) {
        log.info("publishAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证当前状态
        String currentStatus = getString(action, "status");
        if (!"2".equals(currentStatus)) {
            throw new RuntimeException("只有下架状态的动作才能发布");
        }

        // 4. 校验完整性
        ActionConfigHelper.ValidationResult validation = actionConfigHelper.validateActionCompleteness(action);
        if (!validation.isValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态和版本
        ObjectNode mutableAction = (ObjectNode) action;
        int newVersion = action.has("version") ? action.get("version").asInt() + 1 : 1;
        mutableAction.put("status", "1");
        mutableAction.put("version", newVersion);

        // 6. 保存配置
        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionName, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("publishAction success, connectorId: {}, actionName: {}, newVersion: {}", connectorId, actionName, newVersion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineAction(Long connectorId, String actionName) {
        log.info("offlineAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        String status = getString(action, "status");
        if (!"1".equals(status)) {
            throw new RuntimeException("只有已发布的动作才能下架");
        }

        // 4. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "2");

        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionName, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("offlineAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void republishAction(Long connectorId, String actionName) {
        log.info("republishAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        String status = getString(action, "status");
        if (!"2".equals(status)) {
            throw new RuntimeException("只有已下架的动作才能重新上线");
        }

        // 4. 校验完整性
        ActionConfigHelper.ValidationResult validation = actionConfigHelper.validateActionCompleteness(action);
        if (!validation.isValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "1");

        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionName, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("republishAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyAction(Long connectorId, String actionName) {
        log.info("copyAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找原动作
        JsonNode originalAction = actionConfigHelper.findAction(connector.getActionConfig(), actionName);
        if (originalAction == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 获取现有动作列表用于命名去重
        List<JsonNode> existingActions = actionConfigHelper.getActions(connector.getActionConfig());
        List<String> existingNames = existingActions.stream()
                .filter(a -> a.has("actionName") && a.get("actionName") != null && !a.get("actionName").isNull())
                .map(a -> a.get("actionName").asText())
                .collect(Collectors.toList());

        // 4. 生成唯一名称
        String originalName = getString(originalAction, "actionName");
        String newName = nameGenerator.generateCopyName(originalName, existingNames);

        // 5. 复制动作配置
        ObjectNode newAction = originalAction.deepCopy();
        newAction.put("actionName", newName);
        newAction.put("status", "2");

        // 6. 添加到配置
        String updatedConfig = actionConfigHelper.addAction(connector.getActionConfig(), newAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("copyAction success, connectorId: {}, originalName: {}, newName: {}",
                connectorId, originalName, newName);

        return newName;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAction(Long connectorId, String actionName) {
        log.info("deleteAction start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从配置中删除动作
        String updatedConfig = actionConfigHelper.removeAction(connector.getActionConfig(), actionName);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("deleteAction success, connectorId: {}, actionName: {}", connectorId, actionName);
    }

    @Override
    public ActionConfigHelper.ValidationResult validateActionForPublish(Long connectorId, String actionName) {
        log.info("validateActionForPublish start, connectorId: {}, actionName: {}", connectorId, actionName);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionName);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 校验完整性
        ActionConfigHelper.ValidationResult result = actionConfigHelper.validateActionCompleteness(action);

        log.info("validateActionForPublish success, connectorId: {}, actionName: {}, valid: {}",
                connectorId, actionName, result.isValid());
        return result;
    }

    // ==================== 辅助方法 ====================

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
    public Boolean saveActionConfig(Long connectorId, SaveActionConfigReqVO reqVO) {
        log.info("saveActionConfig start, connectorId: {}", connectorId);

        // 1. 查询并验证连接器实例
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            log.warn("Connector not found, connectorId: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 解析或创建 action_config 根配置
        ObjectNode rootActionConfig = parseOrCreateActionRootConfig(connector.getActionConfig());
        ObjectNode properties = rootActionConfig.withObject("properties");

        // 3. 从请求中提取动作编码（actionCode）
        String actionCode = extractActionCodeFromConfig(reqVO.getActionConfig());

        // 4. 检查动作是否已存在
        if (properties.has(actionCode)) {
            log.warn("Action already exists, connectorId: {}, actionCode: {}", connectorId, actionCode);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_ALREADY_EXISTS, actionCode);
        }

        // 5. 添加新动作配置
        properties.set(actionCode, reqVO.getActionConfig());

        // 6. 更新元数据版本
        updateActionMetadataVersion(rootActionConfig);

        // 7. 保存到数据库
        connector.setActionConfig(toJsonString(rootActionConfig));
        connectorRepository.updateById(connector);

        log.info("saveActionConfig success, connectorId: {}, actionCode: {}", connectorId, actionCode);
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
     * 配置格式: {"basic": {"actionName": "hahaha1"}, ...}
     *
     * @param configNode 动作配置节点
     * @return 动作编码（actionName）
     */
    private String extractActionCodeFromConfig(JsonNode configNode) {
        if (configNode == null || !configNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
        }

        JsonNode basicNode = configNode.get("basic");
        if (basicNode == null || !basicNode.isObject()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
        }

        JsonNode actionNameNode = basicNode.get("actionName");
        if (actionNameNode == null || actionNameNode.isNull()) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.INVALID_ACTION_CONFIG);
        }

        return actionNameNode.asText();
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
}
