package com.cmsr.onebase.module.flow.core.event.redis;

import com.cmsr.onebase.module.flow.core.event.FlowEventPublisher;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RedisFlowEventPublisher implements FlowEventPublisher {

    /**
     * Redis Topic 常量定义
     */
    private static final String TOPIC_PROCESS_UPDATE = "flow:process:update";
    private static final String TOPIC_PROCESS_DELETE = "flow:process:delete";

    @Autowired
    private RedissonClient redissonClient;

    public void publishProcessUpdate(Long processId) {
        log.info("发布流程更新事件到 Redis，ProcessId: {}", processId);
        RTopic topic = redissonClient.getTopic(TOPIC_PROCESS_UPDATE);
        topic.publish(processId);
    }

    public void publishProcessDelete(Long processId) {
        log.info("发布流程删除事件到 Redis，ProcessId: {}", processId);
        RTopic topic = redissonClient.getTopic(TOPIC_PROCESS_DELETE);
        topic.publish(processId);
    }


}
