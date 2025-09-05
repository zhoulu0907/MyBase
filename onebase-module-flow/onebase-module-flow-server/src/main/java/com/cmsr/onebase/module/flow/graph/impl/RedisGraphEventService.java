package com.cmsr.onebase.module.flow.graph.impl;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cmsr.onebase.module.flow.graph.GraphEventService;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;

/**
 * Redis 流程图事件服务实现
 * 通过 Redis 发布订阅机制实现分布式流程事件通知
 * 
 * @author huangjie
 */
@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "onebase.flow.event", 
    name = "type", 
    havingValue = "redis"
)
public class RedisGraphEventService extends GraphEventService {

    /**
     * Redis Topic 常量定义
     */
    private static final String TOPIC_PROCESS_ADD = "flow:process:add";
    private static final String TOPIC_PROCESS_DELETE = "flow:process:delete";
    private static final String TOPIC_PROCESS_UPDATE = "flow:process:update";

    @Autowired
    private Redisson redisson;

    /**
     * 初始化 Redis 消息监听器
     */
    @PostConstruct
    public void initListeners() {
        // 监听流程添加事件
        RTopic addTopic = redisson.getTopic(TOPIC_PROCESS_ADD);
        addTopic.addListener(Long.class, new MessageListener<Long>() {
            @Override
            public void onMessage(CharSequence channel, Long processId) {
                log.debug("接收到流程添加事件，Topic: {}, ProcessId: {}", channel, processId);
                onProcessAdd(processId);
            }
        });

        // 监听流程删除事件
        RTopic deleteTopic = redisson.getTopic(TOPIC_PROCESS_DELETE);
        deleteTopic.addListener(Long.class, new MessageListener<Long>() {
            @Override
            public void onMessage(CharSequence channel, Long processId) {
                log.debug("接收到流程删除事件，Topic: {}, ProcessId: {}", channel, processId);
                onProcessDelete(processId);
            }
        });

        // 监听流程更新事件
        RTopic updateTopic = redisson.getTopic(TOPIC_PROCESS_UPDATE);
        updateTopic.addListener(Long.class, new MessageListener<Long>() {
            @Override
            public void onMessage(CharSequence channel, Long processId) {
                log.debug("接收到流程更新事件，Topic: {}, ProcessId: {}", channel, processId);
                onProcessUpdate(processId);
            }
        });
        log.info("Redis 流程事件监听器初始化完成");
    }

    @Override
    public void publishProcessAdd(Long processId) {
        log.info("发布流程添加事件到 Redis，ProcessId: {}", processId);
        RTopic topic = redisson.getTopic(TOPIC_PROCESS_ADD);
        topic.publish(processId);
    }

    @Override
    public void publishProcessDelete(Long processId) {
        log.info("发布流程删除事件到 Redis，ProcessId: {}", processId);
        RTopic topic = redisson.getTopic(TOPIC_PROCESS_DELETE);
        topic.publish(processId);
    }

    @Override
    public void publishProcessUpdate(Long processId) {
        log.info("发布流程更新事件到 Redis，ProcessId: {}", processId);
        RTopic topic = redisson.getTopic(TOPIC_PROCESS_UPDATE);
        topic.publish(processId);
    }

}
