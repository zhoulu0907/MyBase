package com.cmsr.onebase.framework.common.util.monitor;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import java.util.UUID;

/**
 * 链路追踪工具类
 *
 * 考虑到每个 starter 都需要用到该工具类，所以放到 common 模块下的 util 包下
 *
 */
public class TracerUtils {

    /**
     * 私有化构造方法
     */
    private TracerUtils() {
    }

    /**
     * 获得链路追踪编号，直接返回 SkyWalking 的 TraceId。
     * 如果不存在的话生成一个 UUID 作为备用 trace_id
     *
     * @return 链路追踪编号
     */
    public static String getTraceId() {
        String traceId = TraceContext.traceId();
        // 如果 SkyWalking Agent 未启动，TraceContext.traceId() 会返回空字符串
        // 此时我们生成一个 UUID 作为备用的 trace_id，确保日志记录的完整性
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        return traceId;
    }

}
