package com.cmsr.onebase.module.flow.context;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/23 15:54
 */
public class VariableContext {

    private static JexlEngine jexl = new JexlBuilder().create();

    private Map<String, Object> inputParams = Collections.emptyMap();

    private Map<String, Object> nodeVariables = new ConcurrentHashMap<>();

    private Map<String, Object> outputParams = Collections.emptyMap();

    public void setInputParams(Map<String, Object> inputParams) {
        if (inputParams != null) {
            this.inputParams = inputParams;
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

    /**
     * 通过表达式获取变量值，表达式格式为 tag.key，列如： dataQuery_cCui4tl6zRVBwOmxTNr1G.46999569445519360
     * 数据格式为 Map<String, Map<String,Object>>
     *
     * @param expression 表达式字符串
     * @return 变量值
     */
    public Object getVariableByExpression(String expression) {
        if (expression == null) {
            return null;
        }
        JexlExpression exp = jexl.createExpression(expression);
        MapContext jc = new MapContext(nodeVariables);
        return exp.evaluate(jc);
    }

    public int getVariableSizeByTag(String tag) {
        Object value = nodeVariables.get(tag);
        if (value == null) {
            return 0;
        }
        if (value instanceof List list) {
            return list.size();
        }
        throw new RuntimeException("变量" + tag + "不是List类型: " + value.getClass().getName());
    }


}
