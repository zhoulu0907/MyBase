package com.cmsr.onebase.module.flow.core.config;


import com.cmsr.onebase.module.flow.core.event.FlowEventHandler;
import com.cmsr.onebase.module.flow.core.event.rocketmq.RocketMQFlowEventHandler;
import com.cmsr.onebase.module.flow.core.event.rocketmq.RocketMQSlotManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
@Conditional(FlowRuntimeCondition.class)
public class FlowEventRuntimeConfig {

    @Value("${rocketmq.endpoints}")
    private String rocketMQEndpoints;

    @Autowired
    private RedissonClient redissonClient;

    @Bean
    public RocketMQSlotManager rocketMQSlotManager() {
        log.info("初始化流程事件槽管理器");
        RocketMQSlotManager rocketMQSlotManager = new RocketMQSlotManager();
        rocketMQSlotManager.setRedissonClient(redissonClient);
        rocketMQSlotManager.init();
        return rocketMQSlotManager;
    }

    @Bean
    public FlowEventHandler flowEventHandler(RocketMQSlotManager rocketMQSlotManager) throws Exception {
        log.info("初始化流程事件处理器");
        RocketMQFlowEventHandler rocketMQFlowEventHandler = new RocketMQFlowEventHandler();
        rocketMQFlowEventHandler.setEndpoints(rocketMQEndpoints);
        rocketMQFlowEventHandler.setProcessId(rocketMQSlotManager.getSlot());
        rocketMQFlowEventHandler.init();
        return rocketMQFlowEventHandler;
    }

}
