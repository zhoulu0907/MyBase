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
 * 节点分一般和可恢复两者。
 * <p>
 * <p>
 * 恢复执行算法：
 * <p>
 * context里面包含下面两个变量
 * previousNodeTag     恢复执行节点ID
 * ----------------------------------------------------------------------------
 * 初始执行逻辑：
 * previousNodeTag = null
 * 一般节点 判断 previousNodeTag 为 null。正常执行该节点（isAccess返回true）
 * 可恢复节点 方法判断 previousNodeTag 为 null，isAccess返回true，正常执行该节点。（isAccess返回true，afterProcess。1执行该节点，2缓存执行状态，3setIsEnd为 true，4返回结果）
 * <p>
 * <p>
 * <p>
 * ----------------------------------------------------------------------------
 * 恢复执行逻辑：
 * previousNodeTag = 恢复执行节点ID
 * <p>
 * 恢复节点前的节点：
 * 节点 判断 previousNodeTag不为 null 且 previousNodeTag 为 不是自己的ID。跳过该节点执行（isAccess 返回false）。
 * <p>
 * 恢复节点：
 * 恢复节点 方法判断 previousNodeTag 为 自己的ID，isAccess返回false。跳过执行且重置状态。（在isAccess执行该节点，previousNodeTag = null）
 * <p>
 * 恢复节点后的节点：
 * 同“初始执行逻辑”
 * <p>
 * <p>
 * 总结：
 * 一般节点分：正常执行，跳过执行。
 * 可恢复节点分：正常执行，跳过执行，跳过且重置状态。
 *
 * @Author：huangjie
 * @Date：2025/9/5 16:12
 */
@ToString
public class ExecuteContext implements Serializable {

    @Setter
    private Long processId;

    // 节点配置数据
    private Map<String, NodeData> nodeDataMap = new HashMap<>();

    //节点执行的结果
    private Map<String, Object> nodeProcessResults = new ConcurrentHashMap<>();

    private boolean executeEnd = false;

    // 上次执行结束节点
    @Setter
    @Getter
    private String executionEndTag;

    @Setter
    @Getter
    private String executionUuid;

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

    public void setExecuteEnd(boolean executeEnd) {
        this.executeEnd = executeEnd;
    }

    public boolean isExecuteEnd() {
        return executeEnd;
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
