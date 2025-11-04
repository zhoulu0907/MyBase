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
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/3 13:25
 */
@Slf4j
@Service
public class FlowVersionUpdate {

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

    public void updateApplicationVersion(Long applicationId) {
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
        RMap<Long, Long> rMap = redissonClient.getMap(FlowUtils.REDIS_APPLICATION_VERSION_KEY);
        rMap.put(applicationId, System.currentTimeMillis());
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
}
