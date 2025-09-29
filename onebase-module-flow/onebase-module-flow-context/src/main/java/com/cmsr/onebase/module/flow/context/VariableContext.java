package com.cmsr.onebase.module.flow.context;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;

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

    public Map<String, Object> getInputParams() {
        return Collections.unmodifiableMap(this.inputParams);
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

    /**
     * 通过表达式获取变量值，表达式格式为 tag.key，列如： dataQuery_cCui4tl6zRVBwOmxTNr1G.46999569445519360
     * 数据格式为 Map<String, List<Map<String,Object>>>
     *
     * @param index      列表索引
     * @param expression 表达式字符串
     * @return 变量值
     */
    public Object getVariableByExpression(int index, String expression) {
        if (expression == null) {
            return null;
        }

        String[] split = StringUtils.split(expression, ".");
        if (split == null || split.length != 2) {
            throw new IllegalArgumentException("表达式格式错误：" + expression + "，正确格式应为: tag.key");
        }

        String tag = split[0];
        String key = split[1];

        // 获取标签对应的值
        Object value = nodeVariables.get(tag);
        if (value == null) {
            return null;
        }

        // 检查是否为列表类型
        if (!(value instanceof List)) {
            throw new IllegalStateException("变量 " + tag + " 不是List类型，实际类型: " + value.getClass().getName());
        }

        List<Object> list = (List<Object>) value;

        // 检查索引是否越界
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("索引 " + index + " 超出列表 " + tag + " 的范围 [0, " + (list.size() - 1) + "]");
        }

        // 获取列表指定位置的元素
        Object listItem = list.get(index);
        if (listItem == null) {
            return null;
        }

        // 检查元素是否为Map类型
        if (!(listItem instanceof Map)) {
            throw new IllegalStateException("列表 " + tag + " 索引 " + index + " 处的元素不是Map类型，实际类型: " + listItem.getClass().getName());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> mapItem = (Map<String, Object>) listItem;

        return mapItem.get(key);
    }

    public int getVariableSizeByTag(String tag) {
        Object value = nodeVariables.get(tag);
        if (value == null) {
            return 0;
        }
        if (value instanceof List) {
            return ListUtils.emptyIfNull((List<Map<String, Object>>) value).size();
        }
        throw new RuntimeException("变量" + tag + "不是List类型: " + value.getClass().getName());
    }


}
