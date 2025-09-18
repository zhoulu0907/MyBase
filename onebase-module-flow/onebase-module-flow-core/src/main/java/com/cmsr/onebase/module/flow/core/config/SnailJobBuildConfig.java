package com.cmsr.onebase.module.flow.core.config;


import com.aizuda.snailjob.client.common.config.SnailJobProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;



/**
 * @Author：huangjie
 * @Date：2025/9/5 13:40
 */
@Slf4j
@Configuration
@Conditional(FlowBuildCondition.class)
@EnableConfigurationProperties({SnailJobProperties.class})
public class SnailJobBuildConfig {


}
