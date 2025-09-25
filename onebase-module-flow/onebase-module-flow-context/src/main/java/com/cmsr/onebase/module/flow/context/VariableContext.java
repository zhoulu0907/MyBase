package com.cmsr.onebase.module.flow.context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/23 15:54
 */
public class VariableContext {

    private Map<String, Object> inputParams;

    private Map<String, Object> nodeVariables = new ConcurrentHashMap<>();

    private Map<String, Object> outputParams;

    public void setInputParams(Map<String, Object> inputParams) {
        this.inputParams = inputParams;
    }

    public Map<String, Object> getInputParams() {
        return this.inputParams;
    }

    public Map<String, Object> getOutputParams() {
        return this.outputParams;
    }

    public void putNodeVariables(String tag, Map<String, Object> data) {
        this.nodeVariables.put(tag, data);
    }

    public void putNodeVariables(String tag, List<Map<String, Object>> data) {
        this.nodeVariables.put(tag, data);
    }

    public void putInputVariables(String tag) {
        if (inputParams != null) {
            nodeVariables.put(tag, inputParams);
        }
    }


}
