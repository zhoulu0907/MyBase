package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import com.cmsr.onebase.module.bpm.core.dal.mapper.BpmFlowAgentInsMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BPM流程代理实例数据访问层
 *
 * @author liyang
 * @date 2025-11-22
 */
@Repository
public class BpmFlowAgentInsRepository extends ServiceImpl<BpmFlowAgentInsMapper, BpmFlowAgentInsDO> {
    public List<BpmFlowAgentInsDO> findAllByTaskIdAndAgentId(Long taskId, String agentId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BpmFlowAgentInsDO::getTaskId, taskId);
        queryWrapper.eq(BpmFlowAgentInsDO::getAgentId, agentId);

        return list(queryWrapper);
    }

    public List<BpmFlowAgentInsDO> findAllByTaskIdsAndAgentId(List<Long> taskIds, String agentId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(BpmFlowAgentInsDO::getTaskId, taskIds);
        queryWrapper.eq(BpmFlowAgentInsDO::getAgentId, agentId);

        return list(queryWrapper);
    }

    public List<BpmFlowAgentInsDO> findAllByInstanceId(Long instanceId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BpmFlowAgentInsDO::getInstanceId, instanceId);

        return list(queryWrapper);
    }
}
