package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.event.FlowEventPublisherImpl;
import org.apache.rocketmq.client.apis.ClientException;
import org.junit.jupiter.api.Test;

/**
 * @Author：huangjie
 * @Date：2025/10/11 13:45
 */
class FlowEventPublisherImplTest {

    @Test
    void publishProcessUpdate() throws ClientException {
        FlowEventPublisherImpl rocketMQFlowEventPublisher = new FlowEventPublisherImpl();
        rocketMQFlowEventPublisher.setEndpoints("10.0.104.38:8081");
        rocketMQFlowEventPublisher.afterPropertiesSet();
        rocketMQFlowEventPublisher.publishProcessUpdate(85486565251907584L);
    }

    @Test
    void publishProcessDelete() {
    }

}