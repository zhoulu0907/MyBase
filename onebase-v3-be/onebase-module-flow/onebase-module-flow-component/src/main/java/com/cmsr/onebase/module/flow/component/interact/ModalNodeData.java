package com.cmsr.onebase.module.flow.component.interact;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:07
 */
@NodeType("modal")
public class ModalNodeData extends NodeData implements Serializable {

    private Map<String, Object> otherParams = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOtherParams() {
        return otherParams;
    }

    @JsonAnySetter
    public void setOtherParams(String key, Object value) {
        this.otherParams.put(key, value);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nodeType", "modal");
        map.putAll(otherParams);
        return map;
    }
}
