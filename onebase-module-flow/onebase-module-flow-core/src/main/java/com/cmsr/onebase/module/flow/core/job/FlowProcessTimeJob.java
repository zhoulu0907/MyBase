package com.cmsr.onebase.module.flow.core.job;

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
public class FlowProcessTimeJob {

    @JobExecutor(name = "flow_process_time_job")
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        log.info("FlowProcessTimeJob execute, command: {}", jobArgs.getJobParams());
        return ExecuteResult.success("测试成功");
    }

}
