package com.cmsr.onebase.module.flow.context;

import org.mvel2.MVEL;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/23 15:54
 */
public class VariableContext {

    private Map<String, Object> inputParams = Collections.emptyMap();

    private Map<String, Object> nodeVariables = new ConcurrentHashMap<>();

    private Map<String, Object> outputParams = Collections.emptyMap();

    public void setInputParams(Map<String, Object> inputParams) {
        if (inputParams != null) {
            this.inputParams = inputParams;
        }
    }

    public Map<String, Object> getInputParams() {
        return Collections.unmodifiableMap(this.inputParams);
    }

    public Map<String, Object> getOutputParams() {
        return Collections.unmodifiableMap(this.outputParams);
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

    public Object getVariableByExpression(String expression) {
        return MVEL.eval(expression, nodeVariables);
    }

}
