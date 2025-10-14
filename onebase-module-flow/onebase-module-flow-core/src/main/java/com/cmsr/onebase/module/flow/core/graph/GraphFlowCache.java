package com.cmsr.onebase.module.flow.core.graph;


import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartEntityNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartFormNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:16
 */
@Component
@Conditional(FlowRuntimeCondition.class)
public class GraphFlowCache {

    private ConcurrentHashMap<Long, Map<String, NodeData>> flowNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartTimeNodeData> startTimeNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartFormNodeData> startFormNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartEntityNodeData> startEntityNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartDateFieldNodeData> startDateFieldNodeDataCache = new ConcurrentHashMap<>();

    public void update(Long processId, JsonGraph jsonGraph) {
        Map<String, NodeData> flowNodeData = jsonGraph.getNodeData();
        flowNodeDataCache.put(processId, flowNodeData);
        JsonGraphNode startNode = jsonGraph.getStartNode();
        if (startNode.getType().equalsIgnoreCase(JsonGraphConstant.START_TIME)) {
            StartTimeNodeData nodeData = (StartTimeNodeData) startNode.getData();
            startTimeNodeDataCache.put(processId, nodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_FORM)) {
            StartFormNodeData nodeData = (StartFormNodeData) startNode.getData();
            nodeData.setProcessId(processId);
            startFormNodeDataCache.put(processId, nodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_ENTITY)) {
            StartEntityNodeData nodeData = (StartEntityNodeData) startNode.getData();
            nodeData.setProcessId(processId);
            startEntityNodeDataCache.put(processId, nodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_DATE_FIELD)) {
            StartDateFieldNodeData nodeData = (StartDateFieldNodeData) startNode.getData();
            nodeData.setProcessId(processId);
            startDateFieldNodeDataCache.put(processId, nodeData);
        }
    }

    public void delete(Long processId) {
        flowNodeDataCache.remove(processId);
        startTimeNodeDataCache.remove(processId);
        startFormNodeDataCache.remove(processId);
        startDateFieldNodeDataCache.remove(processId);
        startEntityNodeDataCache.remove(processId);
    }

    public Map<String, NodeData> findNodeData(Long processId) {
        return flowNodeDataCache.get(processId);
    }

    public StartTimeNodeData findStartTimeNodeDataByProcessId(Long processId) {
        return startTimeNodeDataCache.get(processId);
    }

    public StartFormNodeData findStartFormNodeDataByProcessId(Long processId) {
        return startFormNodeDataCache.get(processId);
    }

    public StartDateFieldNodeData findStartDateFieldNodeDataByProcessId(Long processId) {
        return startDateFieldNodeDataCache.get(processId);
    }

    public List<StartEntityNodeData> findStartEntityNodeDataByEntityId(Long entityId) {
        return startEntityNodeDataCache.values().stream()
                .filter(startEntityNodeData -> startEntityNodeData.getEntityId().equals(entityId))
                .toList();
    }

    public List<StartFormNodeData> findStartFormNodeDataByPageId(Long pageId) {
        return startFormNodeDataCache.values().stream()
                .filter(startFormNodeData -> startFormNodeData.getPageId().equals(pageId))
                .toList();
    }

}
