package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowTaskMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowTaskRepository;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;

import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTaskMapper, FlowTask> implements FlowTaskDao<FlowTask> {
    @Resource
    private FlowTaskRepository flowTaskRepository;

    @Override
    public ServiceImpl<FlowTaskMapper, FlowTask> getRepository() {
        return flowTaskRepository;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowTask::getInstanceId, instanceIds);

        return getRepository().remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public List<FlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowTask::getInstanceId, instanceId);
        queryWrapper.in(FlowTask::getNodeCode, nodeCodes);

        return getRepository().list(queryWrapper);
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }
}
