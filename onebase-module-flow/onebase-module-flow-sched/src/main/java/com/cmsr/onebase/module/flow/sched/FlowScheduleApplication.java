package com.cmsr.onebase.module.flow.sched;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 代理工具类，开发任务执行给DolphinScheduler调用
 * @author huangjie
 * @since 2025/10/14
 */
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase", "org.anyline"})
public class FlowScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowScheduleApplication.class, args);
    }

}