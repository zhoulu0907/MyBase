package com.cmsr.onebase.module.flow.core.event.redis;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.event.FlowEventHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Redis 流程图事件服务实现
 * 通过 Redis 发布订阅机制实现分布式流程事件通知
 *
 * @author huangjie
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class RedisFlowEventHandler extends FlowEventHandler {

    /**
     * Redis Topic 常量定义
     */
    private static final String TOPIC_PROCESS_UPDATE = "flow:process:update";
    private static final String TOPIC_PROCESS_DELETE = "flow:process:delete";

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 初始化 Redis 消息监听器
     */
    public void initListeners() {
        // 监听流程更新事件
        RTopic updateTopic = redissonClient.getTopic(TOPIC_PROCESS_UPDATE);
        updateTopic.addListener(Long.class, new MessageListener<Long>() {
            @Override
            public void onMessage(CharSequence channel, Long processId) {
                log.debug("接收到流程更新事件，Topic: {}, ProcessId: {}", channel, processId);
                onProcessUpdate(processId);
            }
        });
        // 监听流程删除事件
        RTopic deleteTopic = redissonClient.getTopic(TOPIC_PROCESS_DELETE);
        deleteTopic.addListener(Long.class, new MessageListener<Long>() {
            @Override
            public void onMessage(CharSequence channel, Long processId) {
                log.debug("接收到流程删除事件，Topic: {}, ProcessId: {}", channel, processId);
                onProcessDelete(processId);
            }
        });
        log.info("Redis 流程事件监听器初始化完成");
    }


}
