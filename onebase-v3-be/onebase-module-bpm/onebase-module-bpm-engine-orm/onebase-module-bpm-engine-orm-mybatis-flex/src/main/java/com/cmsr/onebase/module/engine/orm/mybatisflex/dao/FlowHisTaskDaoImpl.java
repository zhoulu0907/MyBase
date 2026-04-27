package com.cmsr.onebase.module.engine.orm.mybatisflex.dao;

import com.cmsr.onebase.module.engine.orm.mybatisflex.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.mybatisflex.mapper.FlowHisTaskMapper;
import com.cmsr.onebase.module.engine.orm.mybatisflex.repository.FlowHisTaskRepository;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.utils.CollUtil;

import java.util.Arrays;
import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTaskMapper, FlowHisTask> implements FlowHisTaskDao<FlowHisTask> {
    @Resource
    private FlowHisTaskRepository flowHisTaskRepository;

    @Override
    public ServiceImpl<FlowHisTaskMapper, FlowHisTask> getRepository() {
        return flowHisTaskRepository;
    }

    @Override
    public List<FlowHisTask> getNoReject(Long instanceId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId);
        queryWrapper.eq(FlowHisTask::getSkipType, SkipType.PASS.getKey());
        queryWrapper.orderBy(FlowHisTask::getCreateTime, false);

        return getRepository().list(queryWrapper);
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId);
        queryWrapper.in(FlowHisTask::getNodeCode, nodeCodes, CollUtil.isNotEmpty(nodeCodes));
        queryWrapper.orderBy(FlowHisTask::getCreateTime, false);

        return getRepository().list(queryWrapper);
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowHisTask::getInstanceId, instanceIds);

        return getRepository().remove(queryWrapper) ? 1 : 0;
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(FlowHisTask::getTaskId, taskId);
        queryWrapper.in(FlowHisTask::getCooperateType, Arrays.asList(cooperateTypes));

        return getRepository().list(queryWrapper);
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }
}
