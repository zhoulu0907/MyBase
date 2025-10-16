package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
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
 * @Date：2025/10/10 10:19
 */
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowEventUpdateFlowCache implements MessageListener, ApplicationRunner, DisposableBean {

    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Setter
    @Value("${rocketmq.endpoints}")
    private String endpoints;

    @Setter
    private String topic = RocketMQConstants.CHANGE_EVENTS_TOPIC;

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Setter
    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Setter
    @Autowired
    private GraphFlowCache graphFlowCache;

    private RocketMQSlotManager slotManager;

    private PushConsumer consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        slotManager = new RocketMQSlotManager();
        slotManager.setSlotKey(RocketMQConstants.CHANGE_EVENTS_CONSUMER_GROUP_SLOT);
        slotManager.setRedissonClient(redissonClient);
        slotManager.afterPropertiesSet();
        Integer slot = slotManager.getSlot();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
        String consumerGroup = RocketMQConstants.CHANGE_EVENTS_CONSUMER_GROUP_PREFIX + slot;
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
            FlowChangeEvent event = FlowChangeEvent.decode(messageView.getBody());
            if (StringUtils.equalsIgnoreCase(event.getType(), FlowChangeEvent.UPDATE)) {
                onProcessUpdate(event.getProcessId());
            } else if (StringUtils.equalsIgnoreCase(event.getType(), FlowChangeEvent.DELETE)) {
                onProcessDelete(event.getProcessId());
            }
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
        }
    }

    public boolean onProcessUpdate(Long processId) {
        log.info("处理流程更新事件：{}", processId);
        FlowProcessDO processDO = flowProcessRepository.findById(processId);
        if (processDO == null) {
            log.warn("流程不存在：{}", processId);
            return false;
        }
        JsonGraph jsonGraph = JsonGraphBuilder.build(processDO.getProcessDefinition());
        String flowChain = jsonGraph.toFlowChain();
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
        //
        graphFlowCache.update(processDO.getId(), jsonGraph);
        return true;
    }

    public boolean onProcessDelete(Long processId) {
        log.info("发布流程删除事件：{}", processId);
        String chainId = FlowUtils.toFlowChainId(processId);
        FlowBus.removeChain(chainId);
        //
        graphFlowCache.delete(processId);
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }
}
