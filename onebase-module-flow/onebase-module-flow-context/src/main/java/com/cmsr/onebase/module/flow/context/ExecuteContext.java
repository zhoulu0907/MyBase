package com.cmsr.onebase.module.flow.context;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/5 16:12
 */
@Data
public class ExecuteContext implements Serializable {

    private String traceId;

    /**
     * 执行唯一标识，二次触发执行需要
     */

    private String executionUuid;


    private Long applicationId;


    private Long versionTag;


    private Long processId;


    /**
     * 触发用户ID
     * 界面触发：登录用户
     * 后台触发： 流程的创建人
     */
    private Long triggerUserId;

    /**
     * 触发用户部门ID
     * 界面触发：登录用户
     * 后台触发： 创建人部门
     */
    private Long triggerUserDeptId;

    /**
     * 元数据接口调用传递过来的，也原样传递回去
     * SystemFieldConstants
     */
    private Map<String, String> systemFields;


    private volatile boolean debugMode = false;

    /**
     * 节点执行的结果
     */
    private Map<String, Object> nodeProcessResults = new ConcurrentHashMap<>();

    /**
     * 是否执行结束
     */
    private volatile boolean executeEnd = false;


    /**
     * 节点配置数据
     */
    private transient volatile Map<String, NodeData> nodeDataMap = new HashMap<>();

    /**
     * 是否异常终止
     */
    private transient volatile Boolean abnormalTermination = Boolean.FALSE;

    /**
     * 异常终止的错误信息
     */

    private transient volatile String terminationMessage;


    private transient ExecuteLog executeLog;


    public ExecuteContext() {
        this.executeLog = new ExecuteLog();
        this.executeLog.addLog("流程执行开始");
    }

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

    public void setNodeProcessResult(String tag, Object result) {
        this.nodeProcessResults.put(tag, result);
    }

    public NodeData getNodeData(String nodeTag) {
        return nodeDataMap.get(nodeTag);
    }

    public void addLog(String log) {
        executeLog.addLog(log);
    }

    public String getLogText() {
        return String.join("\n", executeLog.getLogs());
    }
}
