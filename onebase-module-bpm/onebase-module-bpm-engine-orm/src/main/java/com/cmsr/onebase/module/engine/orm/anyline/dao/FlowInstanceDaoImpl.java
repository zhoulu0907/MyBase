package com.cmsr.onebase.module.engine.orm.anyline.dao;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.engine.orm.anyline.entity.FlowInstance;
import com.cmsr.onebase.module.engine.orm.anyline.repository.FlowInstanceRepository;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;

import java.util.List;

/**
 * WarmFlow 流程实例 DAO 实现
 *
 * @author liyang
 * @date 2025-10-10
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance> implements FlowInstanceDao<FlowInstance> {

    @Resource
    private FlowInstanceRepository flowInstanceRepository;

    @Override
    public DataRepository<FlowInstance> getRepository() {
        return flowInstanceRepository;
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.in(FlowInstance.DEFINITION_ID, defIds);

        return getRepository().findAllByConfig(configStore);
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }
}
