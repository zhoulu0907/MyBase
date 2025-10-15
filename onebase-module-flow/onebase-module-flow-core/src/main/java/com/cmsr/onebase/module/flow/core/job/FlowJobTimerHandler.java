package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.event.RocketMQConstants;
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
public class FlowJobTimerHandler implements MessageListener, ApplicationRunner, DisposableBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    private String topic = RocketMQConstants.JOB_EVENTS_TOPIC_TIMER;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        String consumerGroup = RocketMQConstants.CONSUMER_GROUP_DEFAULT;
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
            FlowJobMessage message = FlowJobMessage.decode(messageView.getBody());
            log.info("FlowProcessDateFieldJob receive message: {}", message);
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
