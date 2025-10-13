package com.cmsr.onebase.module.flow.context;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/5 16:12
 */
@ToString
public class ExecuteContext implements Serializable {

    @Setter
    private Long processId;

    @Setter
    @Getter
    private volatile boolean debugMode = false;

    // 节点配置数据
    private Map<String, NodeData> nodeDataMap = new HashMap<>();

    //节点执行的结果
    private Map<String, Object> nodeProcessResults = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private volatile boolean executeEnd = false;

    // 上次执行结束节点
    @Setter
    @Getter
    private volatile String executionEndTag;

    @Setter
    @Getter
    private volatile String executionUuid;

    public void setNodeDataMap(Map<String, NodeData> nodeData) {
        this.nodeDataMap = Collections.unmodifiableMap(nodeData);
    }

    public void putNodeProcessResult(String tag, Object result) {
        this.nodeProcessResults.put(tag, result);
    }

    public boolean hasNodeProcessResult(String tag) {
        return nodeProcessResults.containsKey(tag);
    }

    public Object getNodeProcessResult(String tag) {
        return nodeProcessResults.get(tag);
    }

    public NodeData getNodeData(String nodeTag) {
        return nodeDataMap.get(nodeTag);
    }

    public void restExecutionEndTag() {
        this.executionEndTag = null;
    }

    public boolean isExecutionEndTagEmpty() {
        return executionEndTag == null;
    }

    public boolean executionEndTagEquals(String tag) {
        return executionEndTag != null && executionEndTag.equals(tag);
    }

    public void restExecutionUuid() {
        this.executionUuid = null;
    }
}
