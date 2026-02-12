package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.nodes.ScriptNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.CommonNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorScriptRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorHttpRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorTableDef;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorMapper;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowNodeConfigMapper;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowProcessMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorScriptDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.external.FlowFieldTypeProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：huangjie
 *                  @Date：2025/9/28 15:12
 */
@Slf4j
@Component
public class FlowGraphBuilder {

    @Setter
    @Autowired
    private FlowFieldTypeProvider flowFieldTypeProvider;

    @Setter
    @Autowired
    private com.cmsr.onebase.module.flow.core.config.FlowProperties flowProperties;

    @Setter
    @Autowired
    private FlowConnectorScriptRepository connectorScriptRepository;

    @Setter
    @Autowired
    private FlowConnectorMapper flowConnectorMapper;

    @Setter
    @Autowired
    private FlowNodeConfigMapper flowNodeConfigMapper;

    @Setter
    @Autowired
    private FlowConnectorHttpRepository connectorHttpRepository;

    @Autowired
    private FlowProcessMapper flowProcessMapper;

    public JsonGraph build(Long applicationId, String json) {
        return build(applicationId, json, null);
    }

    public JsonGraph build(Long applicationId, String json, Long processId) {
        Long traceProcessId = flowProperties.getTraceProcessId();
        if (processId != null && processId.equals(traceProcessId)) {
            log.info("[TRACE-{}] FlowGraphBuilder.build开始: applicationId={}", processId, applicationId);
        }
        JsonGraph jsonGraph = JsonUtils.parseObject(json, JsonGraph.class);
        if (processId != null && processId.equals(traceProcessId)) {
            log.info("[TRACE-{}] JsonGraph解析完成: nodeCount={}", processId,
                    jsonGraph != null && jsonGraph.getNodes() != null ? jsonGraph.getNodes().size() : 0);
        }
        addLoopContextToNodes(jsonGraph);
        enrichNodeData(applicationId, jsonGraph, processId);
        flowFieldTypeProvider.completeFieldType(applicationId, jsonGraph);
        if (processId != null && processId.equals(traceProcessId)) {
            log.info("[TRACE-{}] FlowGraphBuilder.build完成", processId);
        }
        return jsonGraph;
    }

    private void addLoopContextToNodes(JsonGraph jsonGraph) {
        if (jsonGraph == null || jsonGraph.getNodes() == null) {
            return;
        }
        for (JsonGraphNode node : jsonGraph.getNodes()) {
            traverseNodeAndAddLoopContext(node, new InLoopDepth());
        }
    }

    private void enrichNodeData(Long applicationId, JsonGraph jsonGraph, Long processId) {
        Long traceProcessId = flowProperties.getTraceProcessId();
        if (jsonGraph == null || jsonGraph.getNodes() == null) {
            return;
        }
        if (processId != null && processId.equals(traceProcessId)) {
            log.info("[TRACE-{}] enrichNodeData开始: applicationId={}, nodeCount={}",
                    processId, applicationId, jsonGraph.getNodes().size());
        }
        for (JsonGraphNode node : jsonGraph.getNodes()) {
            traverseNodeAndEnrichData(applicationId, node, processId);
        }
        if (processId != null && processId.equals(traceProcessId)) {
            log.info("[TRACE-{}] enrichNodeData完成", processId);
        }
    }

    private void traverseNodeAndAddLoopContext(JsonGraphNode node, InLoopDepth loopDeepMap) {
        if (StringUtils.equals(node.getType(), "loop")) {
            loopDeepMap = new InLoopDepth(loopDeepMap);
            for (String key : loopDeepMap.keySet()) {
                loopDeepMap.put(key, loopDeepMap.get(key) + 1);
            }
            loopDeepMap.put(node.getId(), 0);
        }
        if (!(StringUtils.equals(node.getType(), "loop")) && MapUtils.isNotEmpty(loopDeepMap)) {
            node.getData().setInLoop(Boolean.TRUE);
            node.getData().setInLoopDepth(loopDeepMap);
        }
        if (CollectionUtils.isNotEmpty(node.getBlocks())) {
            for (JsonGraphNode childNode : node.getBlocks()) {
                traverseNodeAndAddLoopContext(childNode, loopDeepMap);
            }
        }
    }

    private void traverseNodeAndEnrichData(Long applicationId, JsonGraphNode node, Long processId) {
        Long traceProcessId = flowProperties.getTraceProcessId();
        String nodeId = node.getId();
        String nodeType = node.getData() != null ? node.getData().getClass().getSimpleName() : "null";
        boolean isTrace = processId != null && processId.equals(traceProcessId);

        if (isTrace) {
            log.info("[TRACE-{}] traverseNodeAndEnrichData开始: applicationId={}, nodeId={}, nodeType={}",
                    processId, applicationId, nodeId, nodeType);
        } else {
            log.debug("[FlowGraphBuilder] traverseNodeAndEnrichData开始: applicationId={}, nodeId={}, nodeType={}",
                    applicationId, nodeId, nodeType);
        }

        if (node.getData() instanceof ScriptNodeData scriptNodeData) {
            if (isTrace) {
                log.info("[TRACE-{}] 处理ScriptNodeData: nodeId={}, actionId={}, actionUuid={}",
                        processId, nodeId, scriptNodeData.getActionId(), scriptNodeData.getActionUuid());
            } else {
                log.debug("[FlowGraphBuilder] 处理ScriptNodeData: nodeId={}, actionId={}, actionUuid={}",
                        nodeId, scriptNodeData.getActionId(), scriptNodeData.getActionUuid());
            }
            FlowConnectorScriptDO connectorScriptDO = TenantManager
                    .withoutTenantCondition(() -> connectorScriptRepository.findByApplicationAndUuid(applicationId,
                            scriptNodeData.getActionId(), scriptNodeData.getActionUuid()));
            scriptNodeData.setScript(connectorScriptDO.getRawScript());
            scriptNodeData.setInputSchema(connectorScriptDO.getInputSchema());
            scriptNodeData.setOutputSchema(connectorScriptDO.getOutputSchema());
            if (isTrace) {
                log.info("[TRACE-{}] ScriptNodeData处理完成: nodeId={}, scriptLength={}, inputSchema={}, outputSchema={}",
                        processId, nodeId,
                        connectorScriptDO.getRawScript() != null ? connectorScriptDO.getRawScript().length() : 0,
                        connectorScriptDO.getInputSchema(),
                        connectorScriptDO.getOutputSchema());
            } else {
                log.debug(
                        "[FlowGraphBuilder] ScriptNodeData处理完成: nodeId={}, scriptLength={}, inputSchema={}, outputSchema={}",
                        nodeId,
                        connectorScriptDO.getRawScript() != null ? connectorScriptDO.getRawScript().length() : 0,
                        connectorScriptDO.getInputSchema(),
                        connectorScriptDO.getOutputSchema());
            }
        }
        if (node.getData() instanceof CommonNodeData commonNodeData) {
            if (isTrace) {
                log.info("[TRACE-{}] 处理CommonNodeData: nodeId={}, connectorCode={}, nodeCode={}",
                        processId, nodeId, commonNodeData.getConnectorCode(), commonNodeData.getNodeCode());
            } else {
                log.debug("[FlowGraphBuilder] 处理CommonNodeData: nodeId={}, connectorCode={}, nodeCode={}",
                        nodeId, commonNodeData.getConnectorCode(), commonNodeData.getNodeCode());
            }
            // 加载连接器配置
            FlowConnectorDO connectorDO = TenantManager.withoutTenantCondition(() -> flowConnectorMapper
                    .selectByApplicationAndTypeCode(applicationId, commonNodeData.getConnectorCode()));
            if (connectorDO != null) {
                Map<String, Object> connectorConfig = new HashMap<>();
                if (StringUtils.isNotBlank(connectorDO.getConfig())) {
                    connectorConfig = JsonUtils.parseObject(connectorDO.getConfig(), Map.class);
                }
                commonNodeData.setConnectorConfig(connectorConfig);
            }

            // 加载节点配置
            FlowNodeConfigDO nodeConfigDO = TenantManager
                    .withoutTenantCondition(() -> flowNodeConfigMapper.selectByCode(commonNodeData.getNodeCode()));
            if (nodeConfigDO != null) {
                Map<String, Object> componentContext = new HashMap<>();
                if (StringUtils.isNotBlank(nodeConfigDO.getConnConfigJson())) {
                    componentContext = JsonUtils.parseObject(nodeConfigDO.getConnConfigJson(), Map.class);
                }
                commonNodeData.setComponentContext(componentContext);

                Map<String, Object> actionConfig = new HashMap<>();
                if (StringUtils.isNotBlank(nodeConfigDO.getActionConfigJson())) {
                    actionConfig = JsonUtils.parseObject(nodeConfigDO.getActionConfigJson(), Map.class);
                }
                commonNodeData.setActionConfig(actionConfig);
            }
            if (isTrace) {
                log.info("[TRACE-{}] CommonNodeData处理完成: nodeId={}, connectorConfig={}, actionConfig={}",
                        processId, nodeId, connectorDO != null, nodeConfigDO != null);
            } else {
                log.debug("[FlowGraphBuilder] CommonNodeData处理完成: nodeId={}, connectorConfig={}, actionConfig={}",
                        nodeId, connectorDO != null, nodeConfigDO != null);
            }
        }
        // ========== HttpNodeData处理 ==========
        if (node.getData() instanceof HttpNodeData httpNodeData) {
            String actionName = httpNodeData.getActionName();
            String envName = httpNodeData.getEnvName();

            if (isTrace) {
                log.info("[TRACE-{}] 开始加载HTTP节点配置: applicationId={}, actionName={}, envName={}, nodeId={}",
                        processId, applicationId, actionName, envName, nodeId);
            } else {
                log.debug("[FlowGraphBuilder] 开始加载HTTP节点配置: applicationId={}, actionName={}, envName={}, nodeId={}",
                        applicationId, actionName, envName, nodeId);
            }

            try {
                // 步骤 1: 获取连接器
                String connectorUuid = httpNodeData.getConnectorUuid();
                if (StringUtils.isBlank(connectorUuid)) {
                    log.error("[FlowGraphBuilder] 节点数据缺失connectorUuid: nodeId={}", nodeId);
                    return;
                }

                QueryWrapper connectorQuery = QueryWrapper.create()
                        .where(FlowConnectorTableDef.FLOW_CONNECTOR.APPLICATION_ID.eq(applicationId))
                        .and(FlowConnectorTableDef.FLOW_CONNECTOR.CONNECTOR_UUID.eq(connectorUuid));

                FlowConnectorDO connectorDO = TenantManager
                        .withoutTenantCondition(() -> flowConnectorMapper.selectOneByQuery(connectorQuery));

                if (connectorDO == null) {
                    log.error("[FlowGraphBuilder] 连接器不存在: connectorUuid={}", connectorUuid);
                    return;
                }

                if (connectorDO.getActiveStatus() == null || connectorDO.getActiveStatus() == 0) {
                    log.warn("[FlowGraphBuilder] 连接器已禁用: connectorUuid={}", connectorUuid);
                    return;
                }

                // 步骤 2: 解析环境配置 — config.properties[envName].envConfig
                Map<String, Object> envConfig = null;
                if (StringUtils.isNotBlank(connectorDO.getConfig())) {
                    try {
                        Map<String, Object> configRoot = JsonUtils.parseObject(connectorDO.getConfig(), Map.class);
                        if (configRoot != null) {
                            Object propertiesObj = configRoot.get("properties");
                            if (propertiesObj instanceof Map) {
                                Map<String, Object> properties = (Map<String, Object>) propertiesObj;
                                Map<String, Object> envEntry = null;
                                if (StringUtils.isNotBlank(envName) && properties.containsKey(envName)) {
                                    envEntry = (Map<String, Object>) properties.get(envName);
                                } else if (!properties.isEmpty()) {
                                    // fallback: 取第一个环境
                                    envEntry = (Map<String, Object>) properties.values().iterator().next();
                                }
                                if (envEntry != null) {
                                    Object envConfigObj = envEntry.get("envConfig");
                                    if (envConfigObj instanceof Map) {
                                        envConfig = (Map<String, Object>) envConfigObj;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("[FlowGraphBuilder] 解析连接器环境配置失败: connectorUuid={}", connectorUuid, e);
                    }
                }
                httpNodeData.setConnectorConfig(envConfig);

                // 步骤 3: 解析动作配置 — action_config.properties[actionName]
                Map<String, Object> httpActionConfig = null;

//                todo:解析动作配置流程:
//                请参考http-flow-exp.json，为flow_process.process_definition数据
//                a）在FlowGraphBuilder.traverseNodeAndEnrichData方法中实现，步骤 3: 解析动作配置
//                b）根据processId从flow_process中获取process_definition信息
//                c）根据nodeId从process_definition中找到http-node信息
//                d）将actionParams.{actionName}（此处为action1）下的内容解析出来，作为httpActionConfig

                httpNodeData.setActionConfig(httpActionConfig);

                if (isTrace) {
                    log.info("[TRACE-{}] HTTP节点配置加载成功: actionName={}, envName={}",
                            processId, actionName, envName);
                } else {
                    log.info("[FlowGraphBuilder] HTTP节点配置加载成功: actionName={}, envName={}",
                            actionName, envName);
                }

            } catch (Exception e) {
                log.error("[FlowGraphBuilder] 加载HTTP节点配置异常: error={}", e.getMessage(), e);
            }
        }

        // 递归处理子节点
        if (CollectionUtils.isNotEmpty(node.getBlocks())) {
            if (isTrace) {
                log.info("[TRACE-{}] 开始递归处理子节点: parentId={}, childrenCount={}",
                        processId, nodeId, node.getBlocks().size());
            } else {
                log.debug("[FlowGraphBuilder] 开始递归处理子节点: parentId={}, childrenCount={}",
                        nodeId, node.getBlocks().size());
            }
            for (JsonGraphNode child : node.getBlocks()) {
                traverseNodeAndEnrichData(applicationId, child, processId);
            }
        }

        if (isTrace) {
            log.info("[TRACE-{}] traverseNodeAndEnrichData完成: applicationId={}, nodeId={}, nodeType={}",
                    processId, applicationId, nodeId, nodeType);
        } else {
            log.debug("[FlowGraphBuilder] traverseNodeAndEnrichData完成: applicationId={}, nodeId={}, nodeType={}",
                    applicationId, nodeId, nodeType);
        }
    }

    /**
     * 从 process_definition 中提取 HTTP 节点的动作配置
     *
     * @param processDefinitionJson 流程定义 JSON 字符串
     * @param nodeId 节点 ID
     * @param actionName 动作名称
     * @param processId 流程 ID（用于日志）
     * @return 动作配置 Map，提取失败返回 null
     */
    private Map<String, Object> extractActionConfigFromProcessDefinition(
            String processDefinitionJson, String nodeId, String actionName, Long processId) {

        Long traceProcessId = flowProperties.getTraceProcessId();
        boolean isTrace = processId != null && processId.equals(traceProcessId);

        try {
            if (StringUtils.isBlank(processDefinitionJson)) {
                if (isTrace) {
                    log.warn("[TRACE-{}] process_definition 为空", processId);
                }
                return null;
            }

            JsonNode processRoot = JsonUtils.parseObject(processDefinitionJson, JsonNode.class);
            JsonNode nodesArray = processRoot.get("nodes");

            if (nodesArray == null || !nodesArray.isArray()) {
                if (isTrace) {
                    log.warn("[TRACE-{}] process_definition 中没有 nodes 数组", processId);
                }
                return null;
            }

            // 遍历节点查找目标节点
            for (JsonNode nodeJson : nodesArray) {
                JsonNode idNode = nodeJson.get("id");
                if (idNode != null && nodeId.equals(idNode.asText())) {
                    // 找到目标节点
                    JsonNode dataNode = nodeJson.get("data");
                    if (dataNode == null) {
                        if (isTrace) {
                            log.warn("[TRACE-{}] 节点没有 data 字段: nodeId={}", processId, nodeId);
                        }
                        return null;
                    }

                    JsonNode actionParamsNode = dataNode.get("actionParams");
                    if (actionParamsNode == null) {
                        if (isTrace) {
                            log.warn("[TRACE-{}] 节点没有 actionParams 字段: nodeId={}", processId, nodeId);
                        }
                        return null;
                    }

                    JsonNode actionConfigNode = actionParamsNode.get(actionName);
                    if (actionConfigNode == null) {
                        if (isTrace) {
                            log.warn("[TRACE-{}] actionParams 中没有找到动作: nodeId={}, actionName={}",
                                    processId, nodeId, actionName);
                        }
                        return null;
                    }

                    Map<String, Object> result = JsonUtils.parseObject(
                            actionConfigNode.toString(), Map.class);

                    if (isTrace) {
                        log.info("[TRACE-{}] 成功提取动作配置: nodeId={}, actionName={}, configSize={}",
                                processId, nodeId, actionName, result != null ? result.size() : 0);
                    }

                    return result;
                }
            }

            if (isTrace) {
                log.warn("[TRACE-{}] 未找到节点: nodeId={}", processId, nodeId);
            }
        } catch (Exception e) {
            if (isTrace) {
                log.error("[TRACE-{}] 解析 process_definition 失败: processId={}, nodeId={}",
                        processId, nodeId, e);
            } else {
                log.error("[FlowGraphBuilder] 解析 process_definition 失败: processId={}, nodeId={}",
                        processId, nodeId, e);
            }
        }
        return null;
    }
}
