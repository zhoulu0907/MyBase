package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.FieldTypeProvider;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
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

    public JsonGraph build(String json) {
        JsonGraph jsonGraph = JsonUtils.parseObject(json, JsonGraph.class);
        addJsonGraphLoopVariable(jsonGraph);
        FieldTypeProvider fieldTypeProvider = objectProvider.getObject();
        fieldTypeProvider.completeFieldType(jsonGraph);
        return jsonGraph;
    }

    private static void addJsonGraphLoopVariable(JsonGraph jsonGraph) {
        if (jsonGraph == null || jsonGraph.getNodes() == null) {
            return;
        }
        for (JsonGraphNode node : jsonGraph.getNodes()) {
            recursiveNode(node, new InLoopDepth());
        }
    }

    private static void recursiveNode(JsonGraphNode node, InLoopDepth loopDeepMap) {
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
                recursiveNode(childNode, loopDeepMap);
            }
        }
    }
}
