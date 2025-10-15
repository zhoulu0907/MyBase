package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.job.DateFieldJobService;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
public class FlowDateFieldJobHandler implements DateFieldJobService, ApplicationRunner {


    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        RRemoteService remoteService = redissonClient.getRemoteService(DateFieldJobService.KEY_PREFIX_FLD);
        remoteService.register(DateFieldJobService.class, this, 12);
        log.info("注册DateFieldJobService成功: {}", DateFieldJobService.KEY_PREFIX_FLD);
    }

    @Override
    public String call(Long processId) {
        try {
            log.info("FlowDateFieldJobHandler call: {}", processId);
            ExecutorResult executorResult = flowProcessExecutor.execute(processId, Collections.emptyMap());
            log.error("执行流程结果：{}", executorResult);
            return "ok:" + JsonUtils.toJsonString(executorResult);
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            ExecutorResult executorResult = new ExecutorResult();
            executorResult.setSuccess(false);
            executorResult.setMessage(ExceptionUtils.getMessage(e));
            return "fail:" + JsonUtils.toJsonString(executorResult);
        }
    }

}
