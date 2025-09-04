package com.cmsr.onebase.module.flow.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Slf4j
@Component
public class TimeFlowProcessJob {

    @XxlJob("timeFlowProcessJob")
    public void execute() {
        String command = XxlJobHelper.getJobParam();
        log.info("TimeFlowProcessJob execute, command: {}", command);
    }
}
