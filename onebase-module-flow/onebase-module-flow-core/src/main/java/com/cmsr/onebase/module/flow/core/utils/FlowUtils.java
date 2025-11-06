package com.cmsr.onebase.module.flow.core.utils;

import org.redisson.codec.Kryo5Codec;

import java.time.Duration;

/**
 * @Author：huangjie
 * @Date：2025/9/4 16:38
 */
public class FlowUtils {

    public static Kryo5Codec KRYO5_CODEC = new Kryo5Codec();

    public static final String REDIS_VERSION_CHANGE_CACHE_KEY = "flow:version:change:cache";

    public static final String REDIS_VERSION_CHANGE_TOPIC_KEY = "flow:version:change:topic";

    public static final int VERSION_TIMEOUT_MINUTES = 30;

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
