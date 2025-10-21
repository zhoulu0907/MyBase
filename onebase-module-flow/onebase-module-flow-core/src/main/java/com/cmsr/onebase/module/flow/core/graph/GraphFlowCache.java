package com.cmsr.onebase.module.flow.core.graph;


import com.cmsr.onebase.module.flow.context.FieldTypeProvider;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphConstant;
import com.cmsr.onebase.module.flow.context.graph.JsonGraphNode;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartEntityNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartFormNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import lombok.Setter;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:16
 */
@Component
@Conditional(FlowRuntimeCondition.class)
public class GraphFlowCache {


    private HashSetValuedHashMap<Long, Long> applicationFlowCache = new HashSetValuedHashMap<>();

    private final ReadWriteLock applicationFlowCacheLock = new ReentrantReadWriteLock();

    private ConcurrentHashMap<Long, Map<String, NodeData>> flowNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartTimeNodeData> startTimeNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartFormNodeData> startFormNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartEntityNodeData> startEntityNodeDataCache = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, StartDateFieldNodeData> startDateFieldNodeDataCache = new ConcurrentHashMap<>();

    @Setter
    @Autowired
    private ObjectProvider<FieldTypeProvider> objectProvider;

    public void update(Long applicationId, Long processId, JsonGraph jsonGraph) {
        FieldTypeProvider fieldTypeProvider = objectProvider.getObject();
        fieldTypeProvider.completeFieldType(jsonGraph);
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
        applicationFlowCacheLock.writeLock().lock();
        try {
            applicationFlowCache.put(applicationId, processId);
        } finally {
            applicationFlowCacheLock.writeLock().unlock();
        }
    }

    public void delete(Long applicationId, Long processId) {
        flowNodeDataCache.remove(processId);
        startTimeNodeDataCache.remove(processId);
        startFormNodeDataCache.remove(processId);
        startDateFieldNodeDataCache.remove(processId);
        startEntityNodeDataCache.remove(processId);
        //删除这个要加锁
        applicationFlowCacheLock.writeLock().lock();
        try {
            applicationFlowCache.removeMapping(applicationId, processId);
        } finally {
            applicationFlowCacheLock.writeLock().unlock();
        }
    }

    public boolean isProcessExist(Long processId) {
        return flowNodeDataCache.containsKey(processId);
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

    public Set<Long> findFlowByApplicationId(Long applicationId) {
        applicationFlowCacheLock.readLock().lock();
        try {
            Set<Long> ids = applicationFlowCache.get(applicationId);
            Set<Long> result = new HashSet<>();
            result.addAll(ids);
            return result;
        } finally {
            applicationFlowCacheLock.readLock().unlock();
        }
    }

    public void delete(Long applicationId) {
        Set<Long> flowIds = findFlowByApplicationId(applicationId);
        for (Long flowId : flowIds) {
            delete(applicationId, flowId);
        }
    }
}
