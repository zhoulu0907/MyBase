package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowProcessNormTimeJob implements MessageListener, ApplicationRunner, DisposableBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.TIME_TOPIC;

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    private RocketMQSlotManager slotManager;

    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        slotManager = new RocketMQSlotManager();
        slotManager.setSlotKey(RocketMQConstants.TIME_TOPIC_SLOT);
        slotManager.setRedissonClient(redissonClient);
        slotManager.afterPropertiesSet();
        Integer slot = slotManager.getSlot();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        String consumerGroup = RocketMQConstants.CONSUMER_GROUP_TIME_PREFIX + slot;
        FilterExpression filterExpression = new FilterExpression(RocketMQConstants.NORMAL_TIME_MESSAGE_TAG);
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
            FlowMessage message = FlowMessage.decode(messageView.getBody());
            log.info("FlowProcessNormTimeJob receive message: {}", message);
            ExecutorResult executorResult = flowProcessExecutor.execute(message.getProcessId(), Collections.emptyMap());
            log.error("执行流程结果：{}", executorResult);
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
        }
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }


}
