package com.cmsr.onebase.module.flow.context.job;

/**
 * @Author：huangjie
 * @Date：2025/10/15 9:56
 */
public interface TimerJobService {

    String KEY_PREFIX_TIMER = "flow:job:service:timer";

    String call(Long processId);

}
