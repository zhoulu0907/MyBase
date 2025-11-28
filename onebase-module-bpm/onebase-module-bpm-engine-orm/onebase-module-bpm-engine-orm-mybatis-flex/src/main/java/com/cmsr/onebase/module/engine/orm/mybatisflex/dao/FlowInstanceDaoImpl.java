package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;


import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowInstanceMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowInstanceRepository;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;

import java.util.List;

/**
 * WarmFlow 流程实例 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstanceMapper, FlowInstance> implements FlowInstanceDao<FlowInstance> {
    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Override
    public ServiceImpl<FlowInstanceMapper, FlowInstance> getRepository() {
        return flowInstanceRepository;
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowInstance::getDefinitionId, defIds);

        return getRepository().list(queryWrapper);
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }
}
