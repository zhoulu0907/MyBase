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

    @Override
    public JsonNode getActionValueById(Long id, String actionName) {
        log.info("getActionValueById start, id: {}, actionName: {}", id, actionName);
        FlowConnectorDO connector = connectorRepository.getById(id);
        if (connector == null) {
            log.warn("Connector not found, id: {}", id);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }
        return parseActionValueFromConnector(connector, actionName, id.toString());
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
            // 安全获取字段值
            String actionCode = getString(action, "actionCode");
            if (actionCode == null) {
                log.warn("Action missing actionCode, skipping: {}", action);
                continue; // 跳过没有 actionCode 的动作
            }

            ConnectorActionVO vo = ConnectorActionVO.builder()
                    .actionCode(actionCode)
                    .actionName(getString(action, "actionName"))
                    .description(getString(action, "description"))
                    .status(getString(action, "status"))
                    .version(getInt(action, "version"))
                    .updateTime(connector.getUpdateTime())
                    .build();
            result.add(vo);
        }

        log.info("getActionList success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    public List<ConnectorActionVO> getActionInfos(Long connectorId) {
        return getActionList(connectorId);
    }

    @Override
    public JsonNode getActionSchema(Long connectorId, String actionCode) {
        log.info("getActionSchema start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 获取 Formily Schema
        JsonNode schema = actionConfigHelper.getActionSchema(connector.getActionConfig(), actionCode);
        if (schema == null) {
            throw new RuntimeException("动作不存在: " + actionCode);
        }

        log.info("getActionSchema success, connectorId: {}, actionCode: {}", connectorId, actionCode);
        return schema;
    }

    @Override
    public ConnectorActionVO getActionDetail(Long connectorId, String actionCode) {
        log.info("getActionDetail start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作 - 从 action_config 字段读取
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 转换为 VO - 使用安全的字段访问方法
        ConnectorActionVO vo = ConnectorActionVO.builder()
                .actionCode(actionCode)
                .actionName(getString(action, "actionName"))
                .description(getString(action, "description"))
                .status(getString(action, "status"))
                .version(getInt(action, "version"))
                .basicInfo(action.has("基础信息") && action.get("基础信息") != null ? action.get("基础信息") : null)
                .inputConfig(action.has("入参配置") && action.get("入参配置") != null ? action.get("入参配置") : null)
                .outputConfig(action.has("出参配置") && action.get("出参配置") != null ? action.get("出参配置") : null)
                .debugConfig(action.has("调试配置") && action.get("调试配置") != null ? action.get("调试配置") : null)
                .updateTime(connector.getUpdateTime())
                .build();

        log.info("getActionDetail success, connectorId: {}, actionCode: {}", connectorId, actionCode);
        return vo;
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
        action.put("status", "draft");
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
    public void updateActionDraft(Long connectorId, String actionCode, UpdateConnectorActionReqVO updateVO) {
        log.info("updateActionDraft start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
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
        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionCode, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("updateActionDraft success, connectorId: {}, actionCode: {}", connectorId, actionCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishAction(Long connectorId, String actionCode) {
        log.info("publishAction start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证当前状态
        String currentStatus = getString(action, "status");
        if (!"draft".equals(currentStatus) && !"offline".equals(currentStatus)) {
            throw new RuntimeException("只有草稿或下架状态的动作才能发布");
        }

        // 4. 校验完整性
        ActionConfigHelper.ValidationResult validation = actionConfigHelper.validateActionCompleteness(action);
        if (!validation.isValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态和版本
        ObjectNode mutableAction = (ObjectNode) action;
        int newVersion = action.has("version") ? action.get("version").asInt() + 1 : 1;
        mutableAction.put("status", "published");
        mutableAction.put("version", newVersion);

        // 6. 保存配置
        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionCode, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("publishAction success, connectorId: {}, actionCode: {}, newVersion: {}", connectorId, actionCode, newVersion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineAction(Long connectorId, String actionCode) {
        log.info("offlineAction start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        String status = getString(action, "status");
        if (!"published".equals(status)) {
            throw new RuntimeException("只有已发布的动作才能下架");
        }

        // 4. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "offline");

        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionCode, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("offlineAction success, connectorId: {}, actionCode: {}", connectorId, actionCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void republishAction(Long connectorId, String actionCode) {
        log.info("republishAction start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        String status = getString(action, "status");
        if (!"offline".equals(status)) {
            throw new RuntimeException("只有已下架的动作才能重新上线");
        }

        // 4. 校验完整性
        ActionConfigHelper.ValidationResult validation = actionConfigHelper.validateActionCompleteness(action);
        if (!validation.isValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "published");

        String updatedConfig = actionConfigHelper.updateAction(connector.getActionConfig(), actionCode, mutableAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("republishAction success, connectorId: {}, actionCode: {}", connectorId, actionCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyAction(Long connectorId, String actionCode) {
        log.info("copyAction start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找原动作
        JsonNode originalAction = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
        if (originalAction == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 获取现有动作列表用于命名去重
        List<JsonNode> existingActions = actionConfigHelper.getActions(connector.getActionConfig());
        List<String> existingNames = existingActions.stream()
                .filter(a -> a.has("actionName") && a.get("actionName") != null && !a.get("actionName").isNull())
                .map(a -> a.get("actionName").asText())
                .collect(Collectors.toList());
        List<String> existingCodes = existingActions.stream()
                .filter(a -> a.has("actionCode") && a.get("actionCode") != null && !a.get("actionCode").isNull())
                .map(a -> a.get("actionCode").asText())
                .collect(Collectors.toList());

        // 4. 生成唯一名称和编码
        String originalName = getString(originalAction, "actionName");
        String originalCode = actionCode;
        String newName = nameGenerator.generateCopyName(originalName, existingNames);
        String newCode = nameGenerator.generateCopyCode(originalCode, existingCodes);

        // 5. 复制动作配置
        ObjectNode newAction = originalAction.deepCopy();
        newAction.put("actionName", newName);
        newAction.put("actionCode", newCode);
        newAction.put("status", "draft");
        newAction.put("version", 1);

        // 6. 添加到配置
        String updatedConfig = actionConfigHelper.addAction(connector.getActionConfig(), newAction);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("copyAction success, connectorId: {}, originalCode: {}, newCode: {}, newName: {}",
                connectorId, actionCode, newCode, newName);

        return newCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAction(Long connectorId, String actionCode) {
        log.info("deleteAction start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 从配置中删除动作
        String updatedConfig = actionConfigHelper.removeAction(connector.getActionConfig(), actionCode);
        connector.setActionConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("deleteAction success, connectorId: {}, actionCode: {}", connectorId, actionCode);
    }

    @Override
    public ActionConfigHelper.ValidationResult validateActionForPublish(Long connectorId, String actionCode) {
        log.info("validateActionForPublish start, connectorId: {}, actionCode: {}", connectorId, actionCode);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getActionConfig(), actionCode);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 校验完整性
        ActionConfigHelper.ValidationResult result = actionConfigHelper.validateActionCompleteness(action);

        log.info("validateActionForPublish success, connectorId: {}, actionCode: {}, valid: {}",
                connectorId, actionCode, result.isValid());
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
