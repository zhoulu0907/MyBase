package com.cmsr.onebase.module.flow.runtime.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Slf4j
@Component
public class TimeFlowProcessJob {

    @JobExecutor(name = "timeFlowProcessJob")
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        log.info("TimeFlowProcessJob execute, command: {}", jobArgs.getJobParams());
        return ExecuteResult.success("测试成功");
    }

}
