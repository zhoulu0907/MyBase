package com.cmsr.onebase.framework.dolphins.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * DolphinScheduler 自动配置类
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Configuration
@EnableConfigurationProperties(DolphinSchedulerProperties.class)
@ComponentScan(basePackages = "com.cmsr.onebase.framework.dolphins")
public class DolphinSchedulerAutoConfiguration {

}
