package com.cmsr.onebase.module.flow.core.graph;


import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartEntityNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartFormNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:16
 */
public class FlowProcessCache {

    private static ConcurrentHashMap<Long, FlowProcessDO> processCache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, Map<String, NodeData>> flowNodeDataCache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, StartTimeNodeData> startTimeNodeDataCache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, StartFormNodeData> startFormNodeDataCache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, StartEntityNodeData> startEntityNodeDataCache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, StartDateFieldNodeData> startDateFieldNodeDataCache = new ConcurrentHashMap<>();

    public static void update(FlowProcessDO processDO, JsonGraph jsonGraph) {
        Long processId = processDO.getId();
        Long applicationId = processDO.getApplicationId();
        //
        Map<String, NodeData> flowNodeData = jsonGraph.getNodeData();
        flowNodeDataCache.put(processId, flowNodeData);
        JsonGraphNode startNode = jsonGraph.getStartNode();
        if (startNode.getType().equalsIgnoreCase(JsonGraphConstant.START_TIME)) {
            StartTimeNodeData nodeData = (StartTimeNodeData) startNode.getData();
            nodeData.setApplicationId(applicationId);
            nodeData.setProcessId(processId);
            startTimeNodeDataCache.put(processId, nodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_FORM)) {
            StartFormNodeData nodeData = (StartFormNodeData) startNode.getData();
            nodeData.setApplicationId(applicationId);
            nodeData.setProcessId(processId);
            startFormNodeDataCache.put(processId, nodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_ENTITY)) {
            StartEntityNodeData nodeData = (StartEntityNodeData) startNode.getData();
            nodeData.setApplicationId(applicationId);
            nodeData.setProcessId(processId);
            startEntityNodeDataCache.put(processId, nodeData);
        } else if (startNode.getType().equals(JsonGraphConstant.START_DATE_FIELD)) {
            StartDateFieldNodeData nodeData = (StartDateFieldNodeData) startNode.getData();
            nodeData.setApplicationId(applicationId);
            nodeData.setProcessId(processId);
            startDateFieldNodeDataCache.put(processId, nodeData);
        }
        //几个大字段太占内存了也没用处，所以置空
        processDO.setProcessDefinition(null);
        processDO.setProcessDescription(null);
        processDO.setTriggerConfig(null);
        processCache.put(processId, processDO);
    }


    public static boolean isProcessExist(Long processId) {
        return flowNodeDataCache.containsKey(processId);
    }

    public static Map<String, NodeData> findNodeData(Long processId) {
        return flowNodeDataCache.get(processId);
    }

    public static StartTimeNodeData findStartTimeNodeDataByProcessId(Long processId) {
        return startTimeNodeDataCache.get(processId);
    }

    public static StartFormNodeData findStartFormNodeDataByProcessId(Long processId) {
        return startFormNodeDataCache.get(processId);
    }

    public static StartDateFieldNodeData findStartDateFieldNodeDataByProcessId(Long processId) {
        return startDateFieldNodeDataCache.get(processId);
    }

    public static List<StartEntityNodeData> findStartEntityNodeDataByEntityName(Long applicationId, String entityName) {
        return startEntityNodeDataCache.values().stream()
                .filter(startEntityNodeData ->
                        startEntityNodeData.getApplicationId().equals(applicationId)
                                && startEntityNodeData.getTableName().equals(entityName)
                )
                .toList();
    }

    public static List<StartFormNodeData> findStartFormNodeDataByPageId(Long applicationId, Long pageId) {
        return startFormNodeDataCache.values().stream()
                .filter(startFormNodeData ->
                        startFormNodeData.getApplicationId().equals(applicationId)
                                && startFormNodeData.getPageId().equals(pageId))
                .toList();
    }

    public static Set<Long> findProcessByApplicationId(Long applicationId) {
        return processCache.entrySet().stream()
                .filter(entry -> entry.getValue().equals(applicationId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static void deleteByProcessId(Long processId) {
        flowNodeDataCache.remove(processId);
        startTimeNodeDataCache.remove(processId);
        startFormNodeDataCache.remove(processId);
        startDateFieldNodeDataCache.remove(processId);
        startEntityNodeDataCache.remove(processId);
        processCache.remove(processId);
    }

    public static FlowProcessDO findProcessByProcessId(Long processId) {
        return processCache.get(processId);
    }

}
