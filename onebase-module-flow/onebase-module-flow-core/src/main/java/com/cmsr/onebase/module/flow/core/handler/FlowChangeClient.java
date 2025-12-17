package com.cmsr.onebase.module.flow.core.handler;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.enums.FlowJobStatusEnum;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/11/3 13:25
 */
@Slf4j
@Service
public class FlowChangeClient {

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Setter
    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Setter
    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;


    public void applicationUpdate(Long applicationId) {
        log.info("更新应用版本：{}", applicationId);
        resetScheduleJobStatus(applicationId);
        FlowChangeEvent flowChangeEvent = new FlowChangeEvent();
        flowChangeEvent.setEventType(FlowChangeEvent.UPDATE_EVENT);
        flowChangeEvent.setApplicationId(applicationId);
        flowChangeEvent.setVersion(System.currentTimeMillis());
        //记录缓存
        RMapCache<Long, FlowChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
        mapCache.put(applicationId, flowChangeEvent, FlowUtils.VERSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        //发送消息
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        topic.publish(flowChangeEvent);
    }

    public void applicationDelete(Long applicationId) {
        FlowChangeEvent flowChangeEvent = new FlowChangeEvent();
        flowChangeEvent.setEventType(FlowChangeEvent.DELETE_EVENT);
        flowChangeEvent.setApplicationId(applicationId);
        flowChangeEvent.setVersion(-1L);
        //
        RMapCache<Long, FlowChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY);
        mapCache.put(applicationId, flowChangeEvent, FlowUtils.VERSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        topic.publish(flowChangeEvent);
    }

    public void resetScheduleJobStatus(Long applicationId) {
        flowProcessTimeRepository.updateJobStatusByAppId(FlowJobStatusEnum.NEED_DEPLOY.getStatus(), applicationId);
        flowProcessDateFieldRepository.updateJobStatusByAppId(FlowJobStatusEnum.NEED_DEPLOY.getStatus(), applicationId);
    }


}
