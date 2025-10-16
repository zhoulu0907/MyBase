package com.cmsr.onebase.module.flow.core.event;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.RocketMQConstants;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.JsonGraphBuilder;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
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
import java.util.List;
import java.util.Set;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:19
 */
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowChangeEventCacheHandler implements MessageListener, ApplicationRunner, DisposableBean {

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
        initAllProcess();
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

    private void initAllProcess() {
        //TODO 这里要用 TenantUtils.executeIgnore 去查询，但这个没有拆分出来，会导致依赖问题。
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findAllByEnableStatus(FlowEnableStatusEnum.ENABLE.getStatus());
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            try {
                onProcessUpdate(flowProcessDO);
                log.info("加载flowProcess流程成功：{}", flowProcessDO.getId());
            } catch (Exception e) {
                log.error("初始化flowProcessDO异常：{}, {}", flowProcessDO, e.getMessage(), e);
            }
        }
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        try {
            FlowChangeEvent event = FlowChangeEvent.decode(messageView.getBody());
            Long applicationId = event.getApplicationId();
            if (event.getType().equals(FlowChangeEvent.UPDATE)) {
                onApplicationChange(applicationId);
            }
            if (event.getType().equals(FlowChangeEvent.DELETE)) {
                onApplicationDelete(applicationId);
            }
            return ConsumeResult.SUCCESS;
        } catch (Exception e) {
            log.error("处理RocketMQ消息异常：{}", e.getMessage(), e);
            return ConsumeResult.FAILURE;
        }
    }

    public String onApplicationDelete(Long applicationId) {
        Set<Long> ids = graphFlowCache.findFlowByApplicationId(applicationId);
        graphFlowCache.delete(applicationId);
        return "删除：" + ids;
    }

    public String onApplicationChange(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus());
        Set<Long> oldProcessIds = graphFlowCache.findFlowByApplicationId(applicationId);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            oldProcessIds.remove(flowProcessDO.getId());
        }
        for (Long processId : oldProcessIds) {
            onProcessDelete(applicationId, processId);
        }
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            onProcessUpdate(flowProcessDO);
        }
        return "删除：" + oldProcessIds + "，添加：" + flowProcessDOS.stream().map(FlowProcessDO::getId).toList();
    }

    private void onProcessUpdate(FlowProcessDO processDO) {
        log.info("处理流程更新事件：{}", processDO.getId());
        JsonGraph jsonGraph = JsonGraphBuilder.build(processDO.getProcessDefinition());
        String flowChain = jsonGraph.toFlowChain();
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
        //
        graphFlowCache.update(processDO.getApplicationId(), processDO.getId(), jsonGraph);
    }

    private void onProcessDelete(Long applicationId, Long processId) {
        log.info("发布流程删除事件：{}", processId);
        String chainId = FlowUtils.toFlowChainId(processId);
        FlowBus.removeChain(chainId);
        //
        graphFlowCache.delete(applicationId, processId);
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }
}
