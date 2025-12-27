package com.cmsr.onebase.module.flow.context;

import com.cmsr.onebase.module.flow.context.table.RowData;
import com.cmsr.onebase.module.flow.context.table.TableData;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/23 15:54
 */
public class VariableContext implements Serializable {

    private RowData inputParams = new RowData();

    private Map<String, Object> nodeVariables = new ConcurrentHashMap<>();

    private Map<String, Object> outputParams = Collections.emptyMap();

    public RowData getInputParams() {
        return inputParams;
    }

    public void setInputParams(RowData inputParams) {
        if (inputParams != null) {
            this.inputParams = inputParams;
        }
    }

    public void setOutputParams(Map<String, Object> outputParams) {
        if (outputParams != null) {
            this.outputParams = outputParams;
        }
    }

    public Map<String, Object> getOutputParams() {
        return Collections.unmodifiableMap(this.outputParams);
    }

    public Map<String, Object> getNodeVariables() {
        return Collections.unmodifiableMap(this.nodeVariables);
    }

    public void putNodeVariables(String tag, Map<String, Object> data) {
        this.nodeVariables.put(tag, data);
    }

    public void putNodeVariables(String tag, List<Map<String, Object>> data) {
        this.nodeVariables.put(tag, data);
    }

    public void putInputVariables(String tag) {
        nodeVariables.put(tag, inputParams);
    }

    public List<Map<String, Object>> getListVariableByTag(String tag) {
        Object value = nodeVariables.get(tag);
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof List list) {
            return list;
        }
        throw new RuntimeException("变量" + tag + "不是List类型: " + value.getClass().getName());
    }
}
