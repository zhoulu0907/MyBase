package com.cmsr.onebase.module.flow.core.event.rocketmq;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.event.FlowEvent;
import com.cmsr.onebase.module.flow.core.event.FlowEventHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:19
 */
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class RocketMQFlowEventHandler implements MessageListener, ApplicationRunner {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.TOPIC;

    @Setter
    @Autowired
    private RocketMQSlotManager slotManager;

    @Setter
    @Autowired
    private FlowEventHandler flowEventHandler;

    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        Integer slot = slotManager.getSlot();
        String consumerGroup = RocketMQConstants.CONSUMER_GROUP_PREFIX + slot;
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
            if (StringUtils.equalsIgnoreCase(event.getType(), FlowEvent.UPDATE)) {
                flowEventHandler.onProcessUpdate(event.getProcessId());
            } else if (StringUtils.equalsIgnoreCase(event.getType(), FlowEvent.DELETE)) {
                flowEventHandler.onProcessDelete(event.getProcessId());
            }
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
        }
    }


}
