package com.cmsr.onebase.module.flow.core.event.rocketmq;

import org.apache.rocketmq.client.apis.ClientException;
import org.junit.jupiter.api.Test;

/**
 * @Author：huangjie
 * @Date：2025/10/11 13:45
 */
class RocketMQFlowEventPublisherTest {

    @Test
    void publishProcessUpdate() throws ClientException {
        RocketMQFlowEventPublisher rocketMQFlowEventPublisher = new RocketMQFlowEventPublisher();
        rocketMQFlowEventPublisher.setEndpoints("10.0.104.38:8081");
        rocketMQFlowEventPublisher.afterPropertiesSet();
        rocketMQFlowEventPublisher.publishProcessUpdate(85486565251907584L);
    }

    @Test
    void publishProcessDelete() {
    }

}