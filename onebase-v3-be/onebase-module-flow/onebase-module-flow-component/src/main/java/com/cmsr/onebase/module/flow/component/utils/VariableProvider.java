package com.cmsr.onebase.module.flow.component.utils;

import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.yomahub.liteflow.core.NodeComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/10/22 12:10
 */
public class VariableProvider {

    /**
     * 处理完循环下标后，当前变量的值Map。
     *
     * @param nodeComponent
     * @param inLoopDepth
     * @param nodeVariables
     * @return
     */
    public static Map<String, Object> resolveLoopVariables(NodeComponent nodeComponent, InLoopDepth inLoopDepth, Map<String, Object> nodeVariables) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> loopIndexMap = loopIndexMap(nodeComponent, inLoopDepth);
        for (Map.Entry<String, Object> entry : nodeVariables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!loopIndexMap.containsKey(key)) {
                result.put(key, value);
            } else {
                Integer index = loopIndexMap.get(key);
                if (value instanceof List list) {
                    Object vo = list.get(index);
                    result.put(key, vo);
                } else {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    /**
     * 返回的key是节点id，value是循环的下标值
     *
     * @param nodeComponent
     * @param inLoopDepth
     * @return
     */
    private static Map<String, Integer> loopIndexMap(NodeComponent nodeComponent, InLoopDepth inLoopDepth) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : inLoopDepth.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Integer index = nodeComponent.getPreNLoopIndex(value);
            result.put(key, index);
        }
        return result;
    }
}
