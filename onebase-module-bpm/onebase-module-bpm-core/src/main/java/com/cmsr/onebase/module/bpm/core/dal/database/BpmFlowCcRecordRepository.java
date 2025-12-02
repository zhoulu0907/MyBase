package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowCcRecordDO;
import com.cmsr.onebase.module.bpm.core.dal.mapper.BpmFlowCcRecordMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BpmFlowCcRecordRepository extends ServiceImpl<BpmFlowCcRecordMapper, BpmFlowCcRecordDO> {
    public List<BpmFlowCcRecordDO> findAllByInstanceId(Long instanceId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BpmFlowCcRecordDO::getInstanceId, instanceId);

        return list(queryWrapper);
    }

    public BpmFlowCcRecordDO findOneByTaskIdAndUserIds(Long taskId, List<String> userIds){
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BpmFlowCcRecordDO::getTaskId, taskId);

        queryWrapper.in(BpmFlowCcRecordDO::getUserId, userIds);

        return getOne(queryWrapper);
    }

    public BpmFlowCcRecordDO findOneByTaskIdAndUserId(Long taskId, String userId){
        return findOneByTaskIdAndUserIds(taskId, List.of(userId));
    }
}
