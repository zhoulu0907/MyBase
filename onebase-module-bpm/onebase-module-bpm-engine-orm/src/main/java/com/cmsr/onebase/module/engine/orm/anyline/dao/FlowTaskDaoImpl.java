package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowTask;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowTaskRepository;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;

import java.util.List;

/**
 * WarmFlow 待办任务 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask> implements FlowTaskDao<FlowTask> {

    @Resource
    private FlowTaskRepository flowTaskRepository;

    @Override
    public DataRepository<FlowTask> getRepository() {
        return flowTaskRepository;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowTask.INSTANCE_ID, instanceIds);

        return (int) getRepository().deleteByConfig(configStore);
    }

    @Override
    public List<FlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq(FlowTask.INSTANCE_ID, instanceId);
        configStore.in(FlowTask.NODE_CODE, nodeCodes);

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }
}
