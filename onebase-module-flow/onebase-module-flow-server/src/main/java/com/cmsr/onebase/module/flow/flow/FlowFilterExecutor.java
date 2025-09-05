package com.cmsr.onebase.module.flow.flow;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/4 18:30
 */
@Component
public class FlowFilterExecutor {

    public boolean filter(Long processId, Map<String, Object> inputParams) {
        return true;
    }
}
