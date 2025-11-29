package com.cmsr.onebase.module.bpm.core.dal.database;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowInsBizExtDO;
import com.cmsr.onebase.module.bpm.core.dal.mapper.BpmFlowInsBizExtMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * BPM流程实例扩展数据访问层
 *
 * @author liyang
 * @date 2025-10-28
 */
@Repository
public class BpmFlowInsBizExtRepository extends ServiceImpl<BpmFlowInsBizExtMapper, BpmFlowInsBizExtDO> {

    public BpmFlowInsBizExtDO findOneByInstanceId(Long instanceId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BpmFlowInsBizExtDO::getInstanceId, instanceId);
        return getOne(queryWrapper);
    }
}