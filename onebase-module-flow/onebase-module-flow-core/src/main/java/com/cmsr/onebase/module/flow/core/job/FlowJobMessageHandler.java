package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.enums.RocketMQConstants;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowJobMessageHandler implements MessageListener, ApplicationRunner, DisposableBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    private String topic = RocketMQConstants.JOB_EVENTS_TOPIC;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private RedisTemplate redisTemplate;

    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.redisTemplate = new RedisTemplate();
        this.redisTemplate.setConnectionFactory(redisConnectionFactory);
        this.redisTemplate.setDefaultSerializer(RedisSerializer.string());
        this.redisTemplate.afterPropertiesSet();
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
        FlowJobMessage message = FlowJobMessage.decode(messageView.getBody());
        String result;
        try {
            Map<String, Object> inputParams;
            if (message.getMsgType().equals("fld")) {
                inputParams = createDateFieldInputParams(message);
            } else {
                inputParams = createTimeInputParams(message);
            }
            log.info("接受到MQ消息: {}", message);
            ExecutorResult executorResult = flowProcessExecutor.execute(message.getProcessId(), inputParams);
            log.error("执行流程结果：{}", executorResult);
            result = executorResult.isSuccess() ? "success:" : "fail:";
            result = result + JsonUtils.toJsonString(executorResult);
        } catch (Exception e) {
            log.error("处理MQ消息异常：{}", e.getMessage(), e);
            result = "fail:" + ExceptionUtils.getRootCauseMessage(e);
        }
        redisTemplate.opsForValue().set(RocketMQConstants.JOB_EVENTS_RESULT + message.getUuid(), result, 120, TimeUnit.SECONDS);
        return ConsumeResult.SUCCESS;
    }

    private Map<String, Object> createDateFieldInputParams(FlowJobMessage message) {
        Long processId = message.getProcessId();
        StartDateFieldNodeData startDateFieldNodeData = graphFlowCache.findStartDateFieldNodeDataByProcessId(processId);
        if (startDateFieldNodeData == null) {
            throw new RuntimeException("实体时间字段触发流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> createTimeInputParams(FlowJobMessage message) {
        Long processId = message.getProcessId();
        StartTimeNodeData startTimeNodeData = graphFlowCache.findStartTimeNodeDataByProcessId(processId);
        if (startTimeNodeData == null) {
            throw new RuntimeException("定时任务流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }
}
