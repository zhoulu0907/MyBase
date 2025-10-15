package com.cmsr.onebase.module.flow.core.event;

/**
 * @Author：huangjie
 * @Date：2025/10/11 10:26
 */
public class RocketMQConstants {

    /**
     * 流程事件topic，流程上线、下线等事件
     */
    public static final String CHANGE_EVENTS_TOPIC = "flow_process_change_events";

    /**
     * 处理缓存更新的消费组，每个进程一个消费组
     */
    public static final String CHANGE_EVENTS_CONSUMER_GROUP_PREFIX = "group_";

    /**
     * 处理定时任务的消费者，只需要一个全局的消费组
     */
    public static final String CHANGE_EVENTS_CONSUMER_GROUP_JOB = "group_job";

    /**
     * 流程事件slot key
     */
    public static final String CHANGE_EVENTS_CONSUMER_GROUP_SLOT = "flow:process:consumer:group:change:events";


    public static final String JOB_EVENTS_TOPIC_TIMER = "flow_process_job_events_timer";

    public static final String JOB_EVENTS_TOPIC_FLD = "flow_process_job_events_fld";


    public static final String CONSUMER_GROUP_DEFAULT = "group_default";


}
