package com.cmsr.onebase.module.flow.core.graph;


import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:16
 */
@Component
public class GraphFlowCache {

    private ConcurrentHashMap<Long, Map<String, Map<String, Object>>> flowNodeDataCache = new ConcurrentHashMap<>();

    public void update(FlowProcessDO flowProcessDO, JsonGraph jsonGraph) {
        Map<String, Map<String, Object>> nodeDataMap = jsonGraph.getNodes().stream()
                .collect(Collectors.toMap(JsonGraphNode::getId, JsonGraphNode::getData));
        flowNodeDataCache.put(flowProcessDO.getId(), nodeDataMap);
    }

    public void delete(Long processId) {
        flowNodeDataCache.remove(processId);
    }

    public Map<String, Object> getNodeData(Long processId, String nodeId) {
        Map<String, Map<String, Object>> flowNodeData = flowNodeDataCache.get(processId);
        return flowNodeData.get(nodeId);
    }

}
