package com.cmsr.onebase.module.flow.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author：huangjie
 * @Date：2025/11/1 19:05
 */
@Configuration
public class FlowConfig {

    //TODO 这个要全局配置
    @ConditionalOnMissingBean(TaskScheduler.class)
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskExecutor = new ThreadPoolTaskScheduler();
        taskExecutor.setPoolSize(10);
        return taskExecutor;
    }

}
