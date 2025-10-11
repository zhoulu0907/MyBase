package com.cmsr.onebase.module.flow.build.graph;

import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/29 20:19
 */
public class GraphNodeDate {

    @Getter
    @Setter
    private List<GraphNodeDataConditions> filterCondition;

    @Getter
    @Setter
    private List<ConditionItem> fields;

    private Map<String, Object> otherProperties = new HashMap<>();

    @JsonAnySetter
    public void setOtherProperties(String key, Object value) {
        otherProperties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getOtherProperties() {
        return otherProperties;
    }

}
