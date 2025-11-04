package com.cmsr.onebase.module.flow.core.utils;

import java.time.Duration;

/**
 * @Author：huangjie
 * @Date：2025/9/4 16:38
 */
public class FlowUtils {

    public static final String REDIS_VERSION_CACHE_KEY = "flow:version:cache";

    public static final String REDIS_VERSION_CHANGE_TOPIC_KEY = "flow:version:change:topic";

    public static final int VERSION_TIMEOUT_HOUR = 12;

    public static final int MAX_QUERY_CALL_COUNT = 10;

    public static final Duration REDIS_TRACE_TIMEOUT = Duration.ofHours(4);

    public static String toFlowChainId(Long processId) {
        return "chain" + processId;
    }

    public static String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String toRedisTraceKey(String traceId) {
        return "flow:trace:" + traceId;
    }

    public static String toRedisProcessLockKey(Long processId) {
        return "flow:process:lock:" + processId;
    }
}
