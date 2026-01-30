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
import com.cmsr.onebase.module.flow.core.dal.database.FlowNodeActionRefRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorEnvDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeActionRefDO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.util.ActionConfigHelper;
import com.cmsr.onebase.module.flow.core.util.ActionNameGenerator;
import com.cmsr.onebase.module.flow.core.util.ConnectorConfigHelper;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorReqVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.mybatisflex.core.paginate.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Setter
@Service
public class FlowConnectorServiceImpl implements FlowConnectorService {

    @Autowired
    private FlowConnectorRepository connectorRepository;

    @Autowired
    private FlowConnectorEnvRepository connectorEnvRepository;

    @Autowired
    private FlowNodeActionRefRepository actionRefRepository;

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
        if (jsonNode == null || jsonNode instanceof NullNode) {
            return null;
        }
        if (jsonNode instanceof TextNode) {
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
            log.warn("Connector not found, id: {}", connectorId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 获取 config 字段
        String configJson = connector.getConfig();
        if (StringUtils.isBlank(configJson)) {
            log.info("Connector config is empty, connectorId: {}", connectorId);
            return new ArrayList<>();
        }

        // 3. 解析环境配置
        List<FlowConnectorEnvLiteVO> environments = connectorConfigParser.parseEnvironments(
                configJson, connector.getTypeCode());

        log.info("getEnvironments success, connectorId: {}, count: {}", connectorId, environments.size());
        return environments;
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

        // 2. 提取动作列表
        List<JsonNode> actions = configHelper.getActions(connector.getConfig());

        // 3. 转换为 VO
        List<ConnectorActionVO> result = new ArrayList<>();
        for (JsonNode action : actions) {
            ConnectorActionVO vo = ConnectorActionVO.builder()
                    .actionId(action.get("actionId").asText())
                    .actionName(action.get("actionName").asText())
                    .actionCode(action.get("actionCode").asText())
                    .description(action.has("description") ? action.get("description").asText() : null)
                    .status(action.get("status").asText())
                    .version(action.get("version").asInt())
                    .updateTime(connector.getUpdateTime())
                    .build();

            // 查询引用次数
            List<FlowNodeActionRefDO> refs = actionRefRepository.findByConnectorAndAction(
                    connectorId, vo.getActionId());
            vo.setUsedCount(refs.size());

            result.add(vo);
        }

        log.info("getActionList success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    public List<ConnectorActionVO> getActionInfos(Long connectorId) {
        return getActionList(connectorId);
    }

    public List<ConnectorActionLiteVO> getActionList(Long connectorId, Long envId) {
        log.info("getActionList start, connectorId: {}, envId: {}", connectorId, envId);

        // 1. 查询环境配置
        FlowConnectorEnvDO env = connectorEnvRepository.getById(envId);
        if (env == null) {
            log.warn("Environment config not found, envId: {}", envId);
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_ENV_NOT_CONFIGURED);
        }

        // 2. 解析 config 字段
        String configJson = env.getConfig();
        if (configJson == null || configJson.isEmpty() || "null".equals(configJson)) {
            log.info("Config is empty, return empty list, envId: {}", envId);
            return Collections.emptyList();
        }

        try {
            // 3. 提取 actions 对象
            JsonNode configNode = objectMapper.readTree(configJson);
            JsonNode actionsNode = configNode.get("actions");

            if (actionsNode == null || !actionsNode.isObject()) {
                log.info("Actions node not found or not an object, return empty list, envId: {}", envId);
                return Collections.emptyList();
            }

            // 4. 转换为 VO 列表
            List<ConnectorActionLiteVO> result = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = actionsNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode actionNode = entry.getValue();

                // 解析创建时间
                LocalDateTime createTime = null;
                JsonNode createTimeNode = actionNode.get("createTime");
                if (createTimeNode != null && !createTimeNode.isNull()) {
                    try {
                        createTime = LocalDateTime.parse(createTimeNode.asText());
                    } catch (Exception e) {
                        log.warn("Failed to parse createTime for action, using null. Value: {}, error: {}",
                                createTimeNode.asText(), e.getMessage());
                        createTime = null;
                    }
                }

                ConnectorActionLiteVO vo = ConnectorActionLiteVO.builder()
                        .actionId(getJsonText(actionNode, "actionId"))
                        .actionName(getJsonText(actionNode, "actionName"))
                        .actionCode(getJsonText(actionNode, "actionCode"))
                        .status(getJsonText(actionNode, "status"))
                        .version(actionNode.has("version") ? actionNode.get("version").asInt() : 1)
                        .createTime(createTime)
                        .build();

                result.add(vo);
            }

            // 5. 按创建时间倒序排序（null 值排最后）
            result.sort(Comparator.comparing(
                    ConnectorActionLiteVO::getCreateTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed());

            log.info("getActionList success, connectorId: {}, envId: {}, count: {}", connectorId, envId, result.size());
            return result;

        } catch (JsonProcessingException e) {
            log.error("Failed to parse action config, envId: {}, error: {}", envId, e.getMessage(), e);
            throw new RuntimeException("解析动作配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 辅助方法：安全获取 JSON 字段文本值
     */
    private String getJsonText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }

    @Override
    public ConnectorActionVO getActionDetail(Long connectorId, String actionId) {
        log.info("getActionDetail start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = configHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 转换为 VO
        ConnectorActionVO vo = ConnectorActionVO.builder()
                .actionId(action.get("actionId").asText())
                .actionName(action.get("actionName").asText())
                .actionCode(action.get("actionCode").asText())
                .description(action.has("description") ? action.get("description").asText() : null)
                .status(action.get("status").asText())
                .version(action.get("version").asInt())
                .updateTime(connector.getUpdateTime())
                .basicInfo(action.has("基础信息") ? action.get("基础信息") : null)
                .inputConfig(action.has("入参配置") ? action.get("入参配置") : null)
                .outputConfig(action.has("出参配置") ? action.get("出参配置") : null)
                .debugConfig(action.has("调试配置") ? action.get("调试配置") : null)
                .build();

        log.info("getActionDetail success, connectorId: {}, actionId: {}", connectorId, actionId);
        return vo;
    }

    @Override
    public String saveActionDraft(Long connectorId, CreateConnectorActionReqVO createVO) {
        log.info("saveActionDraft start, connectorId: {}, actionName: {}", connectorId, createVO.getActionName());

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 构建动作配置
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode action = mapper.createObjectNode();
        String actionId = configHelper.generateActionId();
        action.put("actionId", actionId);
        action.put("actionName", createVO.getActionName());
        action.put("actionCode", createVO.getActionCode());
        action.put("description", createVO.getDescription());
        action.put("status", "draft");
        action.put("version", 1);

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

        // 3. 添加到配置
        String updatedConfig = configHelper.addAction(connector.getConfig(), action);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("saveActionDraft success, connectorId: {}, actionId: {}", connectorId, actionId);
        return actionId;
    }

    @Override
    public void updateActionDraft(Long connectorId, String actionId, UpdateConnectorActionReqVO updateVO) {
        log.info("updateActionDraft start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = configHelper.findAction(connector.getConfig(), actionId);
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
        String updatedConfig = configHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("updateActionDraft success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    public void publishAction(Long connectorId, String actionId) {
        log.info("publishAction start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = configHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证当前状态
        String currentStatus = action.get("status").asText();
        if (!"draft".equals(currentStatus) && !"offline".equals(currentStatus)) {
            throw new RuntimeException("只有草稿或下架状态的动作才能发布");
        }

        // 4. 校验完整性
        ConnectorConfigHelper.ValidationResult validation = configHelper.validateForPublish(action);
        if (!validation.isValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态和版本
        ObjectNode mutableAction = (ObjectNode) action;
        int newVersion = action.has("version") ? action.get("version").asInt() + 1 : 1;
        mutableAction.put("status", "published");
        mutableAction.put("version", newVersion);

        // 6. 保存配置
        String updatedConfig = configHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("publishAction success, connectorId: {}, actionId: {}, newVersion: {}", connectorId, actionId, newVersion);
    }

    @Override
    public void offlineAction(Long connectorId, String actionId) {
        log.info("offlineAction start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = configHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        if (!"published".equals(action.get("status").asText())) {
            throw new RuntimeException("只有已发布的动作才能下架");
        }

        // 4. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "offline");

        String updatedConfig = configHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("offlineAction success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    public void republishAction(Long connectorId, String actionId) {
        log.info("republishAction start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = configHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        if (!"offline".equals(action.get("status").asText())) {
            throw new RuntimeException("只有已下架的动作才能重新上线");
        }

        // 4. 校验完整性
        ConnectorConfigHelper.ValidationResult validation = configHelper.validateForPublish(action);
        if (!validation.isValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "published");

        String updatedConfig = configHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("republishAction success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    public String copyAction(Long connectorId, String actionId) {
        log.info("copyAction start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找原动作
        JsonNode originalAction = configHelper.findAction(connector.getConfig(), actionId);
        if (originalAction == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 获取现有动作列表用于命名去重
        List<JsonNode> existingActions = configHelper.getActions(connector.getConfig());
        List<String> existingNames = existingActions.stream()
                .map(a -> a.get("actionName").asText())
                .collect(Collectors.toList());
        List<String> existingCodes = existingActions.stream()
                .map(a -> a.get("actionCode").asText())
                .collect(Collectors.toList());

        // 4. 生成唯一名称和编码
        String originalName = originalAction.get("actionName").asText();
        String originalCode = originalAction.get("actionCode").asText();
        String newName = nameGenerator.generateCopyName(originalName, existingNames);
        String newCode = nameGenerator.generateCopyCode(originalCode, existingCodes);

        // 5. 复制动作配置
        ObjectNode newAction = originalAction.deepCopy();
        newAction.put("actionId", configHelper.generateActionId());
        newAction.put("actionName", newName);
        newAction.put("actionCode", newCode);
        newAction.put("status", "draft");
        newAction.put("version", 1);

        // 6. 添加到配置
        String updatedConfig = configHelper.addAction(connector.getConfig(), newAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        String newActionId = newAction.get("actionId").asText();
        log.info("copyAction success, connectorId: {}, originalActionId: {}, newActionId: {}, newName: {}",
                connectorId, actionId, newActionId, newName);
        return newActionId;
    }

    @Override
    public void deleteAction(Long connectorId, String actionId) {
        log.info("deleteAction start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 检查引用关系
        List<FlowNodeActionRefDO> references = actionRefRepository.findByConnectorAndAction(
                connectorId, actionId);
        if (!references.isEmpty()) {
            // 转换为引用信息 VO
            List<ActionReferenceVO> refList = references.stream()
                    .map(ref -> ActionReferenceVO.builder()
                            .flowId(ref.getFlowVersion())
                            .nodeId(ref.getNodeId())
                            .build())
                    .collect(Collectors.toList());

            throw new RuntimeException("存在关联逻辑流，请先删除逻辑流内的动作节点！");
        }

        // 3. 从配置中删除动作
        String updatedConfig = configHelper.removeAction(connector.getConfig(), actionId);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("deleteAction success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    public ConnectorConfigHelper.ValidationResult validateActionForPublish(Long connectorId, String actionId) {
        log.info("validateActionForPublish start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = configHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 校验完整性
        ConnectorConfigHelper.ValidationResult result = configHelper.validateForPublish(action);
        log.info("validateActionForPublish success, connectorId: {}, actionId: {}, valid: {}",
                connectorId, actionId, result.isValid());
        return result;
    }

    // ==================== 动作配置管理方法实现 v2.0 ====================

    @Override
    public List<ActionInfoVO> getActionInfosV2(Long connectorId) {
        log.info("getActionInfosV2 start, connectorId: {}", connectorId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 提取动作列表
        List<JsonNode> actions = actionConfigHelper.getActions(connector.getConfig());

        // 3. 批量查询引用关系
        List<String> actionIds = actions.stream()
                .map(a -> a.get("actionId").asText())
                .collect(Collectors.toList());
        Map<String, Integer> usedCountMap = getUsedCountMap(connectorId, actionIds);

        // 4. 转换为 VO
        List<ActionInfoVO> result = new ArrayList<>();
        for (JsonNode action : actions) {
            String actionId = action.get("actionId").asText();
            ActionInfoVO vo = ActionInfoVO.builder()
                    .actionId(actionId)
                    .actionName(action.get("actionName").asText())
                    .actionCode(action.get("actionCode").asText())
                    .description(action.has("description") ? action.get("description").asText() : null)
                    .status(action.get("status").asText())
                    .version(action.get("version").asInt())
                    .usedCount(usedCountMap.getOrDefault(actionId, 0))
                    .updateTime(connector.getUpdateTime())
                    .build();
            result.add(vo);
        }

        log.info("getActionInfosV2 success, connectorId: {}, count: {}", connectorId, result.size());
        return result;
    }

    @Override
    public ActionDetailVO getActionDetailV2(Long connectorId, String actionId) {
        log.info("getActionDetailV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 查询引用次数
        List<FlowNodeActionRefDO> refs = actionRefRepository.findByConnectorAndAction(connectorId, actionId);

        // 4. 转换为 VO
        ActionDetailVO vo = ActionDetailVO.builder()
                .actionId(action.get("actionId").asText())
                .actionName(action.get("actionName").asText())
                .actionCode(action.get("actionCode").asText())
                .description(action.has("description") ? action.get("description").asText() : null)
                .status(action.get("status").asText())
                .version(action.get("version").asInt())
                .basicInfo(action.has("基础信息") ? action.get("基础信息") : null)
                .inputConfig(action.has("入参配置") ? action.get("入参配置") : null)
                .outputConfig(action.has("出参配置") ? action.get("出参配置") : null)
                .debugConfig(action.has("调试配置") ? action.get("调试配置") : null)
                .usedCount(refs.size())
                .updateTime(connector.getUpdateTime())
                .build();

        log.info("getActionDetailV2 success, connectorId: {}, actionId: {}", connectorId, actionId);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveActionDraftV2(Long connectorId, SaveActionReqVO reqVO) {
        log.info("saveActionDraftV2 start, connectorId: {}, actionName: {}", connectorId, reqVO.getActionName());

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 生成动作ID
        String actionId = actionConfigHelper.generateActionId();

        // 3. 构建动作配置
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode action = mapper.createObjectNode();
        action.put("actionId", actionId);
        action.put("actionName", reqVO.getActionName());
        action.put("actionCode", reqVO.getActionCode());
        action.put("description", reqVO.getDescription());
        action.put("status", "draft");
        action.put("version", 1);

        // 添加四步配置
        if (reqVO.getBasicInfo() != null) {
            action.set("基础信息", reqVO.getBasicInfo());
        }
        if (reqVO.getInputConfig() != null) {
            action.set("入参配置", reqVO.getInputConfig());
        }
        if (reqVO.getOutputConfig() != null) {
            action.set("出参配置", reqVO.getOutputConfig());
        }
        if (reqVO.getDebugConfig() != null) {
            action.set("调试配置", reqVO.getDebugConfig());
        }

        // 4. 添加到配置
        String updatedConfig = actionConfigHelper.addAction(connector.getConfig(), action);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("saveActionDraftV2 success, connectorId: {}, actionId: {}", connectorId, actionId);
        return actionId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActionDraftV2(Long connectorId, String actionId, UpdateActionReqVO reqVO) {
        log.info("updateActionDraftV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 更新动作配置
        ObjectNode mutableAction = (ObjectNode) action;
        if (reqVO.getActionName() != null) {
            mutableAction.put("actionName", reqVO.getActionName());
        }
        if (reqVO.getDescription() != null) {
            mutableAction.put("description", reqVO.getDescription());
        }
        if (reqVO.getBasicInfo() != null) {
            mutableAction.set("基础信息", reqVO.getBasicInfo());
        }
        if (reqVO.getInputConfig() != null) {
            mutableAction.set("入参配置", reqVO.getInputConfig());
        }
        if (reqVO.getOutputConfig() != null) {
            mutableAction.set("出参配置", reqVO.getOutputConfig());
        }
        if (reqVO.getDebugConfig() != null) {
            mutableAction.set("调试配置", reqVO.getDebugConfig());
        }

        // 4. 保存配置
        String updatedConfig = actionConfigHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("updateActionDraftV2 success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishActionV2(Long connectorId, String actionId) {
        log.info("publishActionV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证当前状态
        String currentStatus = action.get("status").asText();
        if (!"draft".equals(currentStatus) && !"offline".equals(currentStatus)) {
            throw new RuntimeException("只有草稿或下架状态的动作才能发布");
        }

        // 4. 校验完整性
        ValidationResultVO validation = validateActionForPublishV2(connectorId, actionId);
        if (!validation.getValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态和版本
        ObjectNode mutableAction = (ObjectNode) action;
        int newVersion = action.has("version") ? action.get("version").asInt() + 1 : 1;
        mutableAction.put("status", "published");
        mutableAction.put("version", newVersion);

        // 6. 保存配置
        String updatedConfig = actionConfigHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("publishActionV2 success, connectorId: {}, actionId: {}, newVersion: {}", connectorId, actionId, newVersion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineActionV2(Long connectorId, String actionId) {
        log.info("offlineActionV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        if (!"published".equals(action.get("status").asText())) {
            throw new RuntimeException("只有已发布的动作才能下架");
        }

        // 4. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "offline");

        String updatedConfig = actionConfigHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("offlineActionV2 success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void republishActionV2(Long connectorId, String actionId) {
        log.info("republishActionV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 验证状态
        if (!"offline".equals(action.get("status").asText())) {
            throw new RuntimeException("只有已下架的动作才能重新上线");
        }

        // 4. 校验完整性
        ValidationResultVO validation = validateActionForPublishV2(connectorId, actionId);
        if (!validation.getValid()) {
            throw new RuntimeException("请完善动作信息后再进行发布：" + String.join("; ", validation.getErrors()));
        }

        // 5. 更新状态
        ObjectNode mutableAction = (ObjectNode) action;
        mutableAction.put("status", "published");

        String updatedConfig = actionConfigHelper.updateAction(connector.getConfig(), actionId, mutableAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("republishActionV2 success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CopyActionRespVO copyActionV2(Long connectorId, String actionId) {
        log.info("copyActionV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找原动作
        JsonNode originalAction = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (originalAction == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 获取现有动作列表用于命名去重
        List<JsonNode> existingActions = actionConfigHelper.getActions(connector.getConfig());
        List<String> existingNames = existingActions.stream()
                .map(a -> a.get("actionName").asText())
                .collect(Collectors.toList());
        List<String> existingCodes = existingActions.stream()
                .map(a -> a.get("actionCode").asText())
                .collect(Collectors.toList());

        // 4. 生成唯一名称和编码
        String originalName = originalAction.get("actionName").asText();
        String originalCode = originalAction.get("actionCode").asText();
        String newName = nameGenerator.generateCopyName(originalName, existingNames);
        String newCode = nameGenerator.generateCopyCode(originalCode, existingCodes);

        // 5. 复制动作配置
        ObjectNode newAction = originalAction.deepCopy();
        String newActionId = actionConfigHelper.generateActionId();
        newAction.put("actionId", newActionId);
        newAction.put("actionName", newName);
        newAction.put("actionCode", newCode);
        newAction.put("status", "draft");
        newAction.put("version", 1);

        // 6. 添加到配置
        String updatedConfig = actionConfigHelper.addAction(connector.getConfig(), newAction);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("copyActionV2 success, connectorId: {}, originalActionId: {}, newActionId: {}, newName: {}",
                connectorId, actionId, newActionId, newName);

        return CopyActionRespVO.builder()
                .actionId(newActionId)
                .actionName(newName)
                .actionCode(newCode)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActionV2(Long connectorId, String actionId) {
        log.info("deleteActionV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 检查引用关系
        List<FlowNodeActionRefDO> references = actionRefRepository.findByConnectorAndAction(connectorId, actionId);
        if (!references.isEmpty()) {
            throw new RuntimeException("存在关联逻辑流，请先删除逻辑流内的动作节点！");
        }

        // 3. 从配置中删除动作
        String updatedConfig = actionConfigHelper.removeAction(connector.getConfig(), actionId);
        connector.setConfig(updatedConfig);
        connectorRepository.updateById(connector);

        log.info("deleteActionV2 success, connectorId: {}, actionId: {}", connectorId, actionId);
    }

    @Override
    public ValidationResultVO validateActionForPublishV2(Long connectorId, String actionId) {
        log.info("validateActionForPublishV2 start, connectorId: {}, actionId: {}", connectorId, actionId);

        // 1. 查询连接器
        FlowConnectorDO connector = connectorRepository.getById(connectorId);
        if (connector == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.CONNECTOR_NOT_EXISTS);
        }

        // 2. 查找动作
        JsonNode action = actionConfigHelper.findAction(connector.getConfig(), actionId);
        if (action == null) {
            throw ServiceExceptionUtil.exception(FlowErrorCodeConstants.ACTION_NOT_EXISTS);
        }

        // 3. 校验完整性
        List<String> errors = new ArrayList<>();

        // 检查基础信息
        if (!action.has("基础信息") || action.get("基础信息") == null || action.get("基础信息").isNull()) {
            errors.add("基础信息不能为空");
        }

        // 检查入参配置
        if (!action.has("入参配置") || action.get("入参配置") == null || action.get("入参配置").isNull()) {
            errors.add("入参配置不能为空");
        }

        // 检查出参配置
        if (!action.has("出参配置") || action.get("出参配置") == null || action.get("出参配置").isNull()) {
            errors.add("出参配置不能为空");
        }

        boolean isValid = errors.isEmpty();

        log.info("validateActionForPublishV2 success, connectorId: {}, actionId: {}, valid: {}",
                connectorId, actionId, isValid);
        return ValidationResultVO.builder()
                .valid(isValid)
                .errors(errors)
                .build();
    }

    // ==================== 辅助方法 ====================

    /**
     * 批量获取动作的使用次数
     *
     * @param connectorId 连接器ID
     * @param actionIds   动作ID列表
     * @return 动作ID -> 使用次数的映射
     */
    private Map<String, Integer> getUsedCountMap(Long connectorId, List<String> actionIds) {
        if (actionIds.isEmpty()) {
            return new HashMap<>();
        }

        List<FlowNodeActionRefDO> refs = actionRefRepository.findByConnectorIdAndActionIds(
                connectorId, actionIds);

        Map<String, Integer> countMap = new HashMap<>();
        for (String actionId : actionIds) {
            countMap.put(actionId, 0);
        }

        for (FlowNodeActionRefDO ref : refs) {
            String actionId = ref.getActionId();
            countMap.put(actionId, countMap.getOrDefault(actionId, 0) + 1);
        }

        return countMap;
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
}
