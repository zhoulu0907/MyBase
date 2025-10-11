package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.config.FlowBuildCondition;
import com.cmsr.onebase.module.flow.core.event.FlowEvent;
import com.cmsr.onebase.module.flow.core.event.FlowEventPublisher;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:18
 */
@Slf4j
@Component
public class RocketMQFlowEventPublisher implements FlowEventPublisher, InitializingBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.TOPIC;

    private Producer producer;

    @Override
    public void afterPropertiesSet() throws ClientException {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        this.producer = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setTopics(topic)
                .build();

    }

    @Override
    public void publishProcessUpdate(Long processId) {
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                .setBody(FlowEvent.encode(FlowEvent.UPDATE, processId))
                .build();
        try {
            SendReceipt sendReceipt = producer.send(message);
            log.info("发送publishProcessUpdate RocketMQ消息成功：{}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void publishProcessDelete(Long processId) {
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                .setBody(FlowEvent.encode(FlowEvent.DELETE, processId))
                .build();
        try {
            SendReceipt sendReceipt = producer.send(message);
            log.info("发送publishProcessDelete RocketMQ消息成功：{}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

}
