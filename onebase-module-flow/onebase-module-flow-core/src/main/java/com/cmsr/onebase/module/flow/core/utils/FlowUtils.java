package com.cmsr.onebase.module.flow.core.utils;

/**
 * @Author：huangjie
 * @Date：2025/9/4 16:38
 */
public class FlowUtils {

    public static final String INPUT = "input";

    public static String toFlowChainId(Long processId) {
        return "chain" + processId;
    }
}
