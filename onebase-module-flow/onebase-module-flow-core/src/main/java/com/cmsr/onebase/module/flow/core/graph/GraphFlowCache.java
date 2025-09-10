package com.cmsr.onebase.module.flow.core.graph;


import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:16
 */
@Component
public class GraphFlowCache {


    private ConcurrentHashMap<Long, String> formFilterCondition = new ConcurrentHashMap<>();

    public void putFormFilterCondition(Long processId, String condition) {
        formFilterCondition.put(processId, condition);
    }

    public String getFormFilterCondition(Long processId) {
        return formFilterCondition.get(processId);
    }

}
