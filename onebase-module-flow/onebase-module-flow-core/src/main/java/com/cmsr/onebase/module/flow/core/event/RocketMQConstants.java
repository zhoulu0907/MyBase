package com.cmsr.onebase.module.flow.core.event;

/**
 * @Author：huangjie
 * @Date：2025/10/11 10:26
 */
public class RocketMQConstants {

    /**
     * 流程事件topic，流程上线、下线等事件
     */
    public static final String EVENT_TOPIC = "flow-process-event-topic";

    /**
     * 处理缓存更新的消费组，每个进程一个消费组
     */
    public static final String CONSUMER_GROUP_EVENT_PREFIX = "flow-process-event-";

    /**
     * 处理定时任务的消费者，只需要一个全局的消费组
     */
    public static final String CONSUMER_GROUP_EVENT_JOB = "flow-process-event-job";

    /**
     * 流程事件slot key
     */
    public static final String EVENT_TOPIC_SLOT = "flow:process:consumer:group:event";

}
