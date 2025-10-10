package com.cmsr.onebase.module.flow.core.config;


import com.cmsr.onebase.module.flow.core.event.FlowEventPublisher;
import com.cmsr.onebase.module.flow.core.event.rocketmq.RocketMQFlowEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @Author：huangjie
 * @Date：2025/9/5 13:40
 */
@Slf4j
@Configuration
@Conditional(FlowBuildCondition.class)
public class FlowEventBuildConfig {

    @Value("${rocketmq.endpoints}")
    private String rocketMQEndpoints;


    @Bean
    public FlowEventPublisher flowEventPublisher() throws Exception {
        log.info("初始化流程事件发送器");
        RocketMQFlowEventPublisher rocketMQFlowEventPublisher = new RocketMQFlowEventPublisher();
        rocketMQFlowEventPublisher.setEndpoints(rocketMQEndpoints);
        rocketMQFlowEventPublisher.init();
        return rocketMQFlowEventPublisher;
    }

}
