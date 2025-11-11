package com.cmsr.onebase.module.flow.context;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/9/5 16:12
 */
@ToString
public class ExecuteContext implements Serializable {

    @Setter
    @Getter
    private String traceId;

    @Setter
    @Getter
    private String executionUuid;


    @Setter
    @Getter
    private Long processId;

    @Setter
    @Getter
    private volatile boolean debugMode = false;

    /**
     * 节点配置数据
     */
    private volatile Map<String, NodeData> nodeDataMap = new HashMap<>();

    //节点执行的结果
    private Map<String, Object> nodeProcessHisResults = new ConcurrentHashMap<>();

    private Map<String, Object> nodeProcessCurResults = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private volatile boolean executeEnd = false;

    @Setter
    @Getter
    private volatile String executionEndNodeType;

    /**
     * 上次执行结束节点
     */
    @Setter
    @Getter
    private volatile String executionEndNodeTag;

    private volatile Stopwatch stopwatch;

    private volatile List<String> logs;

    public ExecuteContext() {
        this.stopwatch = Stopwatch.createStarted();
        this.logs = new CopyOnWriteArrayList<>();
        this.logs.add(String.format("[%d] %s", stopwatch.elapsed(TimeUnit.MILLISECONDS), "流程执行开始"));
    }

    public void setNodeDataMap(Map<String, NodeData> nodeData) {
        this.nodeDataMap = Collections.unmodifiableMap(nodeData);
    }

    public void resetNodeProcessResult() {
        nodeProcessHisResults.putAll(nodeProcessCurResults);
        this.nodeProcessCurResults.clear();
    }

    public void putNodeProcessResult(String tag, Object result) {
        this.nodeProcessCurResults.put(tag, result);
    }

    public boolean hasNodeProcessResult(String tag) {
        return nodeProcessHisResults.containsKey(tag);
    }

    public Object getNodeProcessResult(String tag) {
        return nodeProcessHisResults.get(tag);
    }

    public NodeData getNodeData(String nodeTag) {
        return nodeDataMap.get(nodeTag);
    }

    public void restExecutionEndNodeTag() {
        this.executionEndNodeTag = null;
    }

    public boolean isExecutionEndNodeTagEmpty() {
        return executionEndNodeTag == null;
    }

    public boolean isExecutionEndNodeTagEquals(String tag) {
        return executionEndNodeTag != null && executionEndNodeTag.equals(tag);
    }

    public void restExecutionUuid() {
        this.executionUuid = null;
    }

    public void addLog(String log) {
        logs.add(String.format("[%d] %s", stopwatch.elapsed(TimeUnit.MILLISECONDS), log));
    }

    public String getLogText() {
        return String.join("\n", logs);
    }
}
