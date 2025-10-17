package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowHisTask;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowHisTaskRepository;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.utils.CollUtil;

import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
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
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FlowHisTask.INSTANCE_ID, instanceId);
        configStore.eq(FlowHisTask.SKIP_TYPE, SkipType.PASS.getKey());

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FlowHisTask.INSTANCE_ID, instanceId);

        if (CollUtil.isNotEmpty(nodeCodes)) {
            configStore.in(FlowHisTask.NODE_CODE, nodeCodes);
        }

        configStore.order(FlowHisTask.CREATE_TIME, Order.TYPE.DESC);

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowHisTask.INSTANCE_ID, instanceIds);
        return (int)getRepository().deleteByConfig(configStore);
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FlowHisTask.TASK_ID, taskId);
        configStore.in(FlowHisTask.COOPERATE_TYPE, cooperateTypes);

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }
}
