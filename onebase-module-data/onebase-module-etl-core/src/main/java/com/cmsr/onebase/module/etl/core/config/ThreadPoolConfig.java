package com.cmsr.onebase.module.etl.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

// TODO: this class should be placed in infra/base

@Configuration
@ConditionalOnMissingBean(value = ThreadPoolTaskExecutor.class)
public class ThreadPoolConfig {

    @Value("${spring.task.execution.pool.core-size:10}")
    private Integer corePoolSize;

    @Value("${spring.task.execution.pool.max-size:30}")
    private Integer maxPoolSize;

    @Value("${spring.task.execution.pool.queue-capacity:10}")
    private Integer queueCapacity;

    @Value("${spring.task.execution.pool.keep-alive:60}")
    private Integer keepAliveSeconds;

    @Value("${spring.task.execution.pool.thread-name-prefix:onebase-executor-}")
    private String threadNamePrefix;

    @Bean(name = "threadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        // 允许核心线程超时退出
        executor.setAllowCoreThreadTimeOut(true);
        // 等待所有任务结束后再关闭线程池，最多等待10秒
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        // 线程不够时由调用线程进行执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}