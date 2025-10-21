package com.cmsr.onebase.module.flow.core.utils;

import java.time.Duration;

/**
 * @Author：huangjie
 * @Date：2025/9/4 16:38
 */
public class FlowUtils {

    public static final int MAX_QUERY_CALL_COUNT = 10;

    public static final Duration REDIS_TRACE_TIMEOUT = Duration.ofHours(1);

    public static String toFlowChainId(Long processId) {
        return "chain" + processId;
    }

    public static String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String toRedisTraceKey(String traceId) {
        return "flow:trace:" + traceId;
    }
}
