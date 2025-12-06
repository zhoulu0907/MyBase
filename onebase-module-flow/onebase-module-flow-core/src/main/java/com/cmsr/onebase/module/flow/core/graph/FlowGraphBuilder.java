package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.provider.FieldTypeProvider;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.nodes.ScriptNodeData;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorScriptRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorScriptDO;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/28 15:12
 */
@Component
public class FlowGraphBuilder {

    @Setter
    @Autowired
    private ObjectProvider<FieldTypeProvider> objectProvider;

    @Setter
    @Autowired
    private FlowConnectorScriptRepository connectorScriptRepository;

    public JsonGraph build(Long applicationId, String json) {
        JsonGraph jsonGraph = JsonUtils.parseObject(json, JsonGraph.class);
        addLoopContextToNodes(jsonGraph);
        enrichNodeData(jsonGraph);
        FieldTypeProvider fieldTypeProvider = objectProvider.getObject();
        fieldTypeProvider.completeFieldType(applicationId, jsonGraph);
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

    private void enrichNodeData(JsonGraph jsonGraph) {
        if (jsonGraph == null || jsonGraph.getNodes() == null) {
            return;
        }
        for (JsonGraphNode node : jsonGraph.getNodes()) {
            traverseNodeAndEnrichData(node);
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

    private void traverseNodeAndEnrichData(JsonGraphNode node) {
        if (node.getData() instanceof ScriptNodeData scriptNodeData) {
            FlowConnectorScriptDO connectorScriptDO = TenantManager.withoutTenantCondition(
                    //TODO 脚本要修改，表结构要修改
                    () -> connectorScriptRepository.findById(scriptNodeData.getActionId()));
            scriptNodeData.setScript(connectorScriptDO.getRawScript());
            scriptNodeData.setInputSchema(connectorScriptDO.getInputSchema());
            scriptNodeData.setOutputSchema(connectorScriptDO.getOutputSchema());
        }
        if (CollectionUtils.isNotEmpty(node.getBlocks())) {
            for (JsonGraphNode child : node.getBlocks()) {
                traverseNodeAndEnrichData(child);
            }
        }
    }
}
