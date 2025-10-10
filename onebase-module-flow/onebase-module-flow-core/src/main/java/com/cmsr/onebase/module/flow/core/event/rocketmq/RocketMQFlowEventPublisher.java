package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.event.FlowEvent;
import com.cmsr.onebase.module.flow.core.event.FlowEventPublisher;
import lombok.Setter;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:18
 */
public class RocketMQFlowEventPublisher implements FlowEventPublisher {

    private ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    private String endpoints;

    @Setter
    private String topic = "flow-process-event-topic";

    private Producer producer;

    public void init() throws ClientException {
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
            producer.send(message);
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
            producer.send(message);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

}
