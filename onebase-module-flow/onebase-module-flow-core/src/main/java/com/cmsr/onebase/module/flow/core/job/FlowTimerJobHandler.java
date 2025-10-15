package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.job.TimerJobService;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRemoteService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowTimerJobHandler implements TimerJobService, ApplicationRunner {

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RRemoteService remoteService = redissonClient.getRemoteService(TimerJobService.KEY_PREFIX_TIMER);
        remoteService.register(TimerJobService.class, this, 12);
    }

    @Override
    public String call(Long processId) {
        try {
            log.info("FlowTimerJobHandler call: {}", processId);
            ExecutorResult executorResult = flowProcessExecutor.execute(processId, Collections.emptyMap());
            log.error("执行流程结果：{}", executorResult);
            return "ok:" + JsonUtils.toJsonString(executorResult);
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            ExecutorResult executorResult = new ExecutorResult();
            executorResult.setSuccess(false);
            executorResult.setMessage(e.getMessage());
            executorResult.setCause(e);
            return "fail:" + JsonUtils.toJsonString(executorResult);
        }
    }

}
