package com.cmsr.onebase.module.flow.core.graph;


import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.core.enums.JsonGraphConstant;
import com.cmsr.onebase.module.flow.core.graph.data.StartFormNodeData;
import com.cmsr.onebase.module.flow.core.graph.data.StartTimeNodeData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:16
 */
@Component
public class GraphFlowCache {

    private ConcurrentHashMap<Long, Map<String, Map<String, Object>>> flowNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartTimeNodeData> startTimeNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartFormNodeData> startFormNodeDataCache = new ConcurrentHashMap<>();

    public void update(Long processId, JsonGraph jsonGraph) {
        Map<String, Map<String, Object>> flowNodeData = jsonGraph.getNodeData();
        flowNodeDataCache.put(processId, flowNodeData);
        JsonGraphNode startNode = jsonGraph.getStartNode();
        if (startNode.getType().equalsIgnoreCase(JsonGraphConstant.START_TIME)) {
            StartTimeNodeData startTimeNodeData = new StartTimeNodeData(startNode.getData());
            startTimeNodeDataCache.put(processId, startTimeNodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_FORM)) {
            StartFormNodeData startFormNodeData = new StartFormNodeData();
            JsonUtils.updateBean(startFormNodeData, startNode.getData());
            startFormNodeDataCache.put(processId, startFormNodeData);
        }
    }

    public void delete(Long processId) {
        flowNodeDataCache.remove(processId);
        startFormNodeDataCache.remove(processId);
    }

    public Map<String, Object> getNodeData(Long processId, String nodeId) {
        Map<String, Map<String, Object>> flowNodeData = flowNodeDataCache.get(processId);
        return flowNodeData.get(nodeId);
    }

    public StartTimeNodeData getStartTimeNodeData(Long processId) {
        return startTimeNodeDataCache.get(processId);
    }

    public StartFormNodeData getStartFormNodeData(Long processId) {
        return startFormNodeDataCache.get(processId);
    }

}
