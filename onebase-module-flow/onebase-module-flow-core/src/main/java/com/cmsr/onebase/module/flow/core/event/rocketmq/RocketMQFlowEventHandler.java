package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.event.FlowEvent;
import com.cmsr.onebase.module.flow.core.event.FlowEventHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.util.Collections;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:19
 */
@Slf4j
public class RocketMQFlowEventHandler extends FlowEventHandler implements MessageListener {

    private final String CONSUMER_GROUP_PREFIX = "flow-process-consumer-group-";

    final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    private String endpoints;

    @Setter
    private String topic = "flow-process-event-topic";

    @Setter
    private Integer processId;

    private PushConsumer consumer;

    public void init() throws ClientException {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        String consumerGroup = CONSUMER_GROUP_PREFIX + processId;
        FilterExpression filterExpression = new FilterExpression();
        this.consumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setConsumerGroup(consumerGroup)
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .setMessageListener(this)
                .build();
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        try {
            FlowEvent event = FlowEvent.decode(messageView.getBody());
            switch (event.getType()) {
                case FlowEvent.UPDATE:
                    onProcessUpdate(event.getProcessId());
                    break;
                case FlowEvent.DELETE:
                    onProcessDelete(event.getProcessId());
                    break;
            }
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
        }
    }
}
