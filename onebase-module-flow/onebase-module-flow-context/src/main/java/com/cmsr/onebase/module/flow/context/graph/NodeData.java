package com.cmsr.onebase.module.flow.context.graph;

import com.cmsr.onebase.module.flow.context.VariableConstants;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/29 12:02
 */
public class NodeData extends HashMap<String, Object> {
    public InLoopDepth getInLoopDepth() {
        InLoopDepth inLoopDepth = (InLoopDepth) this.get(VariableConstants.IN_LOOP_DEPTH);
        return inLoopDepth == null ? InLoopDepth.EMPTY_LOOP_DEPTH : inLoopDepth;
    }

    public String getString(String key) {
        return MapUtils.getString(this, key);
    }

    public Integer getInteger(String key, int defaultValue) {
        return MapUtils.getInteger(this, key, defaultValue);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return MapUtils.getBooleanValue(this, key, defaultValue);
    }

    public Long getLong(String key) {
        return MapUtils.getLong(this, key);
    }
}
