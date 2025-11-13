package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowJobStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/11/3 13:25
 */
@Slf4j
@Service
public class FlowCacheClient {

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    @Setter
    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Setter
    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Setter
    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    public void applicationUpdate(Long applicationId) {
        log.info("更新应用版本：{}", applicationId);
        List<FlowProcessDO> flowProcessDOS = flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus());
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
                updateTimeJob(flowProcessDO);
            }
            if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
                updateDateFieldJob(flowProcessDO);
            }
        }
        ChangeEvent changeEvent = new ChangeEvent();
        changeEvent.setEventType(ChangeEvent.UPDATE_EVENT);
        changeEvent.setApplicationId(applicationId);
        changeEvent.setVersion(System.currentTimeMillis());
        //记录缓存
        RMapCache<Long, ChangeEvent> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY, FlowUtils.KRYO5_CODEC);
        mapCache.put(applicationId, changeEvent, FlowUtils.VERSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        //发送消息
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        topic.publish(changeEvent);
    }

    private void updateTimeJob(FlowProcessDO flowProcessDO) {
        FlowProcessTimeDO flowProcessTimeDO = flowProcessTimeRepository.findByProcessId(flowProcessDO.getId());
        if (flowProcessTimeDO != null) {
            flowProcessTimeDO.setJobStatus(FlowJobStatusEnum.NEED_DEPLOY.getStatus());
            flowProcessTimeRepository.update(flowProcessTimeDO);
        }
    }

    private void updateDateFieldJob(FlowProcessDO flowProcessDO) {
        FlowProcessDateFieldDO flowProcessDateFieldDO = flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId());
        if (flowProcessDateFieldDO != null) {
            flowProcessDateFieldDO.setJobStatus(FlowJobStatusEnum.NEED_DEPLOY.getStatus());
            flowProcessDateFieldRepository.update(flowProcessDateFieldDO);
        }
    }

    public void applicationDelete(Long applicationId) {
        RMapCache<Long, Long> mapCache = redissonClient.getMapCache(FlowUtils.REDIS_VERSION_CHANGE_CACHE_KEY);
        mapCache.put(applicationId, -1L, FlowUtils.VERSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        RTopic topic = redissonClient.getTopic(FlowUtils.REDIS_VERSION_CHANGE_TOPIC_KEY);
        ChangeEvent changeEvent = new ChangeEvent();
        changeEvent.setEventType(ChangeEvent.DELETE_EVENT);
        changeEvent.setApplicationId(applicationId);
        changeEvent.setVersion(-1L);
        topic.publish(changeEvent);
    }

}
