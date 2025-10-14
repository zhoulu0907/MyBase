package com.cmsr.onebase.module.flow.core.event;

/**
 * @Author：huangjie
 * @Date：2025/10/11 10:26
 */
public class RocketMQConstants {

    public static final String EVENT_TOPIC = "flow-process-event-topic";

    public static final String TIME_TOPIC = "flow-process-time-topic";

    public static final String CONSUMER_GROUP_EVENT_PREFIX = "flow-process-event-";

    public static final String CONSUMER_GROUP_TIME_PREFIX = "flow-process-time-";

    public static final String EVENT_TOPIC_SLOT = "flow:process:consumer:group:event";

    public static final String TIME_TOPIC_SLOT = "flow:process:consumer:group:time";

    public static final String NORMAL_TIME_MESSAGE_TAG = "norm";

    public static final String FIELD_TIME_MESSAGE_TAG = "fld";
}
