package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import jakarta.annotation.Resource;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;

import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 * 使用 WfFlowTaskDo 直接实现
 *
 * @author liyang
 * @date 2025-01-27
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTask> implements FlowHisTaskDao<FlowHisTask> {

    @Resource
    private FlowHisTaskRepository flowHisTaskRepository;

    @Override
    public DataRepository<FlowHisTask> getRepository() {
        return flowHisTaskRepository;
    }

    @Override
    public List<FlowHisTask> getNoReject(Long instanceId) {
        // todo 待处理
        return null;
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        // todo 待处理
        return null;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        // todo 待处理
        return 0;
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        // todo 待处理
        return null;
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }
}
