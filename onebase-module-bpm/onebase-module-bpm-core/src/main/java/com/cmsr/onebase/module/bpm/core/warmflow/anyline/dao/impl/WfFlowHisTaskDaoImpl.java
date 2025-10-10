package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dao.impl;

import com.cmsr.onebase.module.bpm.core.warmflow.anyline.repository.FlowHisTaskRepository;
import com.cmsr.onebase.module.bpm.core.warmflow.anyline.entity.WfFlowHisTask;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.utils.page.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 * 使用 WfFlowTaskDo 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class WfFlowHisTaskDaoImpl implements FlowHisTaskDao<WfFlowHisTask> {

    @Resource
    private FlowHisTaskRepository flowHisTaskRepository;

    @Override
    public List<WfFlowHisTask> getNoReject(Long instanceId) {
        return null;
    }

    @Override
    public List<WfFlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return null;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return 0;
    }

    @Override
    public List<WfFlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        return null;
    }

    @Override
    public WfFlowHisTask newEntity() {
        return null;
    }

    @Override
    public WfFlowHisTask selectById(Serializable id) {
        return null;
    }

    @Override
    public List<WfFlowHisTask> selectByIds(Collection<? extends Serializable> ids) {
        return null;
    }

    @Override
    public Page<WfFlowHisTask> selectPage(WfFlowHisTask entity, Page<WfFlowHisTask> page) {
        return null;
    }

    @Override
    public List<WfFlowHisTask> selectList(WfFlowHisTask entity, WarmQuery<WfFlowHisTask> query) {
        return null;
    }

    @Override
    public long selectCount(WfFlowHisTask entity) {
        return 0;
    }

    @Override
    public int save(WfFlowHisTask entity) {
        return 0;
    }

    @Override
    public int updateById(WfFlowHisTask entity) {
        return 0;
    }

    @Override
    public int delete(WfFlowHisTask entity) {
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
        return 0;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        return 0;
    }

    @Override
    public void saveBatch(List<WfFlowHisTask> list) {

    }

    @Override
    public void updateBatch(List<WfFlowHisTask> list) {

    }
}
