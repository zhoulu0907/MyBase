package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.module.flow.core.enums.RocketMQConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:18
 */
@Slf4j
@Component
public class FlowChangeEventPublisherImpl implements FlowChangeEventPublisher, InitializingBean, DisposableBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.CHANGE_EVENTS_TOPIC;

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
    public void publishApplicationUpdate(Long applicationId) {
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                .setBody(FlowChangeEvent.encode(FlowChangeEvent.UPDATE, applicationId))
                .build();
        try {
            SendReceipt sendReceipt = producer.send(message);
            log.info("发送应用更新MQ消息成功：{}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void publishApplicationDelete(Long applicationId) {
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                .setBody(FlowChangeEvent.encode(FlowChangeEvent.DELETE, applicationId))
                .build();
        try {
            SendReceipt sendReceipt = producer.send(message);
            log.info("发送应用删除MQ消息成功：{}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            producer.close();
        }
    }
}
