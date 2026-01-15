package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.nodes.ScriptNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.CommonNodeData;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorScriptRepository;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorMapper;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowNodeConfigMapper;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorScriptDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowNodeConfigDO;
import com.cmsr.onebase.module.flow.core.external.FlowFieldTypeProvider;
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
 * @Date：2025/9/28 15:12
 */
@Component
public class FlowGraphBuilder {

    @Setter
    @Autowired
    private FlowFieldTypeProvider flowFieldTypeProvider;

    @Setter
    @Autowired
    private FlowConnectorScriptRepository connectorScriptRepository;

    @Setter
    @Autowired
    private FlowConnectorMapper flowConnectorMapper;

    @Setter
    @Autowired
    private FlowNodeConfigMapper flowNodeConfigMapper;


    public JsonGraph build(Long applicationId, String json) {
        JsonGraph jsonGraph = JsonUtils.parseObject(json, JsonGraph.class);
        addLoopContextToNodes(jsonGraph);
        enrichNodeData(applicationId, jsonGraph);
        flowFieldTypeProvider.completeFieldType(applicationId, jsonGraph);
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

    private void enrichNodeData(Long applicationId, JsonGraph jsonGraph) {
        if (jsonGraph == null || jsonGraph.getNodes() == null) {
            return;
        }
        for (JsonGraphNode node : jsonGraph.getNodes()) {
            traverseNodeAndEnrichData(applicationId, node);
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

    private void traverseNodeAndEnrichData(Long applicationId, JsonGraphNode node) {
        if (node.getData() instanceof ScriptNodeData scriptNodeData) {
            FlowConnectorScriptDO connectorScriptDO = TenantManager.withoutTenantCondition(() -> connectorScriptRepository.findByApplicationAndUuid(applicationId, scriptNodeData.getActionId(), scriptNodeData.getActionUuid()));
            scriptNodeData.setScript(connectorScriptDO.getRawScript());
            scriptNodeData.setInputSchema(connectorScriptDO.getInputSchema());
            scriptNodeData.setOutputSchema(connectorScriptDO.getOutputSchema());
        }
        if (node.getData() instanceof CommonNodeData commonNodeData) {
            // 加载连接器配置
            FlowConnectorDO connectorDO = TenantManager.withoutTenantCondition(() ->
                flowConnectorMapper.selectByApplicationAndCode(applicationId, commonNodeData.getConnectorCode()));
            if (connectorDO != null) {
                Map<String, Object> connectorConfig = new HashMap<>();
                if (StringUtils.isNotBlank(connectorDO.getConfigJson())) {
                    connectorConfig = JsonUtils.parseObject(connectorDO.getConfigJson(), Map.class);
                }
                commonNodeData.setConnectorConfig(connectorConfig);
            }
            
            // 加载节点配置
            FlowNodeConfigDO nodeConfigDO = TenantManager.withoutTenantCondition(() ->
                flowNodeConfigMapper.selectByApplicationAndCode(applicationId, commonNodeData.getNodeCode()));
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
        }
        if (CollectionUtils.isNotEmpty(node.getBlocks())) {
            for (JsonGraphNode child : node.getBlocks()) {
                traverseNodeAndEnrichData(applicationId, child);
            }
        }
    }
}
