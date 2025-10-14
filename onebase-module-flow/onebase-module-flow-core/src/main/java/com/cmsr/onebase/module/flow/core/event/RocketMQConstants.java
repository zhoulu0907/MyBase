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
     * 运行时定时任务topic，定时任务执行消息
     */
    public static final String TIME_TOPIC = "flow-process-time-topic";

    /**
     * 流程事件consumer group前缀
     */
    public static final String CONSUMER_GROUP_EVENT_PREFIX = "flow-process-event-";

    public static final String CONSUMER_GROUP_EVENT_JOB = "flow-process-event-job";
    /**
     * 运行时定时任务consumer group
     */
    public static final String CONSUMER_GROUP_TIME_JOB_NORM = "flow-process-time-job-norm";

    public static final String CONSUMER_GROUP_TIME_JOB_FLD = "flow-process-time-job-fld";
    /**
     * 流程事件slot key
     */
    public static final String EVENT_TOPIC_SLOT = "flow:process:consumer:group:event";

    /**
     * 定时任务slot key
     */
    public static final String TIME_TOPIC_SLOT = "flow:process:consumer:group:time";

    /**
     * 正常时间消息tag
     */
    public static final String NORMAL_TIME_MESSAGE_TAG = "norm";

    /**
     * 字段时间消息tag
     */
    public static final String FIELD_TIME_MESSAGE_TAG = "fld";
}
